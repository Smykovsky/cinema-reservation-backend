package pl.smyk.bookingservice.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class BookingSeatResponse {
    private Long id;
    private Long seatId;
    private BigDecimal price;
}