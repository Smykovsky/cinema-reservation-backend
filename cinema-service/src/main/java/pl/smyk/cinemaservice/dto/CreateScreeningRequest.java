package pl.smyk.cinemaservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class CreateScreeningRequest {
    @NotNull(message = "Movie ID cannot be null")
    Long movieId;

    @NotNull(message = "Hall ID cannot be null")
    Long hallId;

    @NotNull(message = "Start time cannot be null")
    @Future(message = "Start time must be in the future")
    Instant startTime;

    @NotNull(message = "End time cannot be null")
    @Future(message = "End time must be in the future")
    Instant endTime;

    @NotNull(message = "Base price cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Base price must be positive")
    BigDecimal basePrice;

    @NotNull(message = "VIP price cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "VIP price must be positive")
    BigDecimal vipPrice;

    @DecimalMin(value = "0.0", inclusive = false, message = "Wheelchair price must be positive")
    BigDecimal wheelchairPrice;
}