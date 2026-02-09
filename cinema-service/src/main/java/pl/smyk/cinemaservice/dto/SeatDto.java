package pl.smyk.cinemaservice.dto;

import lombok.Builder;
import lombok.Value;
import pl.smyk.cinemaservice.model.SeatType;

@Value
@Builder
public class SeatDto {
    Long id;
    Long hallId;
    Integer row;
    Integer number;
    SeatType seatType;
}