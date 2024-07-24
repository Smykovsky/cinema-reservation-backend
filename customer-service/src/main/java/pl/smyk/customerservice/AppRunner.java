package pl.smyk.customerservice;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import pl.smyk.customerservice.dto.MovieDto;
import pl.smyk.customerservice.dto.SeatDto;
import pl.smyk.customerservice.mapper.MovieMapper;
import pl.smyk.customerservice.mapper.ReservationMapper;
import pl.smyk.customerservice.model.Movie;
import pl.smyk.customerservice.model.PlayTime;
import pl.smyk.customerservice.model.Reservation;
import pl.smyk.customerservice.repository.MovieRepository;
import pl.smyk.customerservice.repository.ReservationRepository;
import pl.smyk.customerservice.service.MovieManagementService;
import pl.smyk.customerservice.service.MovieService;
import pl.smyk.customerservice.service.ReservationService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static pl.smyk.customerservice.model.PlayTime.HOUR_12;
import static pl.smyk.customerservice.model.PlayTime.HOUR_18;

@Component
public class AppRunner implements CommandLineRunner {
    @Autowired
    private MongoTemplate template;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private MovieService movieService;
    @Autowired
    private MovieManagementService movieManagementService;
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
          .playDates(List.of(LocalDate.of(2024, 7, 1)))
          .build();

        Movie m2 = Movie.builder()
          .title("Bez litości2")
          .genre("Akcja")
          .description("Bez litości2 opis")
          .playDates(List.of(LocalDate.of(2024, 7, 2)))
          .build();

        Movie m3 = Movie.builder()
                .title("Zabójcze Wesele")
                .genre("Komedia")
                .description("Zabójcze wesele opis")
                .playDates(List.of(LocalDate.of(2024, 7, 3)))
                .build();

        Movie m4 = Movie.builder()
                .title("Geostorm")
                .genre("Akcja")
                .description("Geostorm opis")
                .playDates(List.of(LocalDate.of(2024, 7, 4)))
                .build();

        Movie m5 = Movie.builder()
                .title("Szybcy i Wścielki")
                .genre("Akcja")
                .description("Szybcy i Wścielki opis")
                .playDates(List.of(LocalDate.of(2024, 7, 5)))
                .build();

        movieRepository.save(m1);
        movieRepository.save(m2);
        movieRepository.save(m3);
        movieRepository.save(m4);
        movieRepository.save(m5);

        Reservation r1 = Reservation.builder()
                .customerEmail("smyku1232@wp.pl")
                .movie(m1)
                .selectedPlayTime(LocalDateTime.of(LocalDate.of(2024, 8, 1), HOUR_18.getPlayTime()))
                .roomNumber(3L)
                .seats(List.of(Reservation.Seat.builder().row(1).placeNumber(10).build())).build();

        Reservation r2 = Reservation.builder()
                .customerEmail("smyku1232@wp.pl")
                .movie(m2)
                .selectedPlayTime(LocalDateTime.of(LocalDate.of(2024, 9, 10), HOUR_12.getPlayTime()))
                .roomNumber(7L)
                .seats(List.of(Reservation.Seat.builder().row(3).placeNumber(2).build(), Reservation.Seat.builder().row(3).placeNumber(3).build()))
                .build();

        reservationRepository.save(r1);
        reservationRepository.save(r2);

        System.out.println(ReservationMapper.INSTANCE.reservationToReservationDto(r2));

        System.out.println(MovieMapper.INSTANCE.movieToMovieDto(m1));

        LocalDateTime ldt = LocalDateTime.of(LocalDate.of(2024, 9, 10), HOUR_12.getPlayTime());
        SeatDto seatDto = new SeatDto(3, 3);
        System.out.println(seatDto);


        System.out.println(reservationService.getReservationsAtSelectedPlayTime(7L, ldt));
        System.out.println(reservationService.getOccupiedSeatsByRoomNumberAndSelectedPlayTime(7L, ldt));
        System.out.println(reservationService.isSeatOccupied(7L, ldt, seatDto));
        System.out.println(reservationService.getReservationByIdAndCustomerEmailIfExists("66a0d62bc810a642fa02a1df", "smyku1232@wp.pl"));

        LocalDate ld1 = LocalDate.of(2025, 7, 2);
        LocalDate ld2 = LocalDate.of(2025, 7, 2);


    }
}
