package pl.smyk.bookingservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.smyk.bookingservice.dto.BookingDetailsResponse;
import pl.smyk.bookingservice.dto.CreateBookingRequest;
import pl.smyk.bookingservice.service.BookingService;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDetailsResponse> createBooking(@RequestBody @Valid CreateBookingRequest request) {
        BookingDetailsResponse response = bookingService.createBooking(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingDetailsResponse> getBookingDetails(@PathVariable Long id) {
        BookingDetailsResponse response = bookingService.getBookingDetails(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/user/history")
    public ResponseEntity<List<BookingDetailsResponse>> getUserBookingsDetails(@RequestHeader("X-User-Id") Long userId) {
        List<BookingDetailsResponse> response = bookingService.getAllUserBookings(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id, @RequestBody String reason) {
        bookingService.cancelBooking(id, reason);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}