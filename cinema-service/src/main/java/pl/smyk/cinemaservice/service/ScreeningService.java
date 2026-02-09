package pl.smyk.cinemaservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.smyk.cinemaservice.dto.CreateScreeningRequest;
import pl.smyk.cinemaservice.dto.ScreeningDto;
import pl.smyk.cinemaservice.dto.UpdateScreeningRequest;
import pl.smyk.cinemaservice.exception.HallNotFoundException;
import pl.smyk.cinemaservice.exception.ScreeningAlreadyExistsException;
import pl.smyk.cinemaservice.exception.ScreeningNotFoundException;
import pl.smyk.cinemaservice.mapper.ScreeningMapper;
import pl.smyk.cinemaservice.model.Hall;
import pl.smyk.cinemaservice.model.Screening;
import pl.smyk.cinemaservice.repository.HallRepository;
import pl.smyk.cinemaservice.repository.ScreeningRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScreeningService {

    private final ScreeningRepository screeningRepository;
    private final HallRepository hallRepository;
    private final ScreeningMapper screeningMapper;

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
                    return screeningMapper.toDto(screeningRepository.save(screening));
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
}