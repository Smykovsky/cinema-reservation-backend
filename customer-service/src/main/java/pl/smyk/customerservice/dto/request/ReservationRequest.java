package pl.smyk.customerservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.smyk.customerservice.dto.SeatDto;
import pl.smyk.customerservice.model.PlayTime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReservationRequest {
    private String customerEmail;
    private Long roomNumber;
    private List<SeatDto> seats;
    private String movieId;
    private LocalDate selectedDate;
    private PlayTime selectedTime;
}
