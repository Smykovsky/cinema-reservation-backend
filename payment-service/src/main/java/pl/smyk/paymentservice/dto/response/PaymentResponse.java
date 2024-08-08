package pl.smyk.paymentservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Data
@Builder
public class PaymentResponse {
    private String paymentId;
    private Map<String, String> metaData;
    private String message;
}
