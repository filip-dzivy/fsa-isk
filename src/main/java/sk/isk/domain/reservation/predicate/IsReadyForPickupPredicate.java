package sk.isk.domain.reservation.predicate;

import sk.isk.domain.reservation.Reservation;
import sk.isk.domain.reservation.ReservationStatus;

import java.util.function.Predicate;

public class IsReadyForPickupPredicate implements Predicate<Reservation> {

    public static final IsReadyForPickupPredicate INSTANCE = new IsReadyForPickupPredicate();

    private IsReadyForPickupPredicate() {}

    @Override
    public boolean test(Reservation reservation) {
        return reservation != null
                && reservation.getStatus() == ReservationStatus.READY_FOR_PICKUP;
    }
}

