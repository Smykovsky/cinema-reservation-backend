package pl.smyk.paymentservice.dto.request;

import lombok.Data;

@Data
public class PaymentAcceptRequest {
    private String paymentId;
    private String blikCode;
}
