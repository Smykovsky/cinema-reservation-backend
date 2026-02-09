package pl.smyk.cinemaservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.smyk.cinemaservice.dto.CinemaDto;
import pl.smyk.cinemaservice.dto.CreateCinemaRequest;
import pl.smyk.cinemaservice.dto.UpdateCinemaRequest;
import pl.smyk.cinemaservice.service.CinemaService;

import java.util.List;

@RestController
@RequestMapping("/api/cinema")
@RequiredArgsConstructor
public class CinemaController {

    private final CinemaService cinemaService;

    @GetMapping
    public ResponseEntity<List<CinemaDto>> getAllCinemas() {
        return ResponseEntity.ok(cinemaService.getAllCinemas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CinemaDto> getCinemaById(@PathVariable Long id) {
        return ResponseEntity.ok(cinemaService.getCinemaById(id));
    }

    @PostMapping
    public ResponseEntity<CinemaDto> createCinema(@Valid @RequestBody CreateCinemaRequest request) {
        return new ResponseEntity<>(cinemaService.createCinema(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CinemaDto> updateCinema(@PathVariable Long id, @Valid @RequestBody UpdateCinemaRequest request) {
        // Ensure the ID in the path matches the ID in the request body
        if (!id.equals(request.getId())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(cinemaService.updateCinema(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCinema(@PathVariable Long id) {
        cinemaService.deleteCinema(id);
        return ResponseEntity.noContent().build();
    }
}