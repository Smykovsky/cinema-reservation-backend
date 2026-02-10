package pl.smyk.bookingservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import pl.smyk.bookingservice.dto.ReserveSeatsRequest;
import pl.smyk.bookingservice.dto.ScreeningResponse;

@FeignClient(name = "cinema-service", url = "http://localhost:8080/api/screening")
public interface CinemaServiceFeignClient {

    @GetMapping("/{id}")
    ScreeningResponse getScreeningById(@PathVariable("id") Long id);

    @PostMapping("/{id}/seats/reserve")
    void reserveSeats(@PathVariable("id") Long id, @RequestBody ReserveSeatsRequest request);
}