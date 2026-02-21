package pl.smyk.cinemaservice.dto;

import lombok.*;
import pl.smyk.cinemaservice.model.SeatType;

@Builder
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class SeatDto {
    Long id;
    Long hallId;
    Integer row;
    Integer number;
    SeatType seatType;
}