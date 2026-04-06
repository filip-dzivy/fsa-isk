package sk.posam.fsa.isk.events;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import sk.posam.fsa.isk.domain.lending.event.BookReturnedEvent;
import sk.posam.fsa.isk.domain.reservation.service.ReservationFacade;

@Component
public class BookReturnedEventHandler {

    private final ReservationFacade reservationFacade;

    public BookReturnedEventHandler(ReservationFacade reservationFacade) {
        this.reservationFacade = reservationFacade;
    }

    @EventListener
    public void handle(BookReturnedEvent event) {
        reservationFacade.notifyNextInQueue(event.getBook());
    }
}
