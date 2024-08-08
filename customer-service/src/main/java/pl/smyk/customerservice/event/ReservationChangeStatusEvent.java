package pl.smyk.customerservice.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import pl.smyk.customerservice.model.Reservation;

@Getter
public class ReservationChangeStatusEvent extends ApplicationEvent {
    private final Reservation reservation;

    public ReservationChangeStatusEvent(Reservation reservation) {
        super(reservation);
        this.reservation = reservation;
    }
}
