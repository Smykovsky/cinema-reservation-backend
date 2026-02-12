package pl.smyk.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentFailedEvent {
    private Long paymentId;
    private Long bookingId;
    private String bookingNumber;
    private BigDecimal amount;
    private String currency;
    private String reason;
    private Instant timestamp;
    private UserDto userData;
}
