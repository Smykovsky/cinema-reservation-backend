package pl.smyk.movieservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieDto {
    private Long id;
    private String title;
    private String description;
    private Integer durationMinutes;
    private LocalDate releaseDate;
    private Set<GenreDto> genres;
    private String posterUrl;
    private String ageRating;
}