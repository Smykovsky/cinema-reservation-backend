package pl.smyk.movieservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.smyk.movieservice.model.Genre;
import pl.smyk.movieservice.service.GenreService;

import java.util.List;

@RestController
@RequestMapping("/api/genre")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping()
    public ResponseEntity<List<Genre>> getAllGenres() {
        return ResponseEntity.ok(genreService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Genre> getGenreById(@PathVariable Long id) {
        return genreService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping()
    public ResponseEntity<Genre> createGenre(@RequestBody Genre genre) {
        return ResponseEntity.ok(genreService.save(genre));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Genre> updateGenre(@PathVariable Long id, @RequestBody Genre genre) {
        return genreService.findById(id)
                .map(existingGenre -> {
                    genre.setId(id);
                    return ResponseEntity.ok(genreService.save(genre));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGenre(@PathVariable Long id) {
        genreService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
