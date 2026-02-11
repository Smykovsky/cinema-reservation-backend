package pl.smyk.paymentservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.smyk.paymentservice.client.BookingServiceClient;
import pl.smyk.paymentservice.dto.*;
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found for ID: " + request.getBookingId());
        }

        if (!"PENDING".equalsIgnoreCase(bookingDetails.getStatus()) && !"CONFIRMED".equalsIgnoreCase(bookingDetails.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment can only be initialized for PENDING or CONFIRMED bookings.");
        }

        if (bookingDetails.getExpiresAt() != null && bookingDetails.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking has expired.");
        }

        Payment payment = Payment.builder()
                .bookingId(request.getBookingId())
                .amount(bookingDetails.getTotalAmount())
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
            paymentRepository.save(payment); // Save again with Stripe ID

            PaymentResponse response = mapToPaymentResponse(payment);
            response.setClientSecret(paymentIntent.getClientSecret());
            return response;

        } catch (StripeException e) {
            log.error("Error creating PaymentIntent for booking ID {}: {}", payment.getBookingId(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating payment intent with Stripe.");
        }
    }

    public PaymentResponse getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));
        return mapToPaymentResponse(payment);
    }

    public void handlePaymentCallback(String paymentIntentId, String stripeStatus) { // Changed signature
        Payment payment = paymentRepository.findByProviderTransactionId(paymentIntentId) // Find by Stripe PI ID
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found for Stripe PaymentIntent ID: " + paymentIntentId));

        switch (stripeStatus) {
            case "succeeded":
                payment.setStatus(Payment.PaymentStatus.COMPLETED);
                paymentRepository.save(payment);
                log.info("Payment ID: {} completed successfully via Stripe webhook. Stripe PI ID: {}", payment.getId(), paymentIntentId);
                paymentEventProducer.sendPaymentCompletedEvent(PaymentCompletedEvent.builder()
                        .paymentId(payment.getId())
                        .bookingId(payment.getBookingId())
                        .amount(payment.getAmount())
                        .paymentMethod(payment.getPaymentMethod())
                        .providerTransactionId(paymentIntentId)
                        .completedAt(LocalDateTime.now())
                        .build());
                break;
            case "payment_failed": // Or other failure statuses
                payment.setStatus(Payment.PaymentStatus.FAILED);
                paymentRepository.save(payment);
                log.warn("Payment ID: {} failed via Stripe webhook. Stripe PI ID: {}", payment.getId(), paymentIntentId);
                paymentEventProducer.sendPaymentFailedEvent(PaymentFailedEvent.builder()
                        .paymentId(payment.getId())
                        .bookingId(payment.getBookingId())
                        .amount(payment.getAmount())
                        .paymentMethod(payment.getPaymentMethod())
                        .failedAt(LocalDateTime.now())
                        .reason("Stripe PaymentIntent failed")
                        .build());
                break;
            case "requires_action": // Or other intermediate statuses
                log.info("Payment ID: {} requires action via Stripe webhook. Stripe PI ID: {}", payment.getId(), paymentIntentId);
                // No change to DB status, maybe just log or send an internal event
                break;
            default:
                log.warn("Unhandled Stripe PaymentIntent status '{}' for Payment ID: {}", stripeStatus, payment.getId());
                break;
        }
    }

    public RefundResponse initiateRefund(Long paymentId, RefundRequest request) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));

        if (payment.getStatus() != Payment.PaymentStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only completed payments can be refunded.");
        }
        if (payment.getAmount().compareTo(request.getAmount()) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Refund amount exceeds original payment amount.");
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
                    .status(Refund.RefundStatus.PENDING) // Initial status, will be updated by webhook
                    .build();
            refund = refundRepository.save(refund);
            log.info("Refund initiated for Payment ID: {}, Refund ID: {}, Stripe Refund ID: {}", paymentId, refund.getId(), stripeRefund.getId());

            // In a real scenario, the status update (COMPLETED/FAILED) would come from a Stripe webhook for refund.
            // For now, we'll assume it's immediately successful for the purpose of this call.
            // However, it's better to process refund status updates via webhooks.
            refund.setStatus(Refund.RefundStatus.COMPLETED); // Assuming immediate success for this demo
            refundRepository.save(refund);

            payment.setStatus(Payment.PaymentStatus.REFUNDED); // Mark original payment as refunded
            paymentRepository.save(payment);

            paymentEventProducer.sendPaymentRefundedEvent(PaymentRefundedEvent.builder()
                    .refundId(refund.getId())
                    .paymentId(payment.getId())
                    .amount(refund.getAmount())
                    .refundedAt(LocalDateTime.now())
                    .build());

            return mapToRefundResponse(refund);

        } catch (StripeException e) {
            log.error("Error creating Stripe Refund for Payment ID {}: {}", paymentId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating refund with Stripe.");
        }
    }


    public BlikConfirmResponse confirmBlikPayment(Long paymentId, BlikConfirmRequest blikConfirmRequest) {
        try {
            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));

            if (!"BLIK".equals(payment.getPaymentMethod())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment is not a BLIK payment.");
            }
            if (payment.getStatus() != Payment.PaymentStatus.PENDING) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment is not in PENDING status.");
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

            // Poll for payment status (as per example)
            String finalStatus = pollPaymentStatus(payment.getProviderTransactionId());

            return BlikConfirmResponse.builder().status(finalStatus).build();

        } catch (StripeException e) {
            log.error("Error confirming BLIK payment for Payment ID {}: {}", paymentId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error confirming BLIK payment with Stripe.");
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