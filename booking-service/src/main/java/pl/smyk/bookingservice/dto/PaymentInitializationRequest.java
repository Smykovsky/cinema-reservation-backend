package pl.smyk.bookingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID; // Assuming payment service might use UUID for transaction/booking IDs

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInitializationRequest {
    private Long bookingId;
    private BigDecimal amount;
    private String currency;
    private String description;
}