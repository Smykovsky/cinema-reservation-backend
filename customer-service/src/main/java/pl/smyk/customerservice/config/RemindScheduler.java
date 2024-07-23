package pl.smyk.customerservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.smyk.customerservice.model.Reservation;
import pl.smyk.customerservice.repository.ReservationRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class RemindScheduler {
    @Autowired
    private ReservationRepository reservationRepository;

    @Scheduled(cron = "0 0 9 * * ?")
    public void sendReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusDays(1);

        List<Reservation> reservations = reservationRepository.findByMoviePlayDateTimeBetween(tomorrow.toLocalDate().atStartOfDay(), tomorrow.toLocalDate().atTime(23, 59, 59));

        for (Reservation reservation : reservations) {
            System.out.println("Reminder for: " + reservation.getCustomerEmail() + "for reservation: " + reservation.getId());
            //email reminder
        }
    }
}
