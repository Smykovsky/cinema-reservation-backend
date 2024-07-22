package pl.smyk.customerservice;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.smyk.customerservice.model.Reservation;

public interface ReservationRepository extends MongoRepository<Reservation, String> {
}
