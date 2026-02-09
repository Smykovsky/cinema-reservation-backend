package pl.smyk.cinemaservice.dto;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.List;

@Value
@Builder
public class HallDto {
    Long id;
    Long cinemaId; // Just the ID for now
    String name;
    Integer totalSeats;
    List<String> features;
    Boolean isActive;
    Instant createdAt;
}