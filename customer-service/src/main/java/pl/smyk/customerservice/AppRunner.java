package pl.smyk.customerservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import pl.smyk.customerservice.model.Reservation;

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

        Reservation r1 = Reservation.builder()
                .customerEmail("smyku1232@wp.pl")
                .name("testname1")
                .seats(List.of(Reservation.Seat.builder().row(1).placeNumber(10).build())).build();

        Reservation r2 = Reservation.builder()
                .customerEmail("smyku1232@wp.pl")
                .name("testname2")
                .seats(List.of(Reservation.Seat.builder().row(3).placeNumber(2).build(), Reservation.Seat.builder().row(3).placeNumber(3).build()))
                .build();

        reservationRepository.save(r1);
        reservationRepository.save(r2);
    }
}
