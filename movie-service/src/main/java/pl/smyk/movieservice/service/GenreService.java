package pl.smyk.movieservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.smyk.movieservice.model.Genre;
import pl.smyk.movieservice.repository.GenreRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;

    public List<Genre> findAll() {
        return genreRepository.findAll();
    }

    public Optional<Genre> findById(Long id) {
        return genreRepository.findById(id);
    }

    public Genre save(Genre genre) {
        return genreRepository.save(genre);
    }

    public void deleteById(Long id) {
        genreRepository.deleteById(id);
    }
}
