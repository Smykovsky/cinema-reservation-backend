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
public class PaymentCompletedEvent {
    private Long bookingId;
    private String bookingNumber;
    private BigDecimal amount;
    private String currency; // New field
    private String paymentMethod; // New field
    private String transactionId; // Renamed from paymentId
    private Instant timestamp; // New field
}