package pl.smyk.cinemaservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import pl.smyk.cinemaservice.model.SeatType;

@Value
@Builder
public class UpdateSeatRequest {
    @NotNull(message = "Seat ID cannot be null")
    Long id;

    @NotNull(message = "Hall ID cannot be null")
    Long hallId;

    @NotNull(message = "Row cannot be null")
    @Min(value = 1, message = "Row must be at least 1")
    @Max(value = 20, message = "Row cannot exceed 20")
    Integer row;

    @NotNull(message = "Number cannot be null")
    @Min(value = 1, message = "Number must be at least 1")
    @Max(value = 10, message = "Number cannot exceed 10")
    Integer number;

    @NotNull(message = "Seat type cannot be null")
    SeatType seatType;
}