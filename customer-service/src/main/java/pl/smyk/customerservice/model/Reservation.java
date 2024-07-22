package pl.smyk.customerservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;

@Document(collection = "reservation")
@Getter
@Setter
@Builder
public class Reservation {
    @MongoId
    private String id;
    private String customerEmail;
    private String name;
    private Long roomNumber;
    private Movie movie;
    private List<Seat> seats;


    @AllArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class Seat {
        private int row;
        private int placeNumber;
    }
}
