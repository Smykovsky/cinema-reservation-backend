package pl.smyk.notificationservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsRequest {

    @NotBlank(message = "Numer telefonu nie może być pusty")
    @Pattern(
            regexp = "^[0-9]{9}$",
            message = "Numer telefonu musi składać się z dokładnie 9 cyfr (format: 123456789)"
    )
    private String phoneNumber;

    @NotBlank(message = "Treść wiadomości nie może być pusta")
    @Size(min = 1, max = 160, message = "Treść wiadomości musi mieć od 1 do 160 znaków")
    private String message;
}