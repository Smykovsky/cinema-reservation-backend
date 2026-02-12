package pl.smyk.notificationservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import pl.smyk.common.dto.PaymentCompletedEvent;
import pl.smyk.common.dto.PaymentFailedEvent;
import pl.smyk.common.dto.PaymentRefundedEvent;
import pl.smyk.notificationservice.dto.EmailRequest;
import pl.smyk.notificationservice.service.EmailService;
import pl.smyk.notificationservice.service.SmsService;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentEventsConsumer {
    private final EmailService emailService;
    private final SmsService smsService;

    @KafkaListener(topics = "payment_completed", groupId = "${kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
    public void consumePaymentCompletedEvent(PaymentCompletedEvent paymentCompletedEvent) {
        System.out.println("Received Payment Completed Event: " + paymentCompletedEvent.getPaymentId());
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setTo(paymentCompletedEvent.getUserData().getEmail());
        emailRequest.setSubject("Płatność zakończona sukcesem");
        emailRequest.setDescription("Płatność o id: " + paymentCompletedEvent.getBookingId() + "zakończona sukcesem");
        emailRequest.setTitle("Status płatności");
        emailService.sendEmail(emailRequest);

//        SmsRequest request = new SmsRequest("513108441", "Pomyślnie oplacono rezerwacje");
//        smsService.sendSms(request);
    }

    @KafkaListener(topics = "payment_failed", groupId = "${kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
    public void consumePaymentFailedEvent(PaymentFailedEvent paymentFailedEvent) {
        System.out.println("Received Payment Failed Event: " + paymentFailedEvent.getPaymentId());
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setTo(paymentFailedEvent.getUserData().getEmail());
        emailRequest.setSubject("Płatność zakończona niepowodzeniem");
        emailRequest.setDescription("Wystąpił błąd podczas dokonywania płatności o id: " + paymentFailedEvent.getPaymentId());
        emailRequest.setTitle("Status płatności");
        emailService.sendEmail(emailRequest);
    }

    @KafkaListener(topics = "payment_refunded", groupId = "${kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
    public void consumePaymentRefundedEvent(PaymentRefundedEvent paymentRefundedEvent) {
        System.out.println("Received Payment Refunded Event: " + paymentRefundedEvent.getPaymentId());
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setTo(paymentRefundedEvent.getUserData().getEmail());
        emailRequest.setSubject("Zwrot płatności");
        emailRequest.setDescription("Zlecono zwrot płatności o id: " + paymentRefundedEvent.getPaymentId());
        emailRequest.setTitle("Status płatności");
        emailService.sendEmail(emailRequest);
    }
}