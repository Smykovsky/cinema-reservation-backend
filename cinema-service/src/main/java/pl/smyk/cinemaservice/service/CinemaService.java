package pl.smyk.cinemaservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.smyk.cinemaservice.dto.CinemaDto;
import pl.smyk.cinemaservice.dto.CreateCinemaRequest;
import pl.smyk.cinemaservice.dto.UpdateCinemaRequest;
import pl.smyk.cinemaservice.exception.CinemaAlreadyExistsException;
import pl.smyk.cinemaservice.exception.CinemaNotFoundException;
import pl.smyk.cinemaservice.mapper.CinemaMapper;
import pl.smyk.cinemaservice.model.Cinema;
import pl.smyk.cinemaservice.repository.CinemaRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CinemaService {

    private final CinemaRepository cinemaRepository;
    private final CinemaMapper cinemaMapper;

    public List<CinemaDto> getAllCinemas() {
        return cinemaMapper.toDtoList(cinemaRepository.findAll());
    }

    public CinemaDto getCinemaById(Long id) {
        return cinemaRepository.findById(id).map(cinemaMapper::toDto)
                .orElseThrow(() -> new CinemaNotFoundException("Cinema with id " + id + " not found"));
    }

    @Transactional
    public CinemaDto createCinema(CreateCinemaRequest request) {
        if (cinemaRepository.existsByName(request.getName())) {
            throw new CinemaAlreadyExistsException("Cinema with name " + request.getName() + " already exists");
        }
        Cinema cinema = cinemaMapper.toEntity(request);
        return cinemaMapper.toDto(cinemaRepository.save(cinema));
    }

    @Transactional
    public CinemaDto updateCinema(UpdateCinemaRequest request) {
        return cinemaRepository.findById(request.getId())
                .map(cinema -> {
                    cinemaMapper.updateCinemaFromDto(request, cinema);
                    return cinemaMapper.toDto(cinemaRepository.save(cinema));
                })
                .orElseThrow(() -> new CinemaNotFoundException("Cinema with id " + request.getId() + " not found"));
    }

    public void deleteCinema(Long id) {
        if (!cinemaRepository.existsById(id)) {
            throw new CinemaNotFoundException("Cinema with id " + id + " not found");
        }
        cinemaRepository.deleteById(id);
    }
}
