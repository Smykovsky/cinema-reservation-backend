package pl.smyk.authservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.smyk.authservice.model.Customer;

import java.util.Optional;

public interface CustomerRepository extends MongoRepository<Customer, String> {
    Optional<Customer> findByEmail(String email);
}
