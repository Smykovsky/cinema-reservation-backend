package pl.smyk.customerservice.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import pl.smyk.customerservice.dto.response.PaymentResponse;

@FeignClient(name = "payment-service", url = "http://localhost:8080/api/payment")
public interface PaymentServiceClient {
    //create payment intent
    @PostMapping("/create-payment/{reservationId}")
    PaymentResponse createPayment(@RequestHeader("Authorization") String authorizationHeader, @PathVariable String reservationId);
}
