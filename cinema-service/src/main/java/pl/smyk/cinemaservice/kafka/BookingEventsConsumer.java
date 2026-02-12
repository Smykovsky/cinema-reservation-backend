package pl.smyk.cinemaservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import pl.smyk.cinemaservice.dto.ChangeSeatStatusRequest;
import pl.smyk.cinemaservice.service.ScreeningService;
import pl.smyk.common.dto.BookingEventDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingEventsConsumer {
    private final ScreeningService screeningService;

    @KafkaListener(topics = "booking_created", groupId = "${kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
    public void consumeBookingCreatedEvent(BookingEventDto event) {
        log.info("Received booking_created event: {}", event);
        try {
            ChangeSeatStatusRequest request = ChangeSeatStatusRequest.builder()
                    .screeningId(event.getScreeningId())
                    .seatIds(event.getSeatIds())
                    .build();
            screeningService.confirmSeats(event.getScreeningId(), request);
            log.info("Seats confirmed for screening ID {} and booking ID {}", event.getScreeningId(), event.getBookingId());
        } catch (Exception e) {
            log.error("Error confirming seats for booking event: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "booking_cancelled", groupId = "${kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
    public void consumeBookingCancelledEvent(BookingEventDto event) {
        log.info("Received booking_cancelled event: {}", event);
        try {
            ChangeSeatStatusRequest request = ChangeSeatStatusRequest.builder()
                    .screeningId(event.getScreeningId())
                    .seatIds(event.getSeatIds())
                    .build();
            screeningService.releaseSeats(event.getScreeningId(), request);
            log.info("Seats released for screening ID {} and booking ID {}", event.getScreeningId(), event.getBookingId());
        } catch (Exception e) {
            log.error("Error releasing seats for booking cancellation event: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "booking_expired", groupId = "${kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
    public void consumeBookingExpiredEvent(BookingEventDto event) {
        log.info("Received booking_expired event: {}", event);
        try {
            ChangeSeatStatusRequest request = ChangeSeatStatusRequest.builder()
                    .screeningId(event.getScreeningId())
                    .seatIds(event.getSeatIds())
                    .build();
            screeningService.releaseSeats(event.getScreeningId(), request);
            log.info("Seats released for screening ID {} and booking ID {} due to expiration", event.getScreeningId(), event.getBookingId());
        } catch (Exception e) {
            log.error("Error releasing seats for booking expiration event: {}", e.getMessage());
        }
    }
}