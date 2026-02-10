package pl.smyk.bookingservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.smyk.bookingservice.dto.BookingDetailsResponse;
import pl.smyk.bookingservice.dto.CreateBookingRequest;
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

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int BOOKING_NUMBER_LENGTH = 8;
    private static final Random random = new Random();

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

        String bookingNumber;
        do {
            bookingNumber = generateBookingNumber();
        } while (bookingRepository.findByBookingNumber(bookingNumber).isPresent());

        // For now, let's assume a fixed price for each seat
        // In a real scenario, this would involve calls to other services to get actual seat prices and user details
        BigDecimal seatPrice = new BigDecimal("15.00");

        BigDecimal totalAmount = seatPrice.multiply(BigDecimal.valueOf(request.getSeatIds().size()));
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(15); // Booking expires in 15 minutes

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
                        .price(seatPrice)
                        .build())
                .collect(Collectors.toList());

        bookingSeatRepository.saveAll(bookingSeats);

        BookingDetailsResponse response = buildBookingDetailsResponse(savedBooking, bookingSeats);
        bookingEventProducer.sendBookingCreatedEvent(response); // Send event
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