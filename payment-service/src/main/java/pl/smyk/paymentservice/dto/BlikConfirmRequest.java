package pl.smyk.paymentservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlikConfirmRequest {
    @NotBlank(message = "BLIK code cannot be blank")
    private String blikCode;
}
