package pl.smyk.customerservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.smyk.customerservice.model.Movie;

public interface MovieRepository extends MongoRepository<Movie, String> {
}
