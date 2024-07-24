package pl.smyk.customerservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeatDto {
    @Min(value = 1, message = "Row number minimum 1")
    @Max(value = 12, message = "Row number maximum 12")
    private int row;
    @Min(value = 1, message = "Place number minimum 1")
    @Max(value = 15, message = "Place number maximum 15")
    private int placeNumber;
}
