package pl.smyk.paymentservice.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentRequest {
    private String customerEmail;
    private String reservationId;
}
