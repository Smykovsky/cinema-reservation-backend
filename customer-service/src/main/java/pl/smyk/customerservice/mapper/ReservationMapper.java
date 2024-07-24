package pl.smyk.customerservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import pl.smyk.customerservice.dto.ReservationDto;
import pl.smyk.customerservice.model.Reservation;

@Mapper
public interface ReservationMapper {
    ReservationMapper INSTANCE = Mappers.getMapper(ReservationMapper.class);

    @Mapping(source = "seats", target ="seats")
    @Mapping(source = "movie", target ="movie")
    ReservationDto reservationToReservationDto(Reservation reservation);

}
