package pl.smyk.cinemaservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.smyk.cinemaservice.dto.CreateHallRequest;
import pl.smyk.cinemaservice.dto.HallDto;
import pl.smyk.cinemaservice.dto.UpdateHallRequest;
import pl.smyk.cinemaservice.service.HallService;

import java.util.List;

@RestController
@RequestMapping("/api/hall")
@RequiredArgsConstructor
public class HallController {

    private final HallService hallService;

    @GetMapping
    public ResponseEntity<List<HallDto>> getAllHalls() {
        return ResponseEntity.ok(hallService.getAllHalls());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HallDto> getHallById(@PathVariable Long id) {
        return ResponseEntity.ok(hallService.getHallById(id));
    }

    @PostMapping
    public ResponseEntity<HallDto> createHall(@Valid @RequestBody CreateHallRequest request) {
        return new ResponseEntity<>(hallService.createHall(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HallDto> updateHall(@PathVariable Long id, @Valid @RequestBody UpdateHallRequest request) {
        if (!id.equals(request.getId())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(hallService.updateHall(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHall(@PathVariable Long id) {
        hallService.deleteHall(id);
        return ResponseEntity.noContent().build();
    }
}
