package pl.smyk.cinemaservice.dto;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.List;

@Value
@Builder
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