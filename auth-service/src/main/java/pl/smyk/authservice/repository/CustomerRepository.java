package pl.smyk.authservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.smyk.authservice.model.User;

import java.util.Optional;

public interface CustomerRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
}
