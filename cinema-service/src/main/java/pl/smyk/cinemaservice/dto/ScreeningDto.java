package pl.smyk.cinemaservice.dto;

import lombok.Builder;
import lombok.Value;
import pl.smyk.cinemaservice.model.ScreeningStatus;

import java.math.BigDecimal;
import java.time.Instant;

@Value
@Builder
public class ScreeningDto {
    Long id;
    Long movieId;
    Long hallId;
    Instant startTime;
    Instant endTime;
    BigDecimal basePrice;
    BigDecimal vipPrice;
    BigDecimal wheelchairPrice;
    ScreeningStatus status;
    Instant createdAt;
}