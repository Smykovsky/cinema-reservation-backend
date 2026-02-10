package pl.smyk.bookingservice.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BookingEventConsumer {

    // Example listener for a generic booking event from another service
    @KafkaListener(topics = "some_other_service_booking_event", groupId = "${spring.kafka.consumer.group-id}")
    public void listenToBookingEvents(String message) {
        log.info("Received message from Kafka: {}", message);
        // Process the booking event
    }
}