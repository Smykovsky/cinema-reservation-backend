package pl.smyk.cinemaservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Builder
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class CreateSeatsRequest {
    @NotNull(message = "Seats list cannot be null")
    @Size(min = 1, message = "At least one seat must be provided")
    List<@Valid CreateSeatRequest> seats;
}