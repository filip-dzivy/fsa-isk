package sk.posam.fsa.isk.domain.reservation.predicate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.catalog.BookGenre;
import sk.posam.fsa.isk.domain.catalog.ISBN;
import sk.posam.fsa.isk.domain.member.Email;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.domain.member.MemberRole;
import sk.posam.fsa.isk.domain.reservation.Reservation;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

class ReservationPredicateTest {

    private Member member;
    private Book book;
    private Reservation pending;

    @BeforeEach
    void setUp() {
        member = new Member(1L, new Email("jan@example.sk"), "Jan", "Novak", MemberRole.MEMBER);
        book = new Book(new ISBN("9780306406157"), "Clean Code", "Robert C. Martin",
                BookGenre.TECHNOLOGY, "Prentice Hall", Year.of(2008), 3);
        pending = new Reservation(member, book);
    }

    // IsPendingReservationPredicate
    @Test void isPending_trueForPending() {
        assertTrue(IsPendingReservationPredicate.INSTANCE.test(pending));
    }
    @Test void isPending_falseAfterActivation() {
        pending.activate();
        assertFalse(IsPendingReservationPredicate.INSTANCE.test(pending));
    }
    @Test void isPending_falseForNull() {
        assertFalse(IsPendingReservationPredicate.INSTANCE.test(null));
    }

    // IsReadyForPickupPredicate
    @Test void isReadyForPickup_trueAfterActivation() {
        pending.activate();
        assertTrue(IsReadyForPickupPredicate.INSTANCE.test(pending));
    }
    @Test void isReadyForPickup_falseForPending() {
        assertFalse(IsReadyForPickupPredicate.INSTANCE.test(pending));
    }
    @Test void isReadyForPickup_falseForNull() {
        assertFalse(IsReadyForPickupPredicate.INSTANCE.test(null));
    }

    // IsCancellableReservationPredicate
    @Test void isCancellable_trueForPending() {
        assertTrue(IsCancellableReservationPredicate.INSTANCE.test(pending));
    }
    @Test void isCancellable_trueForReadyForPickup() {
        pending.activate();
        assertTrue(IsCancellableReservationPredicate.INSTANCE.test(pending));
    }
    @Test void isCancellable_falseForCancelled() {
        pending.cancel(member);
        assertFalse(IsCancellableReservationPredicate.INSTANCE.test(pending));
    }
    @Test void isCancellable_falseForFulfilled() {
        pending.activate();
        pending.fulfill();
        assertFalse(IsCancellableReservationPredicate.INSTANCE.test(pending));
    }
    @Test void isCancellable_falseForExpired() {
        pending.activate();
        pending.expire();
        assertFalse(IsCancellableReservationPredicate.INSTANCE.test(pending));
    }
    @Test void isCancellable_falseForNull() {
        assertFalse(IsCancellableReservationPredicate.INSTANCE.test(null));
    }
}
