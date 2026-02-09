package pl.smyk.cinemaservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.smyk.cinemaservice.dto.CreateScreeningRequest;
import pl.smyk.cinemaservice.dto.ScreeningDto;
import pl.smyk.cinemaservice.dto.ScreeningSeatDto;
import pl.smyk.cinemaservice.dto.UpdateScreeningRequest;
import pl.smyk.cinemaservice.service.ScreeningService;

import java.util.List;

@RestController
@RequestMapping("/api/screening")
@RequiredArgsConstructor
public class ScreeningController {

    private final ScreeningService screeningService;

    @GetMapping
    public ResponseEntity<List<ScreeningDto>> getAllScreenings() {
        return ResponseEntity.ok(screeningService.getAllScreenings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScreeningDto> getScreeningById(@PathVariable Long id) {
        return ResponseEntity.ok(screeningService.getScreeningById(id));
    }

    @PostMapping
    public ResponseEntity<ScreeningDto> createScreening(@Valid @RequestBody CreateScreeningRequest request) {
        return new ResponseEntity<>(screeningService.createScreening(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScreeningDto> updateScreening(@PathVariable Long id, @Valid @RequestBody UpdateScreeningRequest request) {
        if (!id.equals(request.getId())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(screeningService.updateScreening(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScreening(@PathVariable Long id) {
        screeningService.deleteScreening(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/seats/available")
    public ResponseEntity<List<ScreeningSeatDto>> getAvailableSeatsForScreening(@PathVariable Long id) {
        return ResponseEntity.ok(screeningService.getAvailableSeatsForScreening(id));
    }
}
