package pl.smyk.paymentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import pl.smyk.common.dto.UserDto;

@FeignClient(name = "auth-service", url = "http://localhost:8080/api/auth")
public interface AuthServiceClient {
    @GetMapping("/user")
    UserDto getUser();
}
