package pl.smyk.customerservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Document(collection = "movie")
@Getter
@Setter
public class Movie {
  @MongoId
  private String id;
  private String title;
  private String genre;
  private String description;
  private Long playingRoom;
  private List<LocalDate> playDates;
  private List<PlayTime> playTimes;

  @Builder
  public Movie(String id, String title, String genre, String description, Long playingRoom, List<LocalDate> playDates, List<PlayTime> playTimes) {
    this.id = id;
    this.title = title;
    this.genre = genre;
    this.description = description;
    this.playingRoom = playingRoom;
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
