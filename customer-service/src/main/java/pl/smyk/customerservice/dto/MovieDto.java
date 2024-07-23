package pl.smyk.customerservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MovieDto {
    private String title;
    private String genre;
    private String description;
    private LocalDateTime playDateTime;
}
