package pl.smyk.paymentservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.smyk.paymentservice.dto.PaymentCompletedEvent;
import pl.smyk.paymentservice.dto.PaymentFailedEvent;
import pl.smyk.paymentservice.dto.PaymentRefundedEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentEventProducer {

    private static final String TOPIC_PAYMENT_COMPLETED = "payment_completed";
    private static final String TOPIC_PAYMENT_FAILED = "payment_failed";
    private static final String TOPIC_PAYMENT_REFUNDED = "payment_refunded";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendPaymentCompletedEvent(PaymentCompletedEvent event) {
        log.info("Sending payment completed event for payment ID: {}", event.getPaymentId());
        kafkaTemplate.send(TOPIC_PAYMENT_COMPLETED, event.getPaymentId().toString(), event);
    }

    public void sendPaymentFailedEvent(PaymentFailedEvent event) {
        log.info("Sending payment failed event for payment ID: {}", event.getPaymentId());
        kafkaTemplate.send(TOPIC_PAYMENT_FAILED, event.getPaymentId().toString(), event);
    }

    public void sendPaymentRefundedEvent(PaymentRefundedEvent event) {
        log.info("Sending payment refunded event for refund ID: {}", event.getRefundId());
        kafkaTemplate.send(TOPIC_PAYMENT_REFUNDED, event.getRefundId().toString(), event);
    }
}
