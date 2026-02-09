package pl.smyk.cinemaservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.smyk.cinemaservice.dto.ScreeningSeatDto;
import pl.smyk.cinemaservice.model.ScreeningSeat;

@Mapper(componentModel = "spring")
public interface ScreeningSeatMapper {

    @Mapping(source = "seat.id", target = "seatId")
    @Mapping(source = "seat.row", target = "row")
    @Mapping(source = "seat.number", target = "number")
    @Mapping(source = "seat.seatType", target = "seatType")
    ScreeningSeatDto toDto(ScreeningSeat screeningSeat);
}