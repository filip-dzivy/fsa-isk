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
import sk.posam.fsa.isk.domain.finance.Fine;
import sk.posam.fsa.isk.domain.finance.Money;
import sk.posam.fsa.isk.domain.member.*;
import sk.posam.fsa.isk.domain.member.access.MemberVisibilityResolver;
import sk.posam.fsa.isk.domain.reservation.service.ReservationService;
import sk.posam.fsa.isk.domain.shared.DomainException;
import sk.posam.fsa.isk.domain.shared.NotificationPort;

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
    private MemberVisibilityResolver memberVisibilityResolver;
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

        service.cancel(r1, member);

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

        assertThrows(DomainException.class, () -> service.cancel(r, member));

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

    // -------------------------------------------------------------------------
    // fulfillReservation
    // -------------------------------------------------------------------------

    @Test
    void fulfillReservation_marksReadyForPickupAsFulfilled() {
        Reservation r = new Reservation(1L, member, book, LocalDate.now(),
                ReservationStatus.READY_FOR_PICKUP, 1);
        when(reservationRepository.findActiveByBook(book)).thenReturn(List.of(r));

        service.fulfillReservation(member, book);

        assertEquals(ReservationStatus.FULFILLED, r.getStatus());
        verify(reservationRepository).save(r);
    }

    @Test
    void fulfillReservation_pendingReservation_doesNothing() {
        Reservation r = new Reservation(1L, member, book, LocalDate.now(),
                ReservationStatus.PENDING, 1);
        when(reservationRepository.findActiveByBook(book)).thenReturn(List.of(r));

        service.fulfillReservation(member, book);

        assertEquals(ReservationStatus.PENDING, r.getStatus());
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void fulfillReservation_noMatchingMember_doesNothing() {
        Member other = new Member(99L, new Email("o@o.sk"), "O", "O", MemberRole.MEMBER);
        Reservation r = new Reservation(1L, other, book, LocalDate.now(),
                ReservationStatus.READY_FOR_PICKUP, 1);
        when(reservationRepository.findActiveByBook(book)).thenReturn(List.of(r));

        service.fulfillReservation(member, book);

        assertEquals(ReservationStatus.READY_FOR_PICKUP, r.getStatus());
        verify(reservationRepository, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // notifyNextInQueue with multiple free slots
    // -------------------------------------------------------------------------

    @Test
    void notifyNextInQueue_freeSlotsGreaterThanOne_activatesMultiplePending() {
        // Book has 3 available copies, 0 ready → 3 free slots
        Member m2 = new Member(2L, new Email("eva@example.sk"), "Eva", "K", MemberRole.MEMBER);
        Reservation r1 = new Reservation(1L, member, book, LocalDate.now(), ReservationStatus.PENDING, 1);
        Reservation r2 = new Reservation(2L, m2, book, LocalDate.now(), ReservationStatus.PENDING, 2);
        when(reservationRepository.findActiveByBook(book)).thenReturn(List.of(r1, r2));

        service.notifyNextInQueue(book);

        assertEquals(ReservationStatus.READY_FOR_PICKUP, r1.getStatus());
        assertEquals(ReservationStatus.READY_FOR_PICKUP, r2.getStatus());
        verify(notificationPort).notifyReservationReady(member, book);
        verify(notificationPort).notifyReservationReady(m2, book);
    }

    // -------------------------------------------------------------------------
    // create(requestingMember, targetMemberId, book) — privileged vs not
    // -------------------------------------------------------------------------

    @Test
    void create_byPrivilegedForOtherMember_usesTargetMember() {
        Member librarian = new Member(99L, new Email("lib@example.sk"), "L", "L", MemberRole.LIBRARIAN);
        when(memberRepository.findWithFines(member.getId())).thenReturn(java.util.Optional.of(member));

        service.create(librarian, member.getId(), book);

        verify(reservationRepository).save(argThat(r -> r.getCreatedBy().equals(member)));
    }

    @Test
    void create_byPrivilegedForUnknownMember_throwsNotFound() {
        Member librarian = new Member(99L, new Email("lib@example.sk"), "L", "L", MemberRole.LIBRARIAN);
        when(memberRepository.findWithFines(123L)).thenReturn(java.util.Optional.empty());

        DomainException ex = assertThrows(DomainException.class,
                () -> service.create(librarian, 123L, book));
        assertEquals(DomainException.Type.NOT_FOUND, ex.getType());
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void create_byNonPrivileged_ignoresTargetIdAndUsesSelf() {
        // Service re-loads the requesting member with fines to evaluate predicates,
        // so the requesting member's id ends up on the reservation.
        when(memberRepository.findWithFines(member.getId())).thenReturn(java.util.Optional.of(member));

        service.create(member, 999L, book);

        verify(reservationRepository).save(argThat(r -> r.getCreatedBy().equals(member)));
    }

    // -------------------------------------------------------------------------
    // find / findVisible
    // -------------------------------------------------------------------------

    @Test
    void find_notFound_throwsNotFound() {
        when(reservationRepository.find(42L)).thenReturn(java.util.Optional.empty());
        DomainException ex = assertThrows(DomainException.class, () -> service.find(42L));
        assertEquals(DomainException.Type.NOT_FOUND, ex.getType());
    }

    @Test
    void findVisible_resolverReturnsTarget_returnsTargetReservations() {
        Reservation r = new Reservation(1L, member, book, LocalDate.now(), ReservationStatus.PENDING, 1);
        when(memberVisibilityResolver.resolve(member, member.getId()))
                .thenReturn(java.util.Optional.of(member));
        when(reservationRepository.findByMember(member)).thenReturn(List.of(r));

        List<Reservation> result = service.findVisible(member, member.getId());
        assertEquals(1, result.size());
    }

    @Test
    void findVisible_resolverReturnsEmpty_returnsAll() {
        Reservation r = new Reservation(1L, member, book, LocalDate.now(), ReservationStatus.PENDING, 1);
        when(memberVisibilityResolver.resolve(member, null)).thenReturn(java.util.Optional.empty());
        when(reservationRepository.findAll()).thenReturn(List.of(r));

        List<Reservation> result = service.findVisible(member, null);
        assertEquals(1, result.size());
    }
}
