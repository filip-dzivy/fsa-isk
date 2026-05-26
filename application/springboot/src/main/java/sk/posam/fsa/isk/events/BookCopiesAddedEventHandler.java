package sk.posam.fsa.isk.events;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import sk.posam.fsa.isk.domain.catalog.event.BookCopiesAddedEvent;
import sk.posam.fsa.isk.domain.reservation.service.ReservationFacade;

@Component
public class BookCopiesAddedEventHandler {

    private final ReservationFacade reservationFacade;

    public BookCopiesAddedEventHandler(ReservationFacade reservationFacade) {
        this.reservationFacade = reservationFacade;
    }

    @EventListener
    public void handle(BookCopiesAddedEvent event) {
        reservationFacade.notifyNextInQueue(event.getBook());
    }
}
