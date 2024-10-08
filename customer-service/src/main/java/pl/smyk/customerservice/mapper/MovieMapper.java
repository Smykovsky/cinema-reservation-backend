package pl.smyk.customerservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import pl.smyk.customerservice.dto.MovieDto;
import pl.smyk.customerservice.model.Movie;

@Mapper
public interface MovieMapper {
    MovieMapper INSTANCE = Mappers.getMapper(MovieMapper.class);

    @Mapping(source = "playDates", target = "playDates")
    @Mapping(source = "playTimes", target = "playTimes")
    MovieDto movieToMovieDto(Movie movie);

}
