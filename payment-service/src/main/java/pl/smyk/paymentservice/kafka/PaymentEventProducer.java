package pl.smyk.paymentservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import pl.smyk.paymentservice.dto.PaymentCompletedEvent;
import pl.smyk.paymentservice.dto.PaymentFailedEvent;
import pl.smyk.paymentservice.dto.PaymentRefundedEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventProducer {

    private static final String TOPIC_PAYMENT_COMPLETED = "payment_completed";
    private static final String TOPIC_PAYMENT_FAILED = "payment_failed";
    private static final String TOPIC_PAYMENT_REFUNDED = "payment_refunded";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendPaymentCompletedEvent(PaymentCompletedEvent event) {
        log.info("Sending payment completed event for booking ID: {}", event.getBookingId());
        kafkaTemplate.send(TOPIC_PAYMENT_COMPLETED, event.getBookingId().toString(), event);
    }

    public void sendPaymentFailedEvent(PaymentFailedEvent event) {
        log.info("Sending payment failed event for booking ID: {}", event.getBookingId());
        kafkaTemplate.send(TOPIC_PAYMENT_FAILED, event.getBookingId().toString(), event);
    }

    public void sendPaymentRefundedEvent(PaymentRefundedEvent event) {
        log.info("Sending payment refunded event for booking ID: {}", event.getBookingId());
        kafkaTemplate.send(TOPIC_PAYMENT_REFUNDED, event.getBookingId().toString(), event);
    }
}
