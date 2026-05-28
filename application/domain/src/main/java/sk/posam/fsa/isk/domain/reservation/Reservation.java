package sk.posam.fsa.isk.domain.reservation;

import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.domain.reservation.predicate.IsCancellableReservationPredicate;
import sk.posam.fsa.isk.domain.reservation.predicate.IsReadyForPickupPredicate;
import sk.posam.fsa.isk.domain.shared.DomainException;

import java.time.LocalDate;
import java.util.Objects;

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
    Reservation(long id, Member member, Book book, LocalDate createdOn, ReservationStatus status, int positionInQueue){
        this.id = id;
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
        this.status = ReservationStatus.READY_FOR_PICKUP;
    }

    public void cancel(Member RequestedBy) {
        require(IsCancellableReservationPredicate.INSTANCE.test(this),
                DomainException.Type.CONFLICT,
                "Rezerváciu v stave " + status + " nemožno zrušiť.");
        require(createdBy.equals(RequestedBy),
                DomainException.Type.FORBIDDEN,
                "Member môže zrušiť iba svoju rezerváciu");
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

    public void changeCreatedOn(LocalDate newCreatedOn) {
        require(newCreatedOn != null,
                DomainException.Type.VALIDATION,
                "Dátum vytvorenia nesmie byť null.");
        require(!newCreatedOn.isAfter(LocalDate.now()),
                DomainException.Type.VALIDATION,
                "Dátum vytvorenia nemôže byť v budúcnosti.");
        require(status != ReservationStatus.CANCELLED
                        && status != ReservationStatus.FULFILLED,
                DomainException.Type.CONFLICT,
                "Rezerváciu v stave " + status + " nemožno upraviť.");
        this.createdOn = newCreatedOn;
    }

    public boolean isExpiredByDate() {
        return IsReadyForPickupPredicate.INSTANCE.test(this)
                && LocalDate.now().isAfter(createdOn.plusDays(PICKUP_WINDOW_DAYS));
    }

    public boolean isActive() {
        return status == ReservationStatus.PENDING
                || status == ReservationStatus.READY_FOR_PICKUP;
    }

    public LocalDate getCreatedOn() {return createdOn;}
    public long getId() {return id;}
    public Book getBook() {return book;}
    public ReservationStatus getStatus() {return status;}
    public int getPositionInQueue() {return positionInQueue;}
    public Member getCreatedBy() {return createdBy;}

    public void setCreatedBy(Member createdBy) { this.createdBy = createdBy; }
    public void setBook(Book book) { this.book = book; }

    public void setPositionInQueue(int position) {
        require(position >= 0,
                DomainException.Type.VALIDATION,
                "Pozícia vo fronte nesmie byť záporná.");
        this.positionInQueue = position;
    }

    private void require(boolean valid, DomainException.Type type, String message) {
        if (!valid) throw new DomainException(type, message);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reservation)) return false;
        return Objects.equals(id, ((Reservation) o).id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Reservation{id=" + id + ", created by=" + createdBy.getId()
                + ", isbn=" + book.getIsbn() + ", status=" + status
                + ", queue=" + positionInQueue + "}";
    }

}
