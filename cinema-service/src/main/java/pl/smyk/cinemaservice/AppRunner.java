package pl.smyk.cinemaservice;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.smyk.cinemaservice.model.Hall;
import pl.smyk.cinemaservice.model.Seat;
import pl.smyk.cinemaservice.model.SeatType;
import pl.smyk.cinemaservice.repository.HallRepository;
import pl.smyk.cinemaservice.repository.SeatRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppRunner implements CommandLineRunner {
    private final HallRepository hallRepository;
    private final SeatRepository seatRepository;

    @Override
    @Transactional
    public void run(String... args) {

        List<Hall> halls = hallRepository.findAll();

        log.info("Initializing seats for {} halls", halls.size());

        for (Hall hall : halls) {

            // Pobieramy istniejące miejsca danej hali jednym zapytaniem
            List<Seat> existingSeats = seatRepository.findAllByHallId(hall.getId());

            Set<String> existingMap = existingSeats.stream()
                    .map(seat -> seat.getRow() + "_" + seat.getNumber())
                    .collect(Collectors.toSet());

            List<Seat> seatsToCreate = new ArrayList<>();

            for (int row = 1; row <= 10; row++) {
                for (int number = 1; number <= 10; number++) {

                    String key = row + "_" + number;

                    if (!existingMap.contains(key)) {

                        Seat seat = new Seat();
                        seat.setHall(hall);
                        seat.setRow(row);
                        seat.setNumber(number);
                        seat.setSeatType(SeatType.STANDARD);

                        seatsToCreate.add(seat);
                    }
                }
            }

            if (!seatsToCreate.isEmpty()) {
                seatRepository.saveAll(seatsToCreate);
                log.info("Hall {} → created {} seats", hall.getId(), seatsToCreate.size());
            } else {
                log.info("Hall {} → already fully initialized", hall.getId());
            }
        }

        log.info("Seat initialization completed.");
    }
}