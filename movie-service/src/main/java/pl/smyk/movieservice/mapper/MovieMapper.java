package pl.smyk.movieservice.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import pl.smyk.movieservice.dto.*;
import pl.smyk.movieservice.model.Genre;
import pl.smyk.movieservice.model.Movie;

import java.util.List;

@Mapper
public interface MovieMapper {
    MovieMapper INSTANCE = Mappers.getMapper(MovieMapper.class);

    MovieDto movieToMovieDto(Movie movie);

    List<MovieDto> moviesToMoviesDto(List<Movie> movies);

    GenreDto genreToGenreDto(Genre genre);
}