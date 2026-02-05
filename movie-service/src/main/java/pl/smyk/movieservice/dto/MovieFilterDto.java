package pl.smyk.movieservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import pl.smyk.movieservice.model.Movie;
import pl.smyk.movieservice.specification.MovieSpecification;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieFilterDto {
    private String title;
    private String genre;
    private Integer minDuration;
    private Integer maxDuration;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate releaseDateFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate releaseDateTo;

    public Specification<Movie> toSpecification() {
        return Specification
                .where(MovieSpecification.hasTitle(title))
                .and(MovieSpecification.hasGenre(genre))
                .and(MovieSpecification.hasDurationGreaterThanOrEqual(minDuration))
                .and(MovieSpecification.hasDurationLessThanOrEqual(maxDuration))
                .and(MovieSpecification.hasReleaseDateAfterOrEqual(releaseDateFrom))
                .and(MovieSpecification.hasReleaseDateBeforeOrEqual(releaseDateTo));
    }
}