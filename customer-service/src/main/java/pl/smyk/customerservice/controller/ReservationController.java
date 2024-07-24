package pl.smyk.customerservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.smyk.customerservice.dto.CustomerDto;
import pl.smyk.customerservice.dto.ReservationDto;
import pl.smyk.customerservice.feignClient.AuthServiceClient;
import pl.smyk.customerservice.service.ReservationService;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;
    private final AuthServiceClient authServiceClient;

    @GetMapping("")
    public ResponseEntity<?> getLoggedUserReservations(@RequestHeader("Authorization") String authorizationHeader) {
        CustomerDto customer = authServiceClient.getCustomer(authorizationHeader);
        if (customer == null) {
            return ResponseEntity.notFound().build();
        }

        List<ReservationDto> reservationsByCustomerEmail = reservationService.getReservationsByCustomerEmail(customer.getEmail());

        return ResponseEntity.ok(reservationsByCustomerEmail);
    }
}
