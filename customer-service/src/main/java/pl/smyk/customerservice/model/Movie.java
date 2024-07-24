package pl.smyk.customerservice.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

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
  private List<LocalDate> playDates;
  private List<PlayTime> playTimes;

  @Builder
  public Movie(String id, String title, String genre, String description, List<LocalDate> playDates, List<PlayTime> playTimes) {
    this.id = id;
    this.title = title;
    this.genre = genre;
    this.description = description;
    this.playDates = playDates;
    this.playTimes = playTimes != null ? playTimes : Arrays.asList(PlayTime.values());
  }

  public void addPlayTime(PlayTime playTime) {
    this.playTimes.add(playTime);
  }

  public void removePlayTime(PlayTime playTime) {
    this.playTimes.remove(playTime);
  }
}
