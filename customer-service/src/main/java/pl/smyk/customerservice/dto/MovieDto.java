package pl.smyk.customerservice.dto;

import lombok.Data;
import pl.smyk.customerservice.model.PlayTime;

import java.time.LocalDate;
import java.util.List;

@Data
public class MovieDto {
    private String title;
    private String genre;
    private String description;
    private List<LocalDate> playDates;
    private List<PlayTime> playTimes;
}
