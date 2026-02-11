package pl.smyk.paymentservice.dto;



import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;




@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentInitializationRequest {
    @NotNull(message = "Booking ID cannot be null")
    private Long bookingId;




}
