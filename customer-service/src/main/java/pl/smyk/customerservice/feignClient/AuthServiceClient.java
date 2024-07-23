package pl.smyk.customerservice.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import pl.smyk.customerservice.dto.CustomerDto;

@FeignClient(name = "auth-service", url = "http://localhost:8080/api/auth")
public interface AuthServiceClient {
    @GetMapping("/user")
    CustomerDto getCustomer(@RequestHeader("Authorization") String authorizationHeader);
}
