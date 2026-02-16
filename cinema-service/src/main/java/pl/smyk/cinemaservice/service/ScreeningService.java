package pl.smyk.cinemaservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.smyk.cinemaservice.dto.*;
import pl.smyk.cinemaservice.exception.HallHasNoSeatsException;
import pl.smyk.cinemaservice.exception.HallNotFoundException;
import pl.smyk.cinemaservice.exception.ScreeningAlreadyExistsException;
import pl.smyk.cinemaservice.exception.ScreeningNotFoundException;
import pl.smyk.cinemaservice.exception.SeatNotAvailableException; // Import new exception
import pl.smyk.cinemaservice.mapper.ScreeningMapper;
import pl.smyk.cinemaservice.mapper.ScreeningSeatMapper;
import pl.smyk.cinemaservice.model.*;
import pl.smyk.cinemaservice.repository.HallRepository;
import pl.smyk.cinemaservice.repository.ScreeningRepository;
import pl.smyk.cinemaservice.repository.ScreeningSeatRepository;
import pl.smyk.cinemaservice.repository.SeatRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScreeningService {

    private final ScreeningRepository screeningRepository;
    private final HallRepository hallRepository;
    private final SeatRepository seatRepository;
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

    public List<ScreeningDto> getScreeningsByCinemaAndDate(Long cinemaId, LocalDate date) {
        Instant startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endOfDay = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        return screeningRepository.findByCinemaIdAndDateRange(cinemaId, startOfDay, endOfDay)
                .stream()
                .map(screeningMapper::toDto)
                .toList();
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
                    Screening savedScreening = screeningRepository.save(screening);

                    List<Seat> seatsInHall = seatRepository.findByHall(hall);
                    if (seatsInHall.isEmpty()) {
                        throw new HallHasNoSeatsException("Hall with id " + hall.getId() + " has no seats defined. Cannot create screening.");
                    }

                    List<ScreeningSeat> newScreeningSeats = seatsInHall.stream()
                            .map(seat -> ScreeningSeat.builder()
                                    .screening(savedScreening)
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

    @Transactional
    public void reserveSeats(Long screeningId, ReserveSeatsRequest request) {
        Screening screening = screeningRepository.findById(screeningId)
                .orElseThrow(() -> new ScreeningNotFoundException("Screening with id " + screeningId + " not found"));

        Set<Long> requestedSeatIds = request.getSeatIds().stream().collect(Collectors.toSet());

        List<ScreeningSeat> seatsToReserve = screeningSeatRepository.findByScreeningAndSeatIdIn(screening, requestedSeatIds);

        if (seatsToReserve.size() != requestedSeatIds.size()) {
            throw new SeatNotAvailableException("One or more requested seats not found for screening " + screeningId);
        }

        for (ScreeningSeat screeningSeat : seatsToReserve) {
            if (screeningSeat.getStatus() != SeatStatus.AVAILABLE) {
                throw new SeatNotAvailableException("Seat " + screeningSeat.getSeat().getId() + " is not available.");
            }
            screeningSeat.setStatus(SeatStatus.RESERVED);
        }
        screeningSeatRepository.saveAll(seatsToReserve);
    }

    public List<ScreeningSeatDto> getAvailableSeatsForScreening(Long screeningId) {
        Screening screening = screeningRepository.findById(screeningId)
                .orElseThrow(() -> new ScreeningNotFoundException("Screening with id " + screeningId + " not found"));

        return screeningSeatRepository.findByScreening(screening).stream()
                .filter(ScreeningSeat::isAvailable)
                .map(screeningSeatMapper::toDto)
                .toList();
    }

    public List<ScreeningSeatDto> getAllSeatsForScreening(Long screeningId) {
        Screening screening = screeningRepository.findById(screeningId)
                .orElseThrow(() -> new ScreeningNotFoundException("Screening with id " + screeningId + " not found"));

        return screeningSeatRepository.findByScreening(screening).stream()
                .map(screeningSeatMapper::toDto)
                .toList();
    }

        @Transactional

        public void confirmSeats(Long screeningId, ChangeSeatStatusRequest request) {

            Screening screening = screeningRepository.findById(screeningId)

                    .orElseThrow(() -> new ScreeningNotFoundException("Screening with id " + screeningId + " not found"));

    

            Set<Long> requestedSeatIds = request.getSeatIds().stream().collect(Collectors.toSet());

    

            List<ScreeningSeat> seatsToConfirm = screeningSeatRepository.findByScreeningAndSeatIdIn(screening, requestedSeatIds);

    

            if (seatsToConfirm.size() != requestedSeatIds.size()) {

                throw new SeatNotAvailableException("One or more requested seats not found for screening " + screeningId);

            }

    

            for (ScreeningSeat screeningSeat : seatsToConfirm) {

                if (screeningSeat.getStatus() != SeatStatus.RESERVED) {

                    throw new SeatNotAvailableException("Seat " + screeningSeat.getSeat().getId() + " is not in RESERVED state and cannot be confirmed.");

                }

                screeningSeat.setStatus(SeatStatus.SOLD);

            }

            screeningSeatRepository.saveAll(seatsToConfirm);

        }

    

        @Transactional

        public void releaseSeats(Long screeningId, ChangeSeatStatusRequest request) {

            Screening screening = screeningRepository.findById(screeningId)

                    .orElseThrow(() -> new ScreeningNotFoundException("Screening with id " + screeningId + " not found"));

    

            Set<Long> requestedSeatIds = request.getSeatIds().stream().collect(Collectors.toSet());

    

            List<ScreeningSeat> seatsToRelease = screeningSeatRepository.findByScreeningAndSeatIdIn(screening, requestedSeatIds);

    

            if (seatsToRelease.size() != requestedSeatIds.size()) {

                throw new SeatNotAvailableException("One or more requested seats not found for screening " + screeningId);

            }

    

            for (ScreeningSeat screeningSeat : seatsToRelease) {

                if (screeningSeat.getStatus() != SeatStatus.RESERVED && screeningSeat.getStatus() != SeatStatus.SOLD) {

                    throw new SeatNotAvailableException("Seat " + screeningSeat.getSeat().getId() + " is not in RESERVED or SOLD state and cannot be released.");

                }

                screeningSeat.setStatus(SeatStatus.AVAILABLE);

            }

            screeningSeatRepository.saveAll(seatsToRelease);

        }

    }

    