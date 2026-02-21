package pl.smyk.cinemaservice.dto;

import lombok.*;

import java.time.Instant;
import java.util.List;

@Builder
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class CinemaDto {
    Long id;
    String name;
    String address;
    String city;
    String phone;
    Boolean isActive;
    Instant createdAt;
    Instant updatedAt;
    List<HallDto> halls; // Potentially simplified or IDs only for a DTO
}