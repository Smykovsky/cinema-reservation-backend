package pl.smyk.customerservice.dto.response;

import lombok.Data;

import java.util.Map;

@Data
public class PaymentResponse {
    private String paymentId;
    private Map<String, String> metaData;
    private String message;
}
