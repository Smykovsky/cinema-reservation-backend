package pl.smyk.paymentservice.FeignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "auth-service", url = "http://localhost:8080/api/auth")
public interface AuthServiceClient {
    @GetMapping("/user")
    ResponseEntity<?> getCustomer(@RequestHeader("Authorization") String authorizationHeader);
}
