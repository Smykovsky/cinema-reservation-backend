package pl.smyk.bookingservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant; // Add this import

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentFailedEvent {
    private Long bookingId;
    private String bookingNumber;
    private BigDecimal amount; // New field, assuming payment failed for a certain amount
    private String currency; // New field
    private String reason;
    private Instant timestamp; // New field
}