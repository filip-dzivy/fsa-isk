package sk.isk.domain.reservation.predicate;

import sk.isk.domain.reservation.Reservation;
import sk.isk.domain.reservation.ReservationStatus;

import java.util.function.Predicate;

public class IsCancellableReservationPredicate implements Predicate<Reservation> {

    public static final IsCancellableReservationPredicate INSTANCE =
            new IsCancellableReservationPredicate();

    private IsCancellableReservationPredicate() {}

    @Override
    public boolean test(Reservation reservation) {
        if (reservation == null) return false;
        ReservationStatus s = reservation.getStatus();
        return s == ReservationStatus.PENDING
                || s == ReservationStatus.READY_FOR_PICKUP;
    }
}
