package pl.smyk.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDetailsResponse {
    private Long id;
    private String bookingNumber;
    private BigDecimal totalAmount;
    private String status; // e.g., PENDING, CONFIRMED, CANCELLED, EXPIRED
    private LocalDateTime expiresAt;
    // Add any other fields from booking-service that might be relevant for payment validation
}
