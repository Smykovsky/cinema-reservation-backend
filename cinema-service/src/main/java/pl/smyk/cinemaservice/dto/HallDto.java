package pl.smyk.cinemaservice.dto;

import lombok.*;

import java.time.Instant;
import java.util.List;

@Builder
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class HallDto {
    Long id;
    Long cinemaId; // Just the ID for now
    String name;
    Integer totalSeats;
    List<String> features;
    Boolean isActive;
    Instant createdAt;
}