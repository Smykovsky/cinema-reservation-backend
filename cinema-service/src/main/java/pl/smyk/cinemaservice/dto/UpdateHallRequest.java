package pl.smyk.cinemaservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Builder
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class UpdateHallRequest {
    @NotNull(message = "Hall ID cannot be null")
    Long id;

    @NotNull(message = "Cinema ID cannot be null")
    Long cinemaId;

    @NotBlank(message = "Hall name cannot be blank")
    @Length(max = 100, message = "Hall name cannot exceed 100 characters")
    String name;

    @NotNull(message = "Total seats cannot be null")
    @Min(value = 1, message = "Total seats must be at least 1")
    Integer totalSeats;

    List<String> features;

    @NotNull(message = "isActive cannot be null")
    Boolean isActive;
}