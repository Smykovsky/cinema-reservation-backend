package pl.smyk.cinemaservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pl.smyk.cinemaservice.dto.CinemaDto;
import pl.smyk.cinemaservice.dto.CreateCinemaRequest;
import pl.smyk.cinemaservice.dto.UpdateCinemaRequest;
import pl.smyk.cinemaservice.model.Cinema;

import java.util.List;

@Mapper(componentModel = "spring", uses = HallMapper.class) // Assuming HallMapper will be created
public interface CinemaMapper {
    @Mapping(target = "halls", source = "halls")
    CinemaDto toDto(Cinema cinema);
    List<CinemaDto> toDtoList(List<Cinema> cinemas);
    Cinema toEntity(CreateCinemaRequest createCinemaRequest);
    Cinema toEntity(UpdateCinemaRequest updateCinemaRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "halls", ignore = true)
    void updateCinemaFromDto(UpdateCinemaRequest updateCinemaRequest, @MappingTarget Cinema cinema);
}