package pl.smyk.cinemaservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.smyk.cinemaservice.dto.CreateSeatRequest;
import pl.smyk.cinemaservice.dto.SeatDto;
import pl.smyk.cinemaservice.dto.UpdateSeatRequest;
import pl.smyk.cinemaservice.exception.HallNotFoundException;
import pl.smyk.cinemaservice.exception.SeatAlreadyExistsException;
import pl.smyk.cinemaservice.exception.SeatNotFoundException;
import pl.smyk.cinemaservice.mapper.SeatMapper;
import pl.smyk.cinemaservice.model.Hall;
import pl.smyk.cinemaservice.model.Seat;
import pl.smyk.cinemaservice.repository.HallRepository;
import pl.smyk.cinemaservice.repository.SeatRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;
    private final HallRepository hallRepository;
    private final SeatMapper seatMapper;

    public List<SeatDto> getAllSeats() {
        return seatMapper.toDtoList(seatRepository.findAll());
    }

    public SeatDto getSeatById(Long id) {
        return seatRepository.findById(id).map(seatMapper::toDto)
                .orElseThrow(() -> new SeatNotFoundException("Seat with id " + id + " not found"));
    }

    @Transactional
    public SeatDto createSeat(CreateSeatRequest request) {
        if (seatRepository.existsByHallIdAndRowAndNumber(request.getHallId(), request.getRow(), request.getNumber())) {
            throw new SeatAlreadyExistsException("Seat with row " + request.getRow() + " and number " + request.getNumber() + " already exists in hall with id " + request.getHallId());
        }
        return hallRepository.findById(request.getHallId())
                .map(hall -> {
                    Seat seat = seatMapper.toEntity(request);
                    seat.setHall(hall);
                    return seatMapper.toDto(seatRepository.save(seat));
                })
                .orElseThrow(() -> new HallNotFoundException("Hall with id " + request.getHallId() + " not found"));
    }

    @Transactional
    public SeatDto updateSeat(UpdateSeatRequest request) {
        return seatRepository.findById(request.getId())
                .flatMap(seat -> hallRepository.findById(request.getHallId())
                        .map(hall -> {
                            seatMapper.updateSeatFromDto(request, seat);
                            seat.setHall(hall);
                            return seatMapper.toDto(seatRepository.save(seat));
                        }))
                .orElseThrow(() -> new SeatNotFoundException("Seat with id " + request.getId() + " not found or Hall with id " + request.getHallId() + " not found"));
    }

    public void deleteSeat(Long id) {
        if (!seatRepository.existsById(id)) {
            throw new SeatNotFoundException("Seat with id " + id + " not found");
        }
        seatRepository.deleteById(id);
    }
}
