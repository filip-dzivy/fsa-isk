package sk.posam.fsa.isk.domain.reservation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.catalog.BookGenre;
import sk.posam.fsa.isk.domain.catalog.ISBN;
import sk.posam.fsa.isk.domain.membership.*;
import sk.posam.fsa.isk.domain.membership.*;
import sk.posam.fsa.isk.domain.reservation.service.ReservationService;
import sk.posam.fsa.isk.domain.shared.DomainException;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private NotificationPort notificationPort;
    @InjectMocks
    private ReservationService service;

    private Member member;
    private Book book;
    private ISBN isbn;

    @BeforeEach
    void setUp() {
        isbn = new ISBN("9780306406157");
        book = new Book(isbn, "Clean Code", "Robert C. Martin",
                BookGenre.TECHNOLOGY, "Prentice Hall", Year.of(2008), 3);
        member = new Member(1L, new Email("jan.novak@example.sk"), "Jan", "Novak", MemberRole.MEMBER);
        member.assignMembership(Membership.createNew());
    }

    // -------------------------------------------------------------------------
    // UC05 — Vytvorenie rezervácie
    // -------------------------------------------------------------------------

    @Test
    void create_happyPath_reservationIsPendingAtPosition1() {
        service.create(member, book);

        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    void create_secondReservationGetsPosition2() {
        Member member2 = new Member(2L, new Email("eva@example.sk"), "Eva", "Kovacova", MemberRole.MEMBER);
        member2.assignMembership(Membership.createNew());

        Reservation existing = new Reservation(1L, member, book, LocalDate.now(), ReservationStatus.PENDING, 1);
        when(reservationRepository.findActiveByBook(book)).thenReturn(List.of(existing));

        service.create(member2, book);

        verify(reservationRepository).save(argThat(r -> r.getPositionInQueue() == 2));
    }

    @Test
    void create_expiredMembership_throws() {
        member.assignMembership(new Membership(LocalDate.now().minusDays(1)));

        assertThrows(DomainException.class, () -> service.create(member, book));

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void create_unpaidFine_throws() {
        member.addFine(new Fine(Money.of(0.50, "EUR"), "Oneskorené vrátenie o 5 dní"));

        assertThrows(DomainException.class, () -> service.create(member, book));

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void create_duplicateReservationForSameMemberAndBook_throws() {
        Reservation existing = new Reservation(1L, member, book, LocalDate.now(), ReservationStatus.PENDING, 1);
        when(reservationRepository.findActiveByBook(book)).thenReturn(List.of(existing));

        assertThrows(DomainException.class, () -> service.create(member, book));

        verify(reservationRepository, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // UC07 — Zrušenie rezervácie
    // -------------------------------------------------------------------------

    @Test
    void cancel_happyPath_rebuildsQueue() {
        Member m2 = new Member(2L, new Email("eva@example.sk"), "Eva", "K", MemberRole.MEMBER);
        Member m3 = new Member(3L, new Email("peter@example.sk"), "Peter", "H", MemberRole.MEMBER);

        Reservation r1 = new Reservation(1L, member, book, LocalDate.now(), ReservationStatus.PENDING, 1);
        Reservation r2 = new Reservation(2L, m2, book, LocalDate.now(), ReservationStatus.PENDING, 2);
        Reservation r3 = new Reservation(3L, m3, book, LocalDate.now(), ReservationStatus.PENDING, 3);

        when(reservationRepository.findActiveByBook(book)).thenReturn(new ArrayList<>(List.of(r2, r3)));

        service.cancel(r1);

        assertEquals(ReservationStatus.CANCELLED, r1.getStatus());
        assertEquals(1, r2.getPositionInQueue());
        assertEquals(2, r3.getPositionInQueue());
        verify(reservationRepository, times(1)).save(r1);
        verify(reservationRepository, times(1)).save(r2);
        verify(reservationRepository, times(1)).save(r3);
    }

    @Test
    void cancel_alreadyCancelledReservation_throws() {
        Reservation r = new Reservation(1L, member, book, LocalDate.now(), ReservationStatus.CANCELLED, 1);

        assertThrows(DomainException.class, () -> service.cancel(r));

        verify(reservationRepository, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // RQ14 — Notifikácia pri vrátení
    // -------------------------------------------------------------------------

    @Test
    void notifyNextInQueue_activatesFirstPendingAndSendsNotification() {
        Reservation r1 = new Reservation(1L, member, book, LocalDate.now(), ReservationStatus.PENDING, 1);
        when(reservationRepository.findActiveByBook(book)).thenReturn(new ArrayList<>(List.of(r1)));

        service.notifyNextInQueue(book);

        assertEquals(ReservationStatus.READY_FOR_PICKUP, r1.getStatus());
        verify(notificationPort).notifyReservationReady(member, book);
        verify(reservationRepository).save(r1);
    }

    @Test
    void notifyNextInQueue_noReservations_noNotificationSent() {
        service.notifyNextInQueue(book);

        verify(notificationPort, never()).notifyReservationReady(any(), any());
    }

    @Test
    void notifyNextInQueue_skipsReadyForPickupAndNotifiesNextPending() {
        Member m2 = new Member(2L, new Email("eva@example.sk"), "Eva", "K", MemberRole.MEMBER);
        Reservation r1 = new Reservation(1L, member, book, LocalDate.now(), ReservationStatus.READY_FOR_PICKUP, 1);
        Reservation r2 = new Reservation(2L, m2, book, LocalDate.now(), ReservationStatus.PENDING, 2);
        when(reservationRepository.findActiveByBook(book)).thenReturn(List.of(r1, r2));

        service.notifyNextInQueue(book);

        assertEquals(ReservationStatus.READY_FOR_PICKUP, r2.getStatus());
        verify(notificationPort).notifyReservationReady(m2, book);
    }

    // -------------------------------------------------------------------------
    // RQ15 — Automatická expirácia po 3 dňoch
    // -------------------------------------------------------------------------

    @Test
    void expireReadyReservations_noExpiredReservations_nothingHappens() {
        Reservation r = new Reservation(1L, member, book, LocalDate.now(), ReservationStatus.READY_FOR_PICKUP, 1);
        when(reservationRepository.findAll()).thenReturn(List.of(r));

        service.expireReadyReservations();

        assertEquals(ReservationStatus.READY_FOR_PICKUP, r.getStatus());
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void expireReadyReservations_expiredReservation_getsExpired() {
        Reservation r = new Reservation(1L, member, book,
                LocalDate.now().minusDays(4), ReservationStatus.READY_FOR_PICKUP, 1);
        when(reservationRepository.findAll()).thenReturn(List.of(r));

        service.expireReadyReservations();

        assertEquals(ReservationStatus.EXPIRED, r.getStatus());
        verify(reservationRepository).save(r);
    }

    // -------------------------------------------------------------------------
    // UC06 — Zoznam rezervácií čitateľa
    // -------------------------------------------------------------------------

    @Test
    void findByMember_returnsOnlyMembersReservations() {
        Reservation r = new Reservation(1L, member, book, LocalDate.now(), ReservationStatus.PENDING, 1);
        when(reservationRepository.findByMember(member)).thenReturn(List.of(r));

        List<Reservation> result = service.findByMember(member);

        assertEquals(1, result.size());
        assertEquals(member, result.get(0).getCreatedBy());
    }

    // -------------------------------------------------------------------------
    // hasPendingReservation
    // -------------------------------------------------------------------------

    @Test
    void hasPendingReservation_trueWhenPendingExists() {
        Reservation r = new Reservation(1L, member, book, LocalDate.now(), ReservationStatus.PENDING, 1);
        when(reservationRepository.findActiveByBook(book)).thenReturn(List.of(r));

        assertTrue(service.hasPendingReservation(book));
    }

    @Test
    void hasPendingReservation_trueWhenReadyForPickupExists() {
        Reservation r = new Reservation(1L, member, book, LocalDate.now(), ReservationStatus.READY_FOR_PICKUP, 1);
        when(reservationRepository.findActiveByBook(book)).thenReturn(List.of(r));

        assertTrue(service.hasPendingReservation(book));
    }

    @Test
    void hasPendingReservation_falseWhenNoneExist() {
        when(reservationRepository.findActiveByBook(book)).thenReturn(List.of());

        assertFalse(service.hasPendingReservation(book));
    }

    @Test
    void hasPendingReservation_falseAfterCancellation() {
        Reservation r = new Reservation(1L, member, book, LocalDate.now(), ReservationStatus.CANCELLED, 1);
        when(reservationRepository.findActiveByBook(book)).thenReturn(List.of(r));

        assertFalse(service.hasPendingReservation(book));
    }
}
