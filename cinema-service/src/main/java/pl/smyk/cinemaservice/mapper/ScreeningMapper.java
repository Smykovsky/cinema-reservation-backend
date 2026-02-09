package pl.smyk.cinemaservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pl.smyk.cinemaservice.dto.CreateScreeningRequest;
import pl.smyk.cinemaservice.dto.ScreeningDto;
import pl.smyk.cinemaservice.dto.UpdateScreeningRequest;
import pl.smyk.cinemaservice.model.Screening;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ScreeningMapper {
    @Mapping(source = "hall.id", target = "hallId")
    ScreeningDto toDto(Screening screening);
    List<ScreeningDto> toDtoList(List<Screening> screenings);
    @Mapping(target = "hall", ignore = true) // Set hall separately
    @Mapping(target = "screeningSeats", ignore = true)
    Screening toEntity(CreateScreeningRequest createScreeningRequest);
    @Mapping(target = "hall", ignore = true) // Set hall separately
    @Mapping(target = "screeningSeats", ignore = true)
    Screening toEntity(UpdateScreeningRequest updateScreeningRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hall", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "screeningSeats", ignore = true)
    void updateScreeningFromDto(UpdateScreeningRequest updateScreeningRequest, @MappingTarget Screening screening);
}