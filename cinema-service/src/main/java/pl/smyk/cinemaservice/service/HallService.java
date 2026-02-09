package pl.smyk.cinemaservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.smyk.cinemaservice.dto.CreateHallRequest;
import pl.smyk.cinemaservice.dto.HallDto;
import pl.smyk.cinemaservice.dto.UpdateHallRequest;
import pl.smyk.cinemaservice.exception.CinemaNotFoundException;
import pl.smyk.cinemaservice.exception.HallAlreadyExistsException;
import pl.smyk.cinemaservice.exception.HallNotFoundException;
import pl.smyk.cinemaservice.mapper.HallMapper;
import pl.smyk.cinemaservice.model.Cinema;
import pl.smyk.cinemaservice.model.Hall;
import pl.smyk.cinemaservice.repository.CinemaRepository;
import pl.smyk.cinemaservice.repository.HallRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HallService {

    private final HallRepository hallRepository;
    private final CinemaRepository cinemaRepository;
    private final HallMapper hallMapper;

    public List<HallDto> getAllHalls() {
        return hallMapper.toDtoList(hallRepository.findAll());
    }

    public HallDto getHallById(Long id) {
        return hallRepository.findById(id).map(hallMapper::toDto)
                .orElseThrow(() -> new HallNotFoundException("Hall with id " + id + " not found"));
    }

    @Transactional
    public HallDto createHall(CreateHallRequest request) {
        if (hallRepository.existsByNameAndCinemaId(request.getName(), request.getCinemaId())) {
            throw new HallAlreadyExistsException("Hall with name " + request.getName() + " already exists in cinema with id " + request.getCinemaId());
        }
        return cinemaRepository.findById(request.getCinemaId())
                .map(cinema -> {
                    Hall hall = hallMapper.toEntity(request);
                    hall.setCinema(cinema);
                    return hallMapper.toDto(hallRepository.save(hall));
                })
                .orElseThrow(() -> new CinemaNotFoundException("Cinema with id " + request.getCinemaId() + " not found"));
    }

    @Transactional
    public HallDto updateHall(UpdateHallRequest request) {
        return hallRepository.findById(request.getId())
                .flatMap(hall -> cinemaRepository.findById(request.getCinemaId())
                        .map(cinema -> {
                            hallMapper.updateHallFromDto(request, hall);
                            hall.setCinema(cinema);
                            return hallMapper.toDto(hallRepository.save(hall));
                        }))
                .orElseThrow(() -> new HallNotFoundException("Hall with id " + request.getId() + " not found or Cinema with id " + request.getCinemaId() + " not found"));
    }

    public void deleteHall(Long id) {
        if (!hallRepository.existsById(id)) {
            throw new HallNotFoundException("Hall with id " + id + " not found");
        }
        hallRepository.deleteById(id);
    }
}
