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

import java.util.*;

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
    public List<SeatDto> createSeats(List<CreateSeatRequest> requests) {

        Set<String> uniqueCheck = new HashSet<>();
        for (CreateSeatRequest r : requests) {
            String key = r.getHallId() + "_" + r.getRow() + "_" + r.getNumber();
            if (!uniqueCheck.add(key)) {
                throw new SeatAlreadyExistsException(
                        "Duplicate seat in request: hall=" + r.getHallId()
                                + ", row=" + r.getRow()
                                + ", number=" + r.getNumber()
                );
            }
        }

        List<Seat> seatsToSave = new ArrayList<>();

        for (CreateSeatRequest request : requests) {

            if (seatRepository.existsByHallIdAndRowAndNumber(
                    request.getHallId(),
                    request.getRow(),
                    request.getNumber())) {

                throw new SeatAlreadyExistsException(
                        "Seat with row " + request.getRow()
                                + " and number " + request.getNumber()
                                + " already exists in hall with id "
                                + request.getHallId());
            }

            Hall hall = hallRepository.findById(request.getHallId())
                    .orElseThrow(() ->
                            new HallNotFoundException(
                                    "Hall with id " + request.getHallId() + " not found"));

            Seat seat = seatMapper.toEntity(request);
            seat.setHall(hall);

            seatsToSave.add(seat);
        }

        return seatRepository.saveAll(seatsToSave)
                .stream()
                .map(seatMapper::toDto)
                .toList();
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
