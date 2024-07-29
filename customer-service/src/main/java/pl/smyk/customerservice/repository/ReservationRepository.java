package pl.smyk.customerservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.smyk.customerservice.model.Reservation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends MongoRepository<Reservation, String> {
    Optional<Reservation> findByCustomerEmail(String customerEmail);
    List<Reservation> findByRoomNumberAndSelectedPlayTime(Long roomNumber, LocalDateTime selectedPlayTime);
    List<Reservation> findBySelectedPlayTimeBetween(LocalDateTime start, LocalDateTime end);
    Reservation findByIdAndCustomerEmail(String id, String customerEmail);

}
