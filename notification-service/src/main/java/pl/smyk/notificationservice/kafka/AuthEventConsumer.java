package pl.smyk.notificationservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import pl.smyk.common.dto.PasswordResetEvent;
import pl.smyk.notificationservice.dto.EmailRequest;
import pl.smyk.notificationservice.service.EmailService;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthEventConsumer {
    private final EmailService emailService;

    @KafkaListener(topics = "password_forgot", groupId = "${kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
    public void consumeAuthResetPasswordEvent(PasswordResetEvent passwordResetEvent) {
        String resetLink = "http://localhost:3000/reset-password?token=" + passwordResetEvent.getToken();

        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setTo(passwordResetEvent.getEmail());
        emailRequest.setSubject("Resetowanie hasła");
        emailRequest.setTitle("Reset hasła");
        emailRequest.setDescription(
                "Kliknij w poniższy link, aby zresetować hasło:\n\n" +
                        resetLink +
                        "\n\nLink wygasa: " + passwordResetEvent.getExpiresAt()
        );

        emailService.sendEmail(emailRequest);
    }
}
