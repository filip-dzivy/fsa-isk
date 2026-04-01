package sk.isk.domain.reservation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.isk.domain.catalog.Book;
import sk.isk.domain.catalog.BookGenre;
import sk.isk.domain.catalog.ISBN;
import sk.isk.domain.membership.Email;
import sk.isk.domain.membership.Member;
import sk.isk.domain.membership.MemberRole;
import sk.isk.domain.shared.DomainException;

import javax.swing.undo.UndoManager;
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


    //TODO : Domysliet logiku za expire
    @Test
    void dasdasdasdas() {
        assertFalse(true);
    }

}
