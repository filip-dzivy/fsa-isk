package sk.isk.domain.reservation;

import sk.isk.domain.catalog.Book;
import sk.isk.domain.membership.Member;
import sk.isk.domain.reservation.predicate.IsCancellableReservationPredicate;
import sk.isk.domain.reservation.predicate.IsReadyForPickupPredicate;
import sk.isk.domain.shared.DomainException;

import java.time.LocalDate;

public class Reservation {
    static final int PICKUP_WINDOW_DAYS = 3;

    private long id;
    private Member createdBy;
    private Book book;
    private LocalDate createdOn;
    private ReservationStatus status;
    private int positionInQueue;

    public Reservation() {}

    public Reservation(Member member, Book book){
        this.createdBy = member;
        this.book = book;
        this.createdOn = LocalDate.now();
        this.status = ReservationStatus.PENDING;
        validateForCreation();
    }

    //pre testovanie
    Reservation(Member member, Book book, LocalDate createdOn, ReservationStatus status, int positionInQueue){
        this.createdBy = member;
        this.book = book;
        this.createdOn = createdOn;
        this.status = status;
        this.positionInQueue = positionInQueue;
    }

    public void validateForCreation() {
        require(createdBy != null,
                DomainException.Type.VALIDATION,
                "CreatedBy je povinný údaj.");
        require(book != null,
                DomainException.Type.VALIDATION,
                "Book je povinný údaj.");
    }

    public void activate() {
        require(status == ReservationStatus.PENDING,
                DomainException.Type.CONFLICT,
                "Aktivovať možno len čakajúcu rezerváciu.");
        this.status     = ReservationStatus.READY_FOR_PICKUP;
    }

    public void cancel() {
        require(IsCancellableReservationPredicate.INSTANCE.test(this),
                DomainException.Type.CONFLICT,
                "Rezerváciu v stave " + status + " nemožno zrušiť.");
        this.status = ReservationStatus.CANCELLED;
    }

    public void expire() {
        require(status != ReservationStatus.CANCELLED
                        && status != ReservationStatus.FULFILLED,
                DomainException.Type.CONFLICT,
                "Rezerváciu v stave " + status + " nemožno expirovať.");
        this.status = ReservationStatus.EXPIRED;
    }

    public void fulfill() {
        require(IsReadyForPickupPredicate.INSTANCE.test(this),
                DomainException.Type.CONFLICT,
                "Splniť možno len rezerváciu v stave READY_FOR_PICKUP.");
        this.status = ReservationStatus.FULFILLED;
    }

    public boolean isActive() {
        return status == ReservationStatus.PENDING
                || status == ReservationStatus.READY_FOR_PICKUP;
    }

    public ReservationStatus getStatus() {return status;}
    public int getPositionInQueue() {return positionInQueue;}

    private void require(boolean valid, DomainException.Type type, String message) {
        if (!valid) throw new DomainException(type, message);
    }
}
