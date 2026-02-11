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

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;
    private final PaymentEventProducer paymentEventProducer;
    private final BookingServiceClient bookingServiceClient;

    public PaymentResponse initializePayment(PaymentInitializationRequest request) {
        BookingDetailsResponse bookingDetails = bookingServiceClient.getBookingDetails(request.getBookingId());

        if (bookingDetails == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found for ID: " + request.getBookingId());
        }

        if (!"PENDING".equalsIgnoreCase(bookingDetails.getBookingStatus()) && !"CONFIRMED".equalsIgnoreCase(bookingDetails.getBookingStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment can only be initialized for PENDING or CONFIRMED bookings.");
        }

        if (bookingDetails.getExpiresAt() != null && bookingDetails.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking has expired.");
        }

        Payment payment = Payment.builder()
                .bookingId(request.getBookingId())
                .amount(bookingDetails.getTotalAmount()) // Use amount from booking details
                .paymentMethod("CARD") // Hardcode for now as per discussion
                .status(Payment.PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        payment = paymentRepository.save(payment);
        log.info("Payment initialized for booking ID: {}, Payment ID: {}", payment.getBookingId(), payment.getId());

        return mapToPaymentResponse(payment);
    }

    public PaymentResponse getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));
        return mapToPaymentResponse(payment);
    }

    public void handlePaymentCallback(Long paymentId, String providerTransactionId, boolean success) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));

        if (success) {
            payment.setStatus(Payment.PaymentStatus.COMPLETED);
            payment.setProviderTransactionId(providerTransactionId);
            paymentRepository.save(payment);
            log.info("Payment ID: {} completed successfully.", paymentId);
            paymentEventProducer.sendPaymentCompletedEvent(PaymentCompletedEvent.builder()
                    .paymentId(payment.getId())
                    .bookingId(payment.getBookingId())
                    .amount(payment.getAmount())
                    .paymentMethod(payment.getPaymentMethod())
                    .providerTransactionId(providerTransactionId)
                    .completedAt(LocalDateTime.now())
                    .build());
        } else {
            payment.setStatus(Payment.PaymentStatus.FAILED);
            paymentRepository.save(payment);
            log.warn("Payment ID: {} failed.", paymentId);
            paymentEventProducer.sendPaymentFailedEvent(PaymentFailedEvent.builder()
                    .paymentId(payment.getId())
                    .bookingId(payment.getBookingId())
                    .amount(payment.getAmount())
                    .paymentMethod(payment.getPaymentMethod())
                    .failedAt(LocalDateTime.now())
                    .reason("Payment provider reported failure") // Example reason
                    .build());
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

        // In a real scenario, this would integrate with the payment gateway to process the refund.
        // For now, we simulate a successful refund.
        Refund refund = Refund.builder()
                .payment(payment)
                .amount(request.getAmount())
                .status(Refund.RefundStatus.PENDING)
                .build();
        refund = refundRepository.save(refund);
        log.info("Refund initiated for Payment ID: {}, Refund ID: {}", paymentId, refund.getId());

        // Simulate successful refund
        refund.setStatus(Refund.RefundStatus.COMPLETED);
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