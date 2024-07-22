package pl.smyk.customerservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import pl.smyk.customerservice.model.Movie;
import pl.smyk.customerservice.model.Reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@Component
public class AppRunner implements CommandLineRunner {
    @Autowired
    private MongoTemplate template;
    @Autowired
    private ReservationRepository reservationRepository;
    @Override
    public void run(String... args) throws Exception {
        template.dropCollection(Reservation.class);
        template.createCollection(Reservation.class);
        template.dropCollection(Movie.class);
        template.createCollection(Movie.class);

        Movie m1 = Movie.builder()
          .title("Bez litości")
          .genre("Akcja")
          .description("Bez litości opis")
          .playDateTime(LocalDateTime.of(2024, 9, 10, 18, 00))
          .build();

        Movie m2 = Movie.builder()
          .title("Bez litości2")
          .genre("Akcja")
          .description("Bez litości2 opis")
          .playDateTime(LocalDateTime.of(2024, 9, 10, 20, 30))
          .build();

        Reservation r1 = Reservation.builder()
                .customerEmail("smyku1232@wp.pl")
                .name("testname1")
                .movie(m1)
                .seats(List.of(Reservation.Seat.builder().row(1).placeNumber(10).build())).build();

        Reservation r2 = Reservation.builder()
                .customerEmail("smyku1232@wp.pl")
                .name("testname2")
                .movie(m2)
                .seats(List.of(Reservation.Seat.builder().row(3).placeNumber(2).build(), Reservation.Seat.builder().row(3).placeNumber(3).build()))
                .build();

        reservationRepository.save(r1);
        reservationRepository.save(r2);
    }
}
