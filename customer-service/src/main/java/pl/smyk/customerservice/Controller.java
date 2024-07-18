package pl.smyk.customerservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customer")
public class Controller {
    @Autowired
    private PaymentServiceManagement paymentServiceManagement;

    @GetMapping("/test1")
    public String get() {
        return paymentServiceManagement.get();
    }

    @GetMapping("/test2")
    public String getTest() {
        return paymentServiceManagement.getTest();
    }
}
