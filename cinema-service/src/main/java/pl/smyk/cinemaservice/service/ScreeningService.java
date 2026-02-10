package pl.smyk.cinemaservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.smyk.cinemaservice.dto.CreateScreeningRequest;
import pl.smyk.cinemaservice.dto.ScreeningDto;
import pl.smyk.cinemaservice.dto.ScreeningSeatDto;
import pl.smyk.cinemaservice.dto.UpdateScreeningRequest;
import pl.smyk.cinemaservice.exception.HallHasNoSeatsException;
import pl.smyk.cinemaservice.exception.HallNotFoundException;
import pl.smyk.cinemaservice.exception.ScreeningAlreadyExistsException;
import pl.smyk.cinemaservice.exception.ScreeningNotFoundException;
import pl.smyk.cinemaservice.mapper.ScreeningMapper;
import pl.smyk.cinemaservice.mapper.ScreeningSeatMapper;
import pl.smyk.cinemaservice.model.*;
import pl.smyk.cinemaservice.repository.HallRepository;
import pl.smyk.cinemaservice.repository.ScreeningRepository;
import pl.smyk.cinemaservice.repository.ScreeningSeatRepository;
import pl.smyk.cinemaservice.repository.SeatRepository; // Added SeatRepository import

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScreeningService {

    private final ScreeningRepository screeningRepository;
    private final HallRepository hallRepository;
    private final SeatRepository seatRepository; // Added SeatRepository
    private final ScreeningSeatRepository screeningSeatRepository;
    private final ScreeningMapper screeningMapper;
    private final ScreeningSeatMapper screeningSeatMapper;

    public List<ScreeningDto> getAllScreenings() {
        return screeningMapper.toDtoList(screeningRepository.findAll());
    }

    public ScreeningDto getScreeningById(Long id) {
        return screeningRepository.findById(id).map(screeningMapper::toDto)
                .orElseThrow(() -> new ScreeningNotFoundException("Screening with id " + id + " not found"));
    }

    @Transactional
    public ScreeningDto createScreening(CreateScreeningRequest request) {
        if (!screeningRepository.findByHallIdAndStartTimeBeforeAndEndTimeAfter(request.getHallId(), request.getEndTime(), request.getStartTime()).isEmpty()) {
            throw new ScreeningAlreadyExistsException("Screening overlaps with another screening in hall with id " + request.getHallId());
        }

        return hallRepository.findById(request.getHallId())
                .map(hall -> {
                    Screening screening = screeningMapper.toEntity(request);
                    screening.setHall(hall);
                    // Najpierw zapisz Screening, aby otrzymać ID, które jest potrzebne do ScreeningSeatId
                    Screening savedScreening = screeningRepository.save(screening);

                    List<Seat> seatsInHall = seatRepository.findByHall(hall);
                    if (seatsInHall.isEmpty()) {
                        throw new HallHasNoSeatsException("Hall with id " + hall.getId() + " has no seats defined. Cannot create screening.");
                    }

                    // Utwórz ScreeningSeat entries dla każdego miejsca w sali i dodaj je do Screening
                    List<ScreeningSeat> newScreeningSeats = seatsInHall.stream()
                            .map(seat -> ScreeningSeat.builder()
                                    .screening(savedScreening) // Przypisz zapisany seans
                                    .seat(seat)
                                    .status(SeatStatus.AVAILABLE)
                                    .build())
                            .toList();

                    savedScreening.getScreeningSeats().addAll(newScreeningSeats);
                    return screeningMapper.toDto(savedScreening);
                })
                .orElseThrow(() -> new HallNotFoundException("Hall with id " + request.getHallId() + " not found"));
    }

    @Transactional
    public ScreeningDto updateScreening(UpdateScreeningRequest request) {
        return screeningRepository.findById(request.getId())
                .flatMap(screening -> hallRepository.findById(request.getHallId())
                        .map(hall -> {
                            screeningMapper.updateScreeningFromDto(request, screening);
                            screening.setHall(hall);
                            return screeningMapper.toDto(screeningRepository.save(screening));
                        }))
                .orElseThrow(() -> new ScreeningNotFoundException("Screening with id " + request.getId() + " not found or Hall with id " + request.getHallId() + " not found"));
    }

    public void deleteScreening(Long id) {
        if (!screeningRepository.existsById(id)) {
            throw new ScreeningNotFoundException("Screening with id " + id + " not found");
        }
        screeningRepository.deleteById(id);
    }

    public List<ScreeningSeatDto> getAvailableSeatsForScreening(Long screeningId) {
        Screening screening = screeningRepository.findById(screeningId)
                .orElseThrow(() -> new ScreeningNotFoundException("Screening with id " + screeningId + " not found"));

        return screeningSeatRepository.findByScreening(screening).stream()
                .filter(ScreeningSeat::isAvailable)
                .map(screeningSeatMapper::toDto)
                .toList();
    }
}