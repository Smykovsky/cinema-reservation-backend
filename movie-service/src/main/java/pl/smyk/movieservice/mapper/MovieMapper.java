package pl.smyk.movieservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import pl.smyk.movieservice.dto.CreateMovieRequest;
import pl.smyk.movieservice.dto.MovieDto;
import pl.smyk.movieservice.dto.UpdateMovieRequest;
import pl.smyk.movieservice.model.Movie;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MovieMapper {
    MovieMapper INSTANCE = Mappers.getMapper(MovieMapper.class);

    MovieDto toDto(Movie movie);
    List<MovieDto> toDtoList(List<Movie> movies);
    Movie toEntity(CreateMovieRequest createMovieRequest);
    Movie toEntity(UpdateMovieRequest updateMovieRequest);
}
