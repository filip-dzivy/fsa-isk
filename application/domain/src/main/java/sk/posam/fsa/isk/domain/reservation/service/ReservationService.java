package sk.posam.fsa.isk.domain.reservation.service;

import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.domain.member.MemberRepository;
import sk.posam.fsa.isk.domain.member.MemberRole;
import sk.posam.fsa.isk.domain.member.access.MemberVisibilityResolver;
import sk.posam.fsa.isk.domain.member.predicate.HasActiveMembershipPredicate;
import sk.posam.fsa.isk.domain.member.predicate.HasNoUnpaidFinesPredicate;
import sk.posam.fsa.isk.domain.shared.NotificationPort;
import sk.posam.fsa.isk.domain.reservation.Reservation;
import sk.posam.fsa.isk.domain.reservation.ReservationRepository;
import sk.posam.fsa.isk.domain.reservation.ReservationStatus;
import sk.posam.fsa.isk.domain.reservation.predicate.IsPendingReservationPredicate;
import sk.posam.fsa.isk.domain.reservation.predicate.IsReadyForPickupPredicate;
import sk.posam.fsa.isk.domain.shared.DomainException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static sk.posam.fsa.isk.domain.shared.DomainException.*;

public class ReservationService implements ReservationFacade{
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final MemberVisibilityResolver memberVisibilityResolver;
    private final NotificationPort notificationPort;


    public ReservationService(ReservationRepository reservationRepository,
                              MemberRepository memberRepository,
                              MemberVisibilityResolver memberVisibilityResolver,
                              NotificationPort notificationPort) {
        this.reservationRepository  = reservationRepository;
        this.memberRepository = memberRepository;
        this.memberVisibilityResolver = memberVisibilityResolver;
        this.notificationPort = notificationPort;
    }

    @Override
    public void create(Member member, Book book) {
        require(HasActiveMembershipPredicate.INSTANCE.test(member.getMembership()),
                Type.FORBIDDEN,
                "Čitateľ " + member.getId() + " nemá platné členstvo.");
        require(HasNoUnpaidFinesPredicate.INSTANCE.test(member),
                Type.FORBIDDEN,
                "Čitateľ " + member.getId() + " má neuhradené pokuty.");

        boolean alreadyReserved = reservationRepository.findActiveByBook(book)
                .stream()
                .anyMatch(r -> r.getCreatedBy().equals(member));
        require(!alreadyReserved,
                Type.CONFLICT,
                "Čitateľ " + member.getId() + " má už aktívnu rezerváciu knihy " + book.getIsbn() + ".");

        List<Reservation> queue = reservationRepository.findActiveByBook(book).stream().toList();
        Reservation reservation = new Reservation(member, book);
        reservation.setPositionInQueue(queue.size() + 1);
        reservationRepository.save(reservation);
    }

    @Override
    public void create(Member requestingMember, Long targetMemberId, Book book) {
        Member member = requestingMember.isPrivileged()
                ? memberRepository.find(targetMemberId)
                        .orElseThrow(() -> new DomainException(
                                Type.NOT_FOUND,
                                "Člen s ID " + targetMemberId + " neexistuje."))
                : requestingMember;
        create(member, book);
    }

    @Override
    public void cancel(Reservation reservation, Member requestedBy) {
        reservation.cancel(requestedBy);
        reservationRepository.save(reservation);
        rebuildQueue(reservation.getBook());
    }

    @Override
    public void notifyNextInQueue(Book book) {
        List<Reservation> active = reservationRepository.findActiveByBook(book).stream().toList();
        long readyCount = active.stream().filter(IsReadyForPickupPredicate.INSTANCE).count();
        int freeSlots = book.getAvailableCopies() - (int) readyCount;
        if (freeSlots <= 0) return;

        List<Reservation> toActivate = active.stream()
                .filter(IsPendingReservationPredicate.INSTANCE)
                .sorted(Comparator.comparingInt(Reservation::getPositionInQueue))
                .limit(freeSlots)
                .toList();

        for (Reservation r : toActivate) {
            r.activate();
            reservationRepository.save(r);
            notificationPort.notifyReservationReady(r.getCreatedBy(), book);
        }
    }

    @Override
    public void expireReadyReservations() {
        reservationRepository.findAll().stream()
                .filter(Reservation::isExpiredByDate)
                .forEach(r -> {
                    r.expire();
                    reservationRepository.save(r);
                    notifyNextInQueue(r.getBook());
                });
    }

    @Override
    public List<Reservation> findByMember(Member member) {
        return reservationRepository.findByMember(member).stream().toList();
    }

    @Override
    public List<Reservation> findByBook(Book book) {
        return reservationRepository.findByBook(book).stream().toList();
    }

    @Override
    public List<Reservation> findAll() {
        return reservationRepository.findAll().stream().toList();
    }

    @Override
    public Reservation find(long id){
        return reservationRepository.find(id)
                .orElseThrow(()->new DomainException(
                        Type.NOT_FOUND,
                        "Reservation not found."));
    }

    @Override
    public void fulfillReservation(Member member, Book book) {
        reservationRepository.findActiveByBook(book).stream()
                .filter(r -> r.getCreatedBy().equals(member))
                .filter(r -> r.getStatus() == ReservationStatus.READY_FOR_PICKUP)
                .findFirst()
                .ifPresent(r -> {
                    r.fulfill();
                    reservationRepository.save(r);
                });
    }

    @Override
    public boolean hasPendingReservation(Book book) {
        return reservationRepository.findActiveByBook(book)
                .stream()
                .anyMatch(Reservation::isActive);
    }

    @Override
    public List<Reservation> findVisible(Member requestingMember, Long targetMemberId) {
        return memberVisibilityResolver.resolve(requestingMember, targetMemberId)
                .map(target -> reservationRepository.findByMember(target).stream().toList())
                .orElseGet(() -> reservationRepository.findAll().stream().toList());
    }

    private void rebuildQueue(Book book) {
        List<Reservation> active = new ArrayList<>(reservationRepository.findActiveByBook(book))
                .stream()
                .filter(Reservation::isActive)
                .collect(Collectors.toCollection(ArrayList::new));
        active.sort(Comparator.comparingInt(Reservation::getPositionInQueue));
        for (int i = 0; i < active.size(); i++) {
            active.get(i).setPositionInQueue(i + 1);
            reservationRepository.save(active.get(i));
        }
    }

    private void require(boolean valid, Type type, String message) {
        if (!valid) throw new DomainException(type, message);
    }
}
