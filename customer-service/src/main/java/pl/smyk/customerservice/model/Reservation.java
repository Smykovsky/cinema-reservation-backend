package pl.smyk.customerservice.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import pl.smyk.customerservice.dto.SeatDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "reservation")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {
    @MongoId
    private String id;
    private String customerEmail;
    private Long roomNumber;
    private Movie movie;
    private LocalDateTime selectedPlayTime;
    private List<Seat> seats;
    private Double totalPrice;
    private PaymentStatus paymentStatus;

    @AllArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class Seat {
        @Min(value = 1, message = "Row number minimum 1")
        @Max(value = 12, message = "Row number maximum 12")
        private int row;
        @Min(value = 1, message = "Place number minimum 1")
        @Max(value = 15, message = "Place number maximum 15")
        private int placeNumber;


        public static List<Seat> seatDtoToSeat(List<SeatDto> seatDtoList) {
            return seatDtoList.stream()
                    .map(dto -> Seat.builder()
                            .row(dto.getRow())
                            .placeNumber(dto.getPlaceNumber())
                            .build())
                    .toList();
        }
    }
}
