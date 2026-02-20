package pl.smyk.cinemaservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.smyk.cinemaservice.dto.*;
import pl.smyk.cinemaservice.service.ScreeningService;

import java.time.LocalDate;
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

    @GetMapping("/by-cinema-and-date")
    public ResponseEntity<List<ScreeningDto>> getScreeningsByCinemaAndDate(
            @RequestParam Long cinemaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return ResponseEntity.ok(screeningService.getScreeningsByCinemaAndDate(cinemaId, date));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<List<ScreeningDto>> createScreenings(@Valid @RequestBody CreateScreeningsRequest request) {
        return new ResponseEntity<>(screeningService.createScreenings(request), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ScreeningDto> updateScreening(@PathVariable Long id, @Valid @RequestBody UpdateScreeningRequest request) {
        if (!id.equals(request.getId())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(screeningService.updateScreening(request));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScreening(@PathVariable Long id) {
        screeningService.deleteScreening(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/seats/reserve")
    public ResponseEntity<Void> reserveSeats(@PathVariable("id") Long id, @Valid @RequestBody ReserveSeatsRequest request) {
        screeningService.reserveSeats(id, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/seats/available")
    public ResponseEntity<List<ScreeningSeatDto>> getAvailableSeatsForScreening(@PathVariable Long id) {
        return ResponseEntity.ok(screeningService.getAvailableSeatsForScreening(id));
    }

    @GetMapping("/{id}/seats")
    public ResponseEntity<List<ScreeningSeatDto>> getAllSeatsForScreening(@PathVariable Long id) {
        return ResponseEntity.ok(screeningService.getAllSeatsForScreening(id));
    }
}
