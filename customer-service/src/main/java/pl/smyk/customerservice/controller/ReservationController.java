package pl.smyk.customerservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.smyk.customerservice.dto.CustomerDto;
import pl.smyk.customerservice.dto.ReservationDto;
import pl.smyk.customerservice.dto.request.ReservationRequest;
import pl.smyk.customerservice.dto.response.PaymentResponse;
import pl.smyk.customerservice.dto.response.ReservationResponse;
import pl.smyk.customerservice.feignClient.AuthServiceClient;
import pl.smyk.customerservice.feignClient.PaymentServiceClient;
import pl.smyk.customerservice.service.ReservationService;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;
    private final AuthServiceClient authServiceClient;
    private final PaymentServiceClient paymentServiceClient;

    @GetMapping("")
    public ResponseEntity<?> getLoggedUserReservations(@RequestHeader("Authorization") String authorizationHeader) {
        CustomerDto customer = authServiceClient.getCustomer(authorizationHeader);
        if (customer == null) {
          return ResponseEntity.status(404).body("User not found");
        }

        List<ReservationDto> reservationsByCustomerEmail = reservationService.getReservationsByCustomerEmail(customer.getEmail());

        return ResponseEntity.ok(reservationsByCustomerEmail);
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getLoggedUserReservationById(@RequestHeader("Authorization") String authorizationHeader, @PathVariable String id) {
        CustomerDto customer = authServiceClient.getCustomer(authorizationHeader);
        if (customer == null) {
          return ResponseEntity.status(404).body("User not found");
        }

        ReservationDto reservationDto = reservationService.getReservationById(id);

        return ResponseEntity.ok(reservationDto);
    }

    @PostMapping("/reservation")
    public ResponseEntity<?> createReservation(@RequestHeader("Authorization") String authorizationHeader, @RequestBody ReservationRequest request) {
        CustomerDto customer = authServiceClient.getCustomer(authorizationHeader);
        if (customer == null) {
            return ResponseEntity.status(404).body("User not found!");
        }
        request.setCustomerEmail(customer.getEmail());

        ReservationResponse response = reservationService.createReservation(request);
        if (response.getErrorReason() == null) {
            PaymentResponse paymentResponse = paymentServiceClient.createPayment(authorizationHeader, response.getReservationId());

            return ResponseEntity.ok(paymentResponse);
        } else if (response.getErrorReason().equals("NOT_SCREENING_AT_DATE")) {
            return ResponseEntity.status(409).body("We are not screening this movie at selected date!");
        } else if (response.getErrorReason().equals("SEAT_OCCUPIED")) {
            return ResponseEntity.status(409).body("Selected seats are currently occupied! Select other seats!");
        } else {
            return ResponseEntity.status(500).body("A problem has been encountered. Please try later");
        }
    }

    @PostMapping("/{id}/update-paymentStatus-paid")
    public void updatePaymentStatusAsPaid(@RequestHeader("Authorization") String  authorizationHeader, @PathVariable String id) {
        CustomerDto customer = authServiceClient.getCustomer(authorizationHeader);
        if (customer == null) {
            return;
        }

        reservationService.changePaymentStatusAsPaid(id);
    }
}
