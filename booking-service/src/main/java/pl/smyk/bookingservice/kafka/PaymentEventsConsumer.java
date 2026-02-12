package pl.smyk.bookingservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.smyk.bookingservice.dto.event.PaymentCompletedEvent;
import pl.smyk.bookingservice.dto.event.PaymentFailedEvent;
import pl.smyk.bookingservice.service.BookingService;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventsConsumer {

    private final BookingService bookingService;

    @KafkaListener(topics = "payment_completed", groupId = "${kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
    public void consumePaymentCompleted(PaymentCompletedEvent event) {
        log.info("Payment completed event received for booking ID: {}", event.getBookingId());
        bookingService.confirmBooking(event.getBookingId());
    }

    @KafkaListener(topics = "payment_failed", groupId = "${kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
    public void consumePaymentFailed(PaymentFailedEvent event) {
        log.info("Payment failed event received for booking ID: {}", event.getBookingId());
        bookingService.cancelBooking(event.getBookingId(), "Payment failed");
    }
}
