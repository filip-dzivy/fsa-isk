package sk.posam.fsa.isk.events;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import sk.posam.fsa.isk.domain.lending.event.LoanCreatedEvent;
import sk.posam.fsa.isk.domain.reservation.service.ReservationFacade;

@Component
public class LoanCreatedEventHandler {

    private final ReservationFacade reservationFacade;

    public LoanCreatedEventHandler(ReservationFacade reservationFacade) {
        this.reservationFacade = reservationFacade;
    }

    @EventListener
    public void handle(LoanCreatedEvent event) {
        reservationFacade.fulfillReservation(event.getLoanedTo(), event.getBook());
    }
}
