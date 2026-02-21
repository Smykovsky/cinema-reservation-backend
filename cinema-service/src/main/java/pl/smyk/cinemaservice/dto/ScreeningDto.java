package pl.smyk.cinemaservice.dto;

import lombok.*;
import pl.smyk.cinemaservice.model.ScreeningStatus;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ScreeningDto {
    Long id;
    Long movieId;
    Long hallId;
    Instant startTime;
    Instant endTime;
    BigDecimal basePrice;
    BigDecimal vipPrice;
    BigDecimal wheelchairPrice;
    String status;
    Instant createdAt;
}