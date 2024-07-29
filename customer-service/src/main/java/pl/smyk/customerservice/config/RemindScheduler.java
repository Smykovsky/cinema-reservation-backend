package pl.smyk.customerservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.smyk.customerservice.model.Reservation;
import pl.smyk.customerservice.repository.ReservationRepository;
import pl.smyk.customerservice.service.MailService;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class RemindScheduler {
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private MailService mailService;

    @Scheduled(cron = "0 0 9 * * ?")
    public void sendReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDayTomorrow = now.plusDays(1).toLocalDate().atStartOfDay();
        LocalDateTime endOfDayTomorrow = startOfDayTomorrow.plusDays(1).minusSeconds(1);

        List<Reservation> reservationsBetweenDates = reservationRepository.findBySelectedPlayTimeBetween(startOfDayTomorrow, endOfDayTomorrow);

        for (Reservation reservation : reservationsBetweenDates) {
            System.out.println("Reminder for: " + reservation.getCustomerEmail() + "for reservation: " + reservation.getId());
//            mailService.sendEmail(reservation.getCustomerEmail(), reservation);
        }
    }
}
