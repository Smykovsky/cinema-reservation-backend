package pl.smyk.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlikConfirmResponse {
    private String status; // e.g., "success", "in_process", "blik_code_error", "failed", "undefined_error"
}
