package pl.smyk.cinemaservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@Builder
@Jacksonized
public class CreateScreeningsRequest {
    @NotNull(message = "Screenings list cannot be null")
    @Size(min = 1, message = "At least one seat must be provided")
    List<@Valid CreateScreeningRequest> screenings;
}
