package pl.smyk.common.dto;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
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