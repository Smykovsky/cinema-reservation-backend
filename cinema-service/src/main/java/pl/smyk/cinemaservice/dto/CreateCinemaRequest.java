package pl.smyk.cinemaservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.validator.constraints.Length;

@Jacksonized
@Value
@Builder
public class CreateCinemaRequest {
    @NotBlank(message = "Cinema name cannot be blank")
    @Length(max = 255, message = "Cinema name cannot exceed 255 characters")
    String name;

    @NotBlank(message = "Address cannot be blank")
    @Length(max = 500, message = "Address cannot exceed 500 characters")
    String address;

    @NotBlank(message = "City cannot be blank")
    @Length(max = 100, message = "City cannot exceed 100 characters")
    String city;

    @Pattern(
            regexp = "^(\\+\\d{1,3}[- ]?)?\\d{9}$",
            message = "Invalid phone number format"
    )
    @Length(max = 20, message = "Phone number cannot exceed 20 characters")
    String phone;

    @NotNull(message = "isActive cannot be null")
    Boolean isActive;
}