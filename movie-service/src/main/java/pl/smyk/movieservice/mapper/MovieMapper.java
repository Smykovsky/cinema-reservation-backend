package pl.smyk.movieservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import pl.smyk.common.dto.MovieDto;
import pl.smyk.movieservice.dto.CreateMovieRequest;
import pl.smyk.movieservice.dto.UpdateMovieRequest;
import pl.smyk.movieservice.model.Genre;
import pl.smyk.movieservice.model.Movie;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    @Mapping(target = "genres", source = "genres", qualifiedByName = "genresToNames")
    MovieDto toDto(Movie movie);

    List<MovieDto> toDtoList(List<Movie> movies);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "genres", ignore = true)
    Movie toEntity(CreateMovieRequest createMovieRequest);

    @Mapping(target = "genres", ignore = true)
    Movie toEntity(UpdateMovieRequest updateMovieRequest);

    @Named("genresToNames")
    default Set<String> genresToNames(Set<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return Collections.emptySet();
        }
        return genres.stream()
                .map(Genre::getName)
                .collect(Collectors.toSet());
    }
}