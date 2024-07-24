package pl.smyk.customerservice.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class ReservationResponse {
    private String reservationId;
    private String customerEmail;
    private String message;
}
