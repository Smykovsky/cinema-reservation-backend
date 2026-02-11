package pl.smyk.paymentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pl.smyk.paymentservice.dto.BookingDetailsResponse;

@FeignClient(name = "booking-service", url = "http://localhost:8080/api/booking")
public interface BookingServiceClient {

    @GetMapping("/{id}")
    BookingDetailsResponse getBookingDetails(@PathVariable("id") Long id);
}
