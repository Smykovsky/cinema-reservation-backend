package pl.smyk.customerservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import pl.smyk.customerservice.mapper.MovieMapper;
import pl.smyk.customerservice.mapper.ReservationMapper;
import pl.smyk.customerservice.model.Movie;
import pl.smyk.customerservice.model.Reservation;
import pl.smyk.customerservice.repository.MovieRepository;
import pl.smyk.customerservice.repository.ReservationRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class AppRunner implements CommandLineRunner {
    @Autowired
    private MongoTemplate template;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private MovieRepository movieRepository;
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
          .playDateTime(LocalDateTime.of(2024, 7, 24, 18, 0))
          .build();

        Movie m2 = Movie.builder()
          .title("Bez litości2")
          .genre("Akcja")
          .description("Bez litości2 opis")
          .playDateTime(LocalDateTime.of(2024, 9, 10, 20, 30))
          .build();

        Movie m3 = Movie.builder()
                .title("Zabójcze Wesele")
                .genre("Komedia")
                .description("Zabójcze wesele opis")
                .playDateTime(LocalDateTime.of(2024, 9, 10, 20, 30))
                .build();

        Movie m4 = Movie.builder()
                .title("Geostorm")
                .genre("Akcja")
                .description("Geostorm opis")
                .playDateTime(LocalDateTime.of(2024, 9, 10, 20, 30))
                .build();

        Movie m5 = Movie.builder()
                .title("Szybcy i Wścielki")
                .genre("Akcja")
                .description("Szybcy i Wścielki opis")
                .playDateTime(LocalDateTime.of(2024, 9, 10, 20, 30))
                .build();

        movieRepository.save(m1);
        movieRepository.save(m2);
        movieRepository.save(m3);
        movieRepository.save(m4);
        movieRepository.save(m5);

        Reservation r1 = Reservation.builder()
                .customerEmail("smyku1232@wp.pl")
                .movie(m1)
                .roomNumber(3L)
                .seats(List.of(Reservation.Seat.builder().row(1).placeNumber(10).build())).build();

        Reservation r2 = Reservation.builder()
                .customerEmail("smyku1232@wp.pl")
                .movie(m2)
                .roomNumber(7L)
                .seats(List.of(Reservation.Seat.builder().row(3).placeNumber(2).build(), Reservation.Seat.builder().row(3).placeNumber(3).build()))
                .build();

        reservationRepository.save(r1);
        reservationRepository.save(r2);

//        System.out.println(reservationRepository.findByMoviePlayDateTimeBetween(LocalDateTime.of(2024,9, 10, 0, 0), LocalDateTime.of(2024, 9, 13, 0, 0 )));

        System.out.println(ReservationMapper.INSTANCE.reservationToReservationDto(r1));

        System.out.println(MovieMapper.INSTANCE.movieToMovieDto(m1));
    }
}
