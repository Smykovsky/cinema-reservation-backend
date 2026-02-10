package pl.smyk.bookingservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.smyk.bookingservice.dto.BookingDetailsResponse;
import pl.smyk.bookingservice.dto.CreateBookingRequest;
import pl.smyk.bookingservice.service.BookingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/test")
    public String test() {
        return "test";
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}