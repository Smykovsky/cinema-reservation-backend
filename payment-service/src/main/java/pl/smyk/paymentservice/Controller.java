package pl.smyk.paymentservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
public class Controller {
    @GetMapping("/test1")
    public String get() {
        return "Hello from payment service and send to customerService";
    }

    @GetMapping("/test2")
    public String getTest() {
        return "This is second endpoint from payment used by customer-service";
    }
}
