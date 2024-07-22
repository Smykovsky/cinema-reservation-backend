package pl.smyk.customerservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.smyk.customerservice.feignClient.AuthServiceClient;

@RestController
@RequestMapping("/api/customer")
public class Controller {
    @Autowired
    private AuthServiceClient authServiceClient;

    @GetMapping("/user")
    public String get(@RequestHeader("Authorization") String authorizationHeader) {
     return authServiceClient.getUser(authorizationHeader);
    }

}
