package pl.smyk.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingEventDto {
    private Long bookingId;
    private Long screeningId;
    private List<Long> seatIds;
    private Instant expiresAt;
}