package pl.smyk.movieservice.dto;

import jakarta.validation.constraints.*;
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
public class MovieCreateRequest {

    @NotBlank(message = "Tytuł filmu jest wymagany")
    @Size(max = 255, message = "Tytuł nie może przekraczać 255 znaków")
    private String title;

    @Size(max = 5000, message = "Opis nie może przekraczać 5000 znaków")
    private String description;

    @NotNull(message = "Czas trwania jest wymagany")
    @Min(value = 1, message = "Czas trwania musi być większy niż 0")
    @Max(value = 600, message = "Czas trwania nie może przekraczać 600 minut")
    private Integer durationMinutes;

    @PastOrPresent(message = "Data premiery nie może być w przyszłości")
    private LocalDate releaseDate;

    private Set<Long> genreIds; // ID gatunków

    @Size(max = 500, message = "URL plakatu nie może przekraczać 500 znaków")
    private String posterUrl;

    @Size(max = 10, message = "Ocena wiekowa nie może przekraczać 10 znaków")
    private String ageRating;
}