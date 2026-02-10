package pl.smyk.bookingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScreeningResponse {
    private Long id;
    private Long movieId;
    private Long hallId;
    private Instant startTime;
    private Instant endTime;
    private BigDecimal basePrice;
    private BigDecimal vipPrice;
    private BigDecimal wheelchairPrice;
    private String status;
    private Instant createdAt;
}