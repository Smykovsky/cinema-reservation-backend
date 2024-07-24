package pl.smyk.customerservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.smyk.customerservice.dto.ReservationDto;
import pl.smyk.customerservice.dto.SeatDto;
import pl.smyk.customerservice.dto.response.ReservationResponse;
import pl.smyk.customerservice.mapper.ReservationMapper;
import pl.smyk.customerservice.model.Reservation;
import pl.smyk.customerservice.repository.ReservationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;


    public void saveReservation(Reservation reservation){
        reservationRepository.save(reservation);
    }

    public Reservation findById(String id) {
        return reservationRepository.findById(id).orElseThrow();
    }

    public List<ReservationDto> getReservationsByCustomerEmail(String customerEmail) {
        return reservationRepository.findAll().stream()
                .filter(reservation -> reservation.getCustomerEmail().equals(customerEmail))
                .map(ReservationMapper.INSTANCE::reservationToReservationDto)
                .collect(Collectors.toList());
    }

    public ReservationDto getReservationByIdAndCustomerEmailIfExists(String reservationId, String customerEmail) {
        Reservation reservation = reservationRepository.findByIdAndCustomerEmail(reservationId, customerEmail);
        if (reservation == null) {
            return null;
        }
        return ReservationMapper.INSTANCE.reservationToReservationDto(reservation);
    }

    public List<ReservationDto> getReservationsAtSelectedPlayTime(Long roomNumber, LocalDateTime selectedPlayTime) {
        List<Reservation> byRoomNumberAndSelectedPlayTime = reservationRepository.findByRoomNumberAndSelectedPlayTime(roomNumber, selectedPlayTime);
        return byRoomNumberAndSelectedPlayTime.stream().map(ReservationMapper.INSTANCE::reservationToReservationDto).collect(Collectors.toList());
    }

    public List<SeatDto> getOccupiedSeatsByRoomNumberAndSelectedPlayTime(Long roomNumber, LocalDateTime selectedPlayTime) {
        List<ReservationDto> reservationsAtSelectedPlayTime = getReservationsAtSelectedPlayTime(roomNumber, selectedPlayTime);
        return reservationsAtSelectedPlayTime.stream()
                .flatMap(reservationDto -> reservationDto.getSeats().stream())
                .collect(Collectors.toList());
    }

    public boolean isSeatOccupied(Long roomNumber, LocalDateTime selectedPlayTime, SeatDto seatDto) {
        List<SeatDto> occupiedSeats = getOccupiedSeatsByRoomNumberAndSelectedPlayTime(roomNumber, selectedPlayTime);
        if (occupiedSeats.stream().anyMatch(occupiedSeat -> occupiedSeat.getRow() == seatDto.getRow() && occupiedSeat.getPlaceNumber() == seatDto.getPlaceNumber())) {
            return true;
        }
        return false;
    }

    public boolean isSeatsOccupied(Long roomNumber, LocalDateTime selectedPlayTime, List<SeatDto> seatDtoList) {
        List<SeatDto> occupiedSeats = getOccupiedSeatsByRoomNumberAndSelectedPlayTime(roomNumber, selectedPlayTime);
        for (SeatDto seatDto : seatDtoList) {
            if (occupiedSeats.stream().anyMatch(occupiedSeat ->
                    occupiedSeat.getRow() == seatDto.getRow() &&
                            occupiedSeat.getPlaceNumber() == seatDto.getPlaceNumber()
            )) {
                return true;
            }
        }
        return false;
    }

//    public ReservationResponse createReservation() {
//        //handle reservation bussines logic
//        return new ReservationResponse();
//    }


}
