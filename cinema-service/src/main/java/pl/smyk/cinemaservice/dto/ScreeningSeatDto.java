package pl.smyk.cinemaservice.dto;

import lombok.Builder;
import lombok.Value;
import pl.smyk.cinemaservice.model.SeatStatus;
import pl.smyk.cinemaservice.model.SeatType;

@Value
@Builder
public class ScreeningSeatDto {
    Long seatId;
    Integer row;
    Integer number;
    SeatType seatType;
    SeatStatus status;
}