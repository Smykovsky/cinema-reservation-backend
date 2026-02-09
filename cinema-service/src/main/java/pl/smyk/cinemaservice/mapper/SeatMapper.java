package pl.smyk.cinemaservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pl.smyk.cinemaservice.dto.CreateSeatRequest;
import pl.smyk.cinemaservice.dto.SeatDto;
import pl.smyk.cinemaservice.dto.UpdateSeatRequest;
import pl.smyk.cinemaservice.model.Seat;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SeatMapper {
    @Mapping(source = "hall.id", target = "hallId")
    SeatDto toDto(Seat seat);
    List<SeatDto> toDtoList(List<Seat> seats);
    @Mapping(target = "hall", ignore = true) // Set hall separately
    Seat toEntity(CreateSeatRequest createSeatRequest);
    @Mapping(target = "hall", ignore = true) // Set hall separately
    Seat toEntity(UpdateSeatRequest updateSeatRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hall", ignore = true)
    void updateSeatFromDto(UpdateSeatRequest updateSeatRequest, @MappingTarget Seat seat);
}