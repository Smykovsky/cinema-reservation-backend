package pl.smyk.authservice.kafka;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.smyk.common.dto.BookingEventDto;
import pl.smyk.common.dto.PasswordResetEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthEventProducer {

    private static final String PASSWORD_FORGOT = "password_forgot";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendPasswordResetEvent(PasswordResetEvent passwordResetEvent) {
        log.info("Producing booking created event for booking ID: {}", passwordResetEvent.getEmail());
        kafkaTemplate.send(PASSWORD_FORGOT, passwordResetEvent.getEmail(), passwordResetEvent);
    }
}