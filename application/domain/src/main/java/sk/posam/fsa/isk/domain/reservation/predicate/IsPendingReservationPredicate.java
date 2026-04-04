package sk.posam.fsa.isk.domain.reservation.predicate;

import sk.posam.fsa.isk.domain.reservation.Reservation;
import sk.posam.fsa.isk.domain.reservation.ReservationStatus;

import java.util.function.Predicate;

public class IsPendingReservationPredicate implements Predicate<Reservation> {

    public static final IsPendingReservationPredicate INSTANCE = new IsPendingReservationPredicate();

    private IsPendingReservationPredicate() {}

    @Override
    public boolean test(Reservation reservation) {
        return reservation != null
                && reservation.getStatus() == ReservationStatus.PENDING;
    }
}
