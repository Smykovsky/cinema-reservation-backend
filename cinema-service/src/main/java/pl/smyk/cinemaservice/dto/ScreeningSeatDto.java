package pl.smyk.cinemaservice.dto;

import lombok.*;
import pl.smyk.cinemaservice.model.SeatStatus;
import pl.smyk.cinemaservice.model.SeatType;

@Builder
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ScreeningSeatDto {
    Long seatId;
    Integer row;
    Integer number;
    SeatType seatType;
    SeatStatus status;
}