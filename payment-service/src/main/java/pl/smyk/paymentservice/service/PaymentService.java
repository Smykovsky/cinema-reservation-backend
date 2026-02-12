package pl.smyk.paymentservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import pl.smyk.common.dto.PaymentCompletedEvent;
import pl.smyk.common.dto.PaymentFailedEvent;
import pl.smyk.common.dto.PaymentRefundedEvent;
import pl.smyk.paymentservice.client.BookingServiceClient;
import pl.smyk.paymentservice.dto.*;
import pl.smyk.paymentservice.exception.InvalidBlikCodeException;
import pl.smyk.paymentservice.exception.PaymentNotFoundException;
import pl.smyk.paymentservice.exception.PaymentValidationException;
import pl.smyk.paymentservice.exception.StripeIntegrationException;
import pl.smyk.paymentservice.kafka.PaymentEventProducer;
import pl.smyk.paymentservice.model.Payment;
import pl.smyk.paymentservice.model.Refund;
import pl.smyk.paymentservice.repository.PaymentRepository;
import pl.smyk.paymentservice.repository.RefundRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentIntentConfirmParams;


@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;
    private final PaymentEventProducer paymentEventProducer;
    private final BookingServiceClient bookingServiceClient;

    private final int MAX_RETRY_COUNT = 12;
    private final int DELAY_MILLIS = 2000;

    public PaymentResponse initializePayment(PaymentInitializationRequest request) {
        BookingDetailsResponse bookingDetails = bookingServiceClient.getBookingDetails(request.getBookingId());

        if (bookingDetails == null) {
            throw new PaymentNotFoundException("Booking not found for ID: " + request.getBookingId());
        }

        if (!"PENDING".equalsIgnoreCase(bookingDetails.getStatus()) && !"CONFIRMED".equalsIgnoreCase(bookingDetails.getStatus())) {
            throw new PaymentValidationException("Payment can only be initialized for PENDING or CONFIRMED bookings.");
        }

        if (bookingDetails.getExpiresAt() != null && bookingDetails.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new PaymentValidationException("Booking has expired.");
        }

        Payment payment = Payment.builder()
                .bookingId(request.getBookingId())
                .bookingNumber(bookingDetails.getBookingNumber())
                .amount(bookingDetails.getTotalAmount())
                .currency("PLN") // Hardcode for now, or get from bookingDetails if available
                .paymentMethod("BLIK") // Hardcode for now
                .status(Payment.PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        payment = paymentRepository.save(payment);
        log.info("Payment initialized for booking ID: {}, Payment ID: {}", payment.getBookingId(), payment.getId());

        try {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("booking_id", payment.getBookingId().toString());
            metadata.put("payment_id", payment.getId().toString()); // Store internal payment ID

            PaymentIntentCreateParams createParams = PaymentIntentCreateParams.builder()
                    .setAmount(payment.getAmount().multiply(new BigDecimal("100")).longValue()) // Amount in cents
                    .setCurrency("pln")
                    .addPaymentMethodType("blik")
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(createParams);

            payment.setProviderTransactionId(paymentIntent.getId()); // Store Stripe PaymentIntent ID
            // Map initial Stripe PaymentIntent status to internal Payment status
            payment.setStatus(Payment.PaymentStatus.PENDING); // Initial status
            paymentRepository.save(payment); // Save again with Stripe ID and initial status

            PaymentResponse response = mapToPaymentResponse(payment);
            response.setClientSecret(paymentIntent.getClientSecret());
            return response;

        } catch (StripeException e) {
            log.error("Error creating PaymentIntent for booking ID {}: {}", payment.getBookingId(), e.getMessage());
            throw new StripeIntegrationException("Error creating payment intent with Stripe.", e);
        }
    }

    public PaymentResponse getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));
        return mapToPaymentResponse(payment);
    }



    public RefundResponse initiateRefund(Long paymentId, RefundRequest request) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));

        if (payment.getStatus() != Payment.PaymentStatus.COMPLETED) {
            throw new PaymentValidationException("Only completed payments can be refunded.");
        }
        if (payment.getAmount().compareTo(request.getAmount()) < 0) {
            throw new PaymentValidationException("Refund amount exceeds original payment amount.");
        }

        try {
            // Create Stripe Refund
            Map<String, Object> params = new HashMap<>();
            params.put("payment_intent", payment.getProviderTransactionId());
            params.put("amount", request.getAmount().multiply(new BigDecimal("100")).longValue()); // Amount in cents

            com.stripe.model.Refund stripeRefund = com.stripe.model.Refund.create(params);

            Refund refund = Refund.builder()
                    .payment(payment)
                    .amount(request.getAmount())
                    .providerRefundId(stripeRefund.getId()) // Store Stripe Refund ID
                    .build();
            // Map Stripe refund status to internal status
            switch (stripeRefund.getStatus()) {
                case "succeeded":
                    refund.setStatus(Refund.RefundStatus.COMPLETED);
                    payment.setStatus(Payment.PaymentStatus.REFUNDED); // Mark original payment as refunded
                    paymentRepository.save(payment);
                    log.info("Refund initiated and completed for Payment ID: {}, Refund ID: {}, Stripe Refund ID: {}", paymentId, refund.getId(), stripeRefund.getId());

                    paymentEventProducer.sendPaymentRefundedEvent(PaymentRefundedEvent.builder()
                            .bookingId(payment.getBookingId())
                            .bookingNumber(payment.getBookingNumber())
                            .amount(refund.getAmount())
                            .currency(payment.getCurrency())
                            .refundId(refund.getProviderRefundId())
                            .reason(request.getReason() != null ? request.getReason() : "User requested refund")
                            .timestamp(java.time.Instant.now())
                            .build());
                    break;
                case "pending":
                    refund.setStatus(Refund.RefundStatus.PENDING);
                    log.info("Refund initiated and is pending for Payment ID: {}, Refund ID: {}, Stripe Refund ID: {}", paymentId, refund.getId(), stripeRefund.getId());
                    break;
                case "failed":
                    refund.setStatus(Refund.RefundStatus.FAILED);
                    log.warn("Refund initiated and failed for Payment ID: {}, Refund ID: {}, Stripe Refund ID: {}", paymentId, refund.getId(), stripeRefund.getId());
                    break;
                default:
                    refund.setStatus(Refund.RefundStatus.FAILED);
                    log.error("Unknown refund status for Payment ID: {}, Refund ID: {}, Stripe Refund ID: {}", paymentId, refund.getId(), stripeRefund.getId());
                    break;
            }
            refundRepository.save(refund);

            return mapToRefundResponse(refund);

        } catch (StripeException e) {
            log.error("Error creating Stripe Refund for Payment ID {}: {}", paymentId, e.getMessage());
            throw new StripeIntegrationException("Error creating refund with Stripe.", e);
        }
    }


    public BlikConfirmResponse confirmBlikPayment(Long paymentId, BlikConfirmRequest blikConfirmRequest) {
        try {
            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));

            if (!"BLIK".equals(payment.getPaymentMethod())) {
                throw new PaymentValidationException("Payment is not a BLIK payment.");
            }
            if (payment.getStatus() != Payment.PaymentStatus.PENDING) {
                throw new PaymentValidationException("Payment is not in PENDING status.");
            }

            // Validate BLIK code format
            if (!blikConfirmRequest.getBlikCode().matches("\\d{6}")) {
                throw new InvalidBlikCodeException("Invalid BLIK code format. Must be 6 digits.");
            }

            // Retrieve Stripe PaymentIntent using providerTransactionId
            PaymentIntent paymentIntent = PaymentIntent.retrieve(payment.getProviderTransactionId());

            PaymentIntentConfirmParams params = PaymentIntentConfirmParams.builder()
                    .setPaymentMethodData(PaymentIntentConfirmParams.PaymentMethodData.builder().setType(PaymentIntentConfirmParams.PaymentMethodData.Type.BLIK).build())
                    .setPaymentMethodOptions(PaymentIntentConfirmParams.PaymentMethodOptions.builder()
                            .setBlik(PaymentIntentConfirmParams.PaymentMethodOptions.Blik.builder().setCode(blikConfirmRequest.getBlikCode()).build())
                            .build())
                    .build();

            // Confirm the PaymentIntent with the BLIK code
            paymentIntent.confirm(params);

            // Poll for payment status
            String finalStatus = pollPaymentStatus(payment.getProviderTransactionId());
            if ("success".equals(finalStatus)) {
                payment.setStatus(Payment.PaymentStatus.COMPLETED);
                log.info("Payment ID: {} completed successfully after BLIK confirmation. Stripe PI ID: {}", payment.getId(), payment.getProviderTransactionId());
                PaymentCompletedEvent build = PaymentCompletedEvent.builder()
                        .paymentId(payment.getId())
                        .bookingId(payment.getBookingId())
                        .amount(payment.getAmount())
                        .paymentMethod(payment.getPaymentMethod())
                        .providerTransactionId(payment.getProviderTransactionId())
                        .completedAt(LocalDateTime.now())
                        .build();
                paymentEventProducer.sendPaymentCompletedEvent(build);
            } else if ("failed".equals(finalStatus)) {
                payment.setStatus(Payment.PaymentStatus.FAILED);
                log.warn("Payment ID: {} failed after BLIK confirmation. Stripe PI ID: {}", payment.getId(), payment.getProviderTransactionId());
                paymentEventProducer.sendPaymentFailedEvent(PaymentFailedEvent.builder()
                        .bookingId(payment.getBookingId())
                        .bookingNumber(payment.getBookingNumber())
                        .amount(payment.getAmount())
                        .currency(payment.getCurrency())
                        .reason("BLIK payment confirmation failed")
                        .timestamp(java.time.Instant.now())
                        .build());
            } else if ("in_process".equals(finalStatus)) {
                log.info("Payment ID: {} is still in process after BLIK confirmation. Stripe PI ID: {}", payment.getId(), payment.getProviderTransactionId());
            } else {
                payment.setStatus(Payment.PaymentStatus.FAILED);
                log.error("Payment ID: {} encountered an undefined error after BLIK confirmation. Stripe PI ID: {}", payment.getId(), payment.getProviderTransactionId());
                paymentEventProducer.sendPaymentFailedEvent(PaymentFailedEvent.builder()
                        .bookingId(payment.getBookingId())
                        .bookingNumber(payment.getBookingNumber())
                        .amount(payment.getAmount())
                        .currency(payment.getCurrency())
                        .reason("BLIK payment confirmation undefined error")
                        .timestamp(java.time.Instant.now())
                        .build());
            }

            paymentRepository.save(payment);

            return BlikConfirmResponse.builder().status(finalStatus).build();

        } catch (StripeException e) {
            log.error("Error confirming BLIK payment for Payment ID {}: {}", paymentId, e.getMessage());
            throw new StripeIntegrationException("Error confirming BLIK payment with Stripe.", e);
        }
    }

    private String pollPaymentStatus(String paymentIntentId) throws StripeException {
        int retryCount = 0;
        String status;
        PaymentIntent paymentIntent;

        do {
            paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            status = paymentIntent.getStatus();
            log.info("Polling status for PaymentIntent {}: {}", paymentIntentId, status);

            if ("succeeded".equals(status)) {
                return "success";
            } else if ("requires_payment_method".equals(status) || "canceled".equals(status)) {
                return "failed";
            } else if ("processing".equals(status) || "requires_action".equals(status)) {
                // Continue polling
            } else {
                return "undefined_error";
            }

            try {
                Thread.sleep(DELAY_MILLIS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Polling thread interrupted: {}", e.getMessage());
                return "undefined_error";
            }

            retryCount++;
            if (retryCount >= MAX_RETRY_COUNT) {
                log.warn("Max retry count reached for PaymentIntent {}. Final status: {}", paymentIntentId, status);
                return "in_process"; // Still in process after max retries
            }

        } while (!"succeeded".equals(status) && !"requires_payment_method".equals(status) && !"canceled".equals(status));

        return "undefined_error";
    }

    private PaymentResponse mapToPaymentResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .bookingId(payment.getBookingId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .paymentMethod(payment.getPaymentMethod())
                .providerTransactionId(payment.getProviderTransactionId())
                .createdAt(payment.getCreatedAt())
                .build();
    }

    private RefundResponse mapToRefundResponse(Refund refund) {
        return RefundResponse.builder()
                .id(refund.getId())
                .paymentId(refund.getPayment().getId())
                .amount(refund.getAmount())
                .status(refund.getStatus())
                .build();
    }
}