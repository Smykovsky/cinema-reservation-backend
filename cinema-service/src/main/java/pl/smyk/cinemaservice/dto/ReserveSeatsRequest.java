package pl.smyk.cinemaservice.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReserveSeatsRequest {
    @NotNull
    private Long screeningId;
    @NotEmpty
    private List<Long> seatIds;
}