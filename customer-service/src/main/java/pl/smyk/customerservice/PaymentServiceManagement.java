package pl.smyk.customerservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "payment-service", url = "http://localhost:8080/api/payment")
public interface PaymentServiceManagement {

    @GetMapping("/test1")
    String get();

    @GetMapping("/test2")
    String getTest();
}
