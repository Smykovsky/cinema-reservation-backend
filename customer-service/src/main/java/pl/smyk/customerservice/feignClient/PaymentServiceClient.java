package pl.smyk.customerservice.feignClient;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "payment-service", url = "http://localhost:8080/api/payment")
public interface PaymentServiceClient {
    //create payment intent

    //accept payment (blik)
}
