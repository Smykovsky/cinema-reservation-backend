package pl.smyk.common.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

@Data
public class UserDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private List<String> roles;
    @Pattern(
            regexp = "^\\+?[1-9]\\d{1,14}$",
            message = "Numer telefonu musi być w formacie E.164 (np. +48123456789)"
    )
    private String phoneNumber;

    private boolean totpEnabled;
}