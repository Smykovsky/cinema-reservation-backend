package pl.smyk.customerservice.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Document(collection = "movie")
@Builder
@Getter
@Setter
public class Movie {
  @MongoId
  private String id;
  private String title;
  private String genre;
  private String description;
  private LocalDateTime playDateTime;
}
