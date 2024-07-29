package pl.smyk.customerservice.dto;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.smyk.customerservice.model.Genre;
import pl.smyk.customerservice.model.PlayTime;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieDto {
    @Nullable
    private String id;
    private String title;
    private List<Genre> genres;
    private String description;
    private Long playingRoom;
    private List<LocalDate> playDates;
    private List<PlayTime> playTimes;
}
