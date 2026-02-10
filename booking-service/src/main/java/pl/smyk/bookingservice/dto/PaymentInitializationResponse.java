package pl.smyk.bookingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInitializationResponse {
    private String paymentId; // ID from the payment service
    private String paymentStatus; // e.g., PENDING, SUCCESS, FAILED
    private String redirectUrl; // URL for the user to complete payment
    // Other relevant payment details
}