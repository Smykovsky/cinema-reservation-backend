package pl.smyk.customerservice.dto;

import lombok.Data;
import pl.smyk.customerservice.model.Movie;
import pl.smyk.customerservice.model.Reservation;

import java.util.List;

@Data
public class ReservationDto {
    private String customerEmail;
    private Long roomNumber;
    private MovieDto movie;
    private List<SeatDto> seats;
}
