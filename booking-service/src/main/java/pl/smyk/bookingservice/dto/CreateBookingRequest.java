package pl.smyk.bookingservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CreateBookingRequest {
    @NotNull
    private Long screeningId;
    @NotNull
    private Long userId;
    @NotEmpty // Ensures the list is not null and not empty
    private List<Long> seatIds;
}