package pl.smyk.paymentservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.smyk.paymentservice.service.PaymentService;

@Component
public class AppRunner implements CommandLineRunner {
    @Autowired
    private PaymentService paymentService;
    @Override
    public void run(String... args) throws Exception {

    }
}
