package pl.smyk.paymentservice.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResponse {
    private String paymentId;
    private String message;
}
