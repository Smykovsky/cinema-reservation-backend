package pl.smyk.bookingservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.smyk.bookingservice.client.CinemaServiceFeignClient;
import pl.smyk.bookingservice.dto.BookingDetailsResponse;
import pl.smyk.bookingservice.dto.CreateBookingRequest;
import pl.smyk.bookingservice.dto.ReserveSeatsRequest;
import pl.smyk.bookingservice.dto.ScreeningResponse;
import pl.smyk.bookingservice.exception.BookingNotFoundException;
import pl.smyk.bookingservice.exception.InvalidBookingRequestException;
import pl.smyk.bookingservice.kafka.BookingEventProducer;
import pl.smyk.bookingservice.model.Booking;
import pl.smyk.bookingservice.model.BookingSeat;
import pl.smyk.bookingservice.model.BookingStatus;
import pl.smyk.bookingservice.repository.BookingRepository;
import pl.smyk.bookingservice.repository.BookingSeatRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    public BookingDetailsResponse createBooking(CreateBookingRequest request) {
        if (request.getSeatIds() == null || request.getSeatIds().isEmpty()) {
            throw new InvalidBookingRequestException("Booking request must contain at least one seat.");
        }

        // 1. Get screening details from Cinema Service
        ScreeningResponse screening = cinemaServiceFeignClient.getScreeningById(request.getScreeningId());
        if (screening == null) {
            throw new InvalidBookingRequestException("Screening with ID " + request.getScreeningId() + " not found.");
        }

        // 2. Reserve seats in Cinema Service
        ReserveSeatsRequest reserveSeatsRequest = ReserveSeatsRequest.builder()
                .screeningId(request.getScreeningId())
                .seatIds(request.getSeatIds())
                .build();
        cinemaServiceFeignClient.reserveSeats(request.getScreeningId(), reserveSeatsRequest);

        String bookingNumber;
        do {
            bookingNumber = generateBookingNumber();
        } while (bookingRepository.findByBookingNumber(bookingNumber).isPresent());

        // Calculate total amount (assuming all seats have the base price for simplicity, adjust as needed)
        BigDecimal totalAmount = screening.getBasePrice().multiply(BigDecimal.valueOf(request.getSeatIds().size()));
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
                        .price(screening.getBasePrice()) // Assign seat price from screening
                        .build())
                .collect(Collectors.toList());

        bookingSeatRepository.saveAll(bookingSeats);

        // 3. Initialize payment in Payment Service
        // Here, you might want to store paymentResponse details in the booking or a separate entity
        // For now, we proceed to build the response and publish the event

        BookingDetailsResponse response = buildBookingDetailsResponse(savedBooking, bookingSeats);
        // 4. Publish booking.created Kafka event
        bookingEventProducer.sendBookingCreatedEvent(response);
        return response;
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

        // In a real scenario, implement logic to release seats, notify other services, etc.
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        bookingEventProducer.sendBookingCancelledEvent(bookingId); // Send event
    }

    public void confirmBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking with ID " + bookingId + " not found."));

        if (booking.getStatus() == BookingStatus.PENDING) {
            booking.setStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);
            bookingEventProducer.sendBookingConfirmedEvent(bookingId);
        } else {
            // Handle case where booking cannot be confirmed (e.g., already cancelled or expired)
            throw new InvalidBookingRequestException("Booking " + bookingId + " cannot be confirmed from its current status: " + booking.getStatus());
        }
    }

    public void expireBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking with ID " + bookingId + " not found."));

        if (booking.getStatus() == BookingStatus.PENDING) {
            booking.setStatus(BookingStatus.EXPIRED);
            bookingRepository.save(booking);
            bookingEventProducer.sendBookingExpiredEvent(bookingId);
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