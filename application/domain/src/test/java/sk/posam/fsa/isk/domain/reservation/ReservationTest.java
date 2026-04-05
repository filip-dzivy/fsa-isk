package sk.posam.fsa.isk.domain.reservation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.catalog.BookGenre;
import sk.posam.fsa.isk.domain.catalog.ISBN;
import sk.posam.fsa.isk.domain.member.Email;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.domain.member.MemberRole;
import sk.posam.fsa.isk.domain.shared.DomainException;

import java.time.LocalDate;
import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

public class ReservationTest {
    private Member member;
    private Book book;
    private ISBN isbn;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        isbn = new ISBN("9780306406157");
        member = new Member(1L, new Email("jan.novak@example.sk"), "Jan", "Novak", MemberRole.MEMBER);
        book = new Book(isbn, "Clean Code", "Robert C. Martin", BookGenre.TECHNOLOGY, "Prentice Hall", Year.of(2008),3);
        reservation = new Reservation(member, book);
    }

    @Test
    void newReservationIsPending() {
        assertEquals(ReservationStatus.PENDING, reservation.getStatus());
        assertTrue(reservation.isActive());
        assertEquals(0, reservation.getPositionInQueue());
    }

    @Test
    void activateSetsReadyForPickupWithExpiryDate() {
        reservation.activate();
        assertEquals(ReservationStatus.READY_FOR_PICKUP, reservation.getStatus());
    }

    @Test
    void activatingNonPendingReservationThrows(){
        reservation.cancel();
        assertThrows(DomainException.class, reservation::activate);
    }

    @Test
    void cancelPendingReservation() {
        reservation.cancel();
        assertEquals(ReservationStatus.CANCELLED, reservation.getStatus());
        assertFalse(reservation.isActive());
    }

    @Test
    void cancelReadyForPickupReservation() {
        reservation.activate();
        reservation.cancel();
        assertEquals(ReservationStatus.CANCELLED, reservation.getStatus());
    }

    @Test
    void cancellingFulfilledReservationThrows() {
        reservation.activate();
        reservation.fulfill();
        assertThrows(DomainException.class, reservation::cancel);
    }

    @Test
    void expireSetsExpiredStatus() {
        reservation.activate();
        reservation.expire();
        assertEquals(ReservationStatus.EXPIRED, reservation.getStatus());
        assertFalse(reservation.isActive());
    }

    @Test
    void expiringCancelledReservationThrows() {
        reservation.cancel();
        assertThrows(DomainException.class, reservation::expire);
    }

    @Test
    void fulfillChangesStatusToFulfilled() {
        reservation.activate();
        reservation.fulfill();
        assertEquals(ReservationStatus.FULFILLED, reservation.getStatus());
    }

    @Test
    void fulfillFromPendingThrows() {
        assertThrows(DomainException.class, reservation::fulfill);
    }


    @Test
    void isExpiredByDateWhenPickupWindowPassed() {
        // A freshly created PENDING reservation is never expired by date
        Reservation pending = new Reservation(member, book);
        assertFalse(pending.isExpiredByDate());

        // A READY_FOR_PICKUP reservation activated just now still has 3 days left
        Reservation ready = new Reservation(member, book);
        ready.activate();
        assertFalse(ready.isExpiredByDate());
        assertEquals(LocalDate.now().plusDays(3), ready.getCreatedOn().plusDays(3));
    }

    @Test
    void setPositionInQueueUpdatesPosition() {
        Reservation r = new Reservation(member, book);
        r.setPositionInQueue(3);
        assertEquals(3, r.getPositionInQueue());
    }

    @Test
    void negativePositionThrows() {
        Reservation r = new Reservation(member, book);
        assertThrows(DomainException.class, () -> r.setPositionInQueue(-1));
    }

}
