package pl.smyk.cinemaservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.smyk.cinemaservice.dto.CreateSeatRequest;
import pl.smyk.cinemaservice.dto.CreateSeatsRequest;
import pl.smyk.cinemaservice.dto.SeatDto;
import pl.smyk.cinemaservice.dto.UpdateSeatRequest;
import pl.smyk.cinemaservice.service.SeatService;

import java.util.List;

@RestController
@RequestMapping("/api/seat")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @GetMapping
    public ResponseEntity<List<SeatDto>> getAllSeats() {
        return ResponseEntity.ok(seatService.getAllSeats());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeatDto> getSeatById(@PathVariable Long id) {
        return ResponseEntity.ok(seatService.getSeatById(id));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<List<SeatDto>> createSeats(
            @Valid @RequestBody CreateSeatsRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(seatService.createSeats(request.getSeats()));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<SeatDto> updateSeat(@PathVariable Long id, @Valid @RequestBody UpdateSeatRequest request) {
        if (!id.equals(request.getId())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(seatService.updateSeat(request));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeat(@PathVariable Long id) {
        seatService.deleteSeat(id);
        return ResponseEntity.noContent().build();
    }
}
