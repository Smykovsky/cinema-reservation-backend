package pl.smyk.customerservice.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import pl.smyk.customerservice.model.Reservation;
import pl.smyk.customerservice.service.MailService;

@Component
public class ReservationChangeStatusListener {
    @Autowired
    private MailService mailService;

    @EventListener
    public void handleReservationChangeStatusEvent(ReservationChangeStatusEvent event) {
        Reservation reservation = event.getReservation();
        mailService.sendEmailWithReservationTicket(reservation.getCustomerEmail(), reservation);
        System.out.println("Reservation has been paid! Ticket will be send on email address!");
    }
}
