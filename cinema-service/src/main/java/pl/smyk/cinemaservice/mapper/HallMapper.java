package pl.smyk.cinemaservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pl.smyk.cinemaservice.dto.CreateHallRequest;
import pl.smyk.cinemaservice.dto.HallDto;
import pl.smyk.cinemaservice.dto.UpdateHallRequest;
import pl.smyk.cinemaservice.model.Hall;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HallMapper {
    @Mapping(source = "cinema.id", target = "cinemaId")
    HallDto toDto(Hall hall);
    List<HallDto> toDtoList(List<Hall> halls);
    @Mapping(target = "cinema", ignore = true) // Set cinema separately
    Hall toEntity(CreateHallRequest createHallRequest);
    @Mapping(target = "cinema", ignore = true) // Set cinema separately
    Hall toEntity(UpdateHallRequest updateHallRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cinema", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "seats", ignore = true)
    @Mapping(target = "screenings", ignore = true)
    void updateHallFromDto(UpdateHallRequest updateHallRequest, @MappingTarget Hall hall);
}