package pl.smyk.movieservice.specification;

import org.springframework.data.jpa.domain.Specification;
import pl.smyk.movieservice.model.Movie;

import java.time.LocalDate;

public class MovieSpecification {

    public static Specification<Movie> hasTitle(String title) {
        return (root, query, criteriaBuilder) ->
                title == null ? null : criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("title")),
                        "%" + title.toLowerCase() + "%"
                );
    }

    public static Specification<Movie> hasGenre(String genre) {
        return (root, query, criteriaBuilder) ->
                genre == null ? null : criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("genre")),
                        genre.toLowerCase()
                );
    }

    public static Specification<Movie> hasDurationGreaterThanOrEqual(Integer minDuration) {
        return (root, query, criteriaBuilder) ->
                minDuration == null ? null : criteriaBuilder.greaterThanOrEqualTo(
                        root.get("duration"),
                        minDuration
                );
    }

    public static Specification<Movie> hasDurationLessThanOrEqual(Integer maxDuration) {
        return (root, query, criteriaBuilder) ->
                maxDuration == null ? null : criteriaBuilder.lessThanOrEqualTo(
                        root.get("duration"),
                        maxDuration
                );
    }

    public static Specification<Movie> hasReleaseDateAfterOrEqual(LocalDate releaseDateFrom) {
        return (root, query, criteriaBuilder) ->
                releaseDateFrom == null ? null : criteriaBuilder.greaterThanOrEqualTo(
                        root.get("releaseDate"),
                        releaseDateFrom
                );
    }

    public static Specification<Movie> hasReleaseDateBeforeOrEqual(LocalDate releaseDateTo) {
        return (root, query, criteriaBuilder) ->
                releaseDateTo == null ? null : criteriaBuilder.lessThanOrEqualTo(
                        root.get("releaseDate"),
                        releaseDateTo
                );
    }
}