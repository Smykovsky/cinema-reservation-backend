package pl.smyk.bookingservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.smyk.bookingservice.client.CinemaServiceFeignClient;
import pl.smyk.bookingservice.dto.*;
import pl.smyk.bookingservice.exception.BookingNotFoundException;
import pl.smyk.bookingservice.exception.InvalidBookingRequestException;
import pl.smyk.bookingservice.kafka.BookingEventProducer;
import pl.smyk.bookingservice.model.Booking;
import pl.smyk.bookingservice.model.BookingSeat;
import pl.smyk.bookingservice.model.BookingStatus;
import pl.smyk.bookingservice.repository.BookingRepository;
import pl.smyk.bookingservice.repository.BookingSeatRepository;
import pl.smyk.common.dto.BookingEventDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset; // New import for Instant conversion
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final BookingEventProducer bookingEventProducer;
    private final CinemaServiceFeignClient cinemaServiceFeignClient;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int BOOKING_NUMBER_LENGTH = 8;
    private static final Random random = new Random();
    private static final int BOOKING_EXPIRATION_MINUTES = 15; // Timeout for booking

    private String generateBookingNumber() {
        return random.ints(BOOKING_NUMBER_LENGTH, 0, CHARACTERS.length())
                .mapToObj(CHARACTERS::charAt)
                .map(Object::toString)
                .collect(Collectors.joining());
    }

    @Transactional
    public BookingDetailsResponse createBooking(CreateBookingRequest request) {

        if (request.getSeatIds() == null || request.getSeatIds().isEmpty()) {
            throw new InvalidBookingRequestException("Booking request must contain at least one seat.");
        }

        // 1. Pobranie seansu
        ScreeningResponse screening = cinemaServiceFeignClient.getScreeningById(request.getScreeningId());
        if (screening == null) {
            throw new InvalidBookingRequestException(
                    "Screening with ID " + request.getScreeningId() + " not found.");
        }

        // 2. Rezerwacja miejsc w cinema-service
        ReserveSeatsRequest reserveSeatsRequest = ReserveSeatsRequest.builder()
                .screeningId(request.getScreeningId())
                .seatIds(request.getSeatIds())
                .build();

        cinemaServiceFeignClient.reserveSeats(request.getScreeningId(), reserveSeatsRequest);

        // 3. Generowanie numeru rezerwacji
        String bookingNumber;
        do {
            bookingNumber = generateBookingNumber();
        } while (bookingRepository.findByBookingNumber(bookingNumber).isPresent());

        BigDecimal totalAmount = screening.getBasePrice()
                .multiply(BigDecimal.valueOf(request.getSeatIds().size()));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(BOOKING_EXPIRATION_MINUTES);

        Booking booking = Booking.builder()
                .userId(request.getUserId())
                .screeningId(request.getScreeningId())
                .bookingNumber(bookingNumber)
                .status(BookingStatus.PENDING)
                .totalAmount(totalAmount)
                .expiresAt(expiresAt)
                .createdAt(now)
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        List<BookingSeat> bookingSeats = request.getSeatIds().stream()
                .map(seatId -> BookingSeat.builder()
                        .booking(savedBooking)
                        .seatId(seatId)
                        .price(screening.getBasePrice())
                        .build())
                .toList();

        bookingSeatRepository.saveAll(bookingSeats);

        // ✅ 4. Tworzymy DTO eventowe (nie HTTP response)
        BookingEventDto eventDto = BookingEventDto.builder()
                .bookingId(savedBooking.getId())
                .screeningId(savedBooking.getScreeningId())
                .seatIds(request.getSeatIds())
                .expiresAt(savedBooking.getExpiresAt()
                        .atZone(ZoneId.systemDefault())
                        .toInstant())
                .build();

        bookingEventProducer.sendBookingCreatedEvent(eventDto);

        // 5. Zwracamy normalny response REST
        return buildBookingDetailsResponse(savedBooking, bookingSeats);
    }

    public BookingDetailsResponse getBookingDetails(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking with ID " + bookingId + " not found."));

        List<BookingSeat> bookingSeats = bookingSeatRepository.findByBooking(booking);
        return buildBookingDetailsResponse(booking, bookingSeats);
    }

    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking with ID " + bookingId + " not found."));

        List<Long> seatIds = bookingSeatRepository.findByBooking(booking).stream()
                .map(BookingSeat::getSeatId)
                .collect(Collectors.toList());

        BookingEventDto eventDto = BookingEventDto.builder()
                .bookingId(booking.getId())
                .screeningId(booking.getScreeningId())
                .seatIds(seatIds)
                .expiresAt(booking.getExpiresAt().toInstant(ZoneOffset.UTC))
                .build();

        // In a real scenario, implement logic to release seats, notify other services, etc.
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        bookingEventProducer.sendBookingCancelledEvent(eventDto); // Send event with DTO
    }

    public void confirmBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking with ID " + bookingId + " not found."));

        if (booking.getStatus() == BookingStatus.PENDING) {
            List<Long> seatIds = bookingSeatRepository.findByBooking(booking).stream()
                    .map(BookingSeat::getSeatId)
                    .collect(Collectors.toList());

            BookingEventDto eventDto = BookingEventDto.builder()
                    .bookingId(booking.getId())
                    .screeningId(booking.getScreeningId())
                    .seatIds(seatIds)
                    .expiresAt(booking.getExpiresAt().toInstant(ZoneOffset.UTC))
                    .build();

            booking.setStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);
            bookingEventProducer.sendBookingConfirmedEvent(eventDto); // Send event with DTO
        } else {
            // Handle case where booking cannot be confirmed (e.g., already cancelled or expired)
            throw new InvalidBookingRequestException("Booking " + bookingId + " cannot be confirmed from its current status: " + booking.getStatus());
        }
    }

    public void expireBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking with ID " + bookingId + " not found."));

        if (booking.getStatus() == BookingStatus.PENDING) {
            List<Long> seatIds = bookingSeatRepository.findByBooking(booking).stream()
                    .map(BookingSeat::getSeatId)
                    .collect(Collectors.toList());

            BookingEventDto eventDto = BookingEventDto.builder()
                    .bookingId(booking.getId())
                    .screeningId(booking.getScreeningId())
                    .seatIds(seatIds)
                    .expiresAt(booking.getExpiresAt().toInstant(ZoneOffset.UTC))
                    .build();

            booking.setStatus(BookingStatus.EXPIRED);
            bookingRepository.save(booking);
            bookingEventProducer.sendBookingExpiredEvent(eventDto);
            // In a real scenario, you might also want to release seats here
        } else {
            // Handle case where booking cannot be expired (e.g., already confirmed or cancelled)
            throw new InvalidBookingRequestException("Booking " + bookingId + " cannot be expired from its current status: " + booking.getStatus());
        }
    }

    private BookingDetailsResponse buildBookingDetailsResponse(Booking booking, List<BookingSeat> bookingSeats) {
        return BookingDetailsResponse.builder()
                .id(booking.getId())
                .userId(booking.getUserId())
                .screeningId(booking.getScreeningId())
                .bookingNumber(booking.getBookingNumber())
                .status(booking.getStatus())
                .totalAmount(booking.getTotalAmount())
                .expiresAt(booking.getExpiresAt())
                .createdAt(booking.getCreatedAt())
                .seats(bookingSeats.stream()
                        .map(seat -> pl.smyk.bookingservice.dto.BookingSeatResponse.builder()
                                .id(seat.getId())
                                .seatId(seat.getSeatId())
                                .price(seat.getPrice())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}