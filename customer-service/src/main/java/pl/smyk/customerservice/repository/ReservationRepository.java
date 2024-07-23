package pl.smyk.customerservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.smyk.customerservice.model.Reservation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends MongoRepository<Reservation, String> {
    List<Reservation> findByMoviePlayDateTimeBetween(LocalDateTime before, LocalDateTime after);
    Optional<Reservation> findByCustomerEmail(String customerEmail);

}
