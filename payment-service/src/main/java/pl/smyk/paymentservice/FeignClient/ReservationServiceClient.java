package pl.smyk.paymentservice.FeignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "customer-service", url = "http://localhost:8080/api/customer")
public interface ReservationServiceClient {
    @GetMapping("/{id}")
    ResponseEntity<?> getLoggedUserReservationById(@RequestHeader("Authorization") String authorizationHeader, @PathVariable String id);
    @GetMapping("/{id}/update-paymentStatus-paid")
    void updatePaymentStatusAsPaid(@RequestHeader("Authorization") String authorizationHeader, @PathVariable String id);
}
