package sk.posam.fsa.isk.domain.reservation.service;

import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.domain.member.predicate.HasActiveMembershipPredicate;
import sk.posam.fsa.isk.domain.member.predicate.HasNoUnpaidFinesPredicate;
import sk.posam.fsa.isk.domain.reservation.NotificationPort;
import sk.posam.fsa.isk.domain.reservation.Reservation;
import sk.posam.fsa.isk.domain.reservation.ReservationRepository;
import sk.posam.fsa.isk.domain.reservation.predicate.IsPendingReservationPredicate;
import sk.posam.fsa.isk.domain.shared.DomainException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static sk.posam.fsa.isk.domain.shared.DomainException.*;

public class ReservationService implements ReservationFacade{
    private final ReservationRepository reservationRepository;
    private final NotificationPort notificationPort;


    public ReservationService(ReservationRepository reservationRepository, NotificationPort notificationPort) {
        this.reservationRepository  = reservationRepository;
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
    public void cancel(Reservation reservation) {
        reservation.cancel();
        reservationRepository.save(reservation);
        rebuildQueue(reservation.getBook());
    }

    @Override
    public void notifyNextInQueue(Book book) {
        reservationRepository.findActiveByBook(book)
                .stream()
                .filter(IsPendingReservationPredicate.INSTANCE)
                .min(Comparator.comparingInt(Reservation::getPositionInQueue))
                .ifPresent(first -> {
                    first.activate();
                    reservationRepository.save(first);
                    notificationPort.notifyReservationReady(first.getCreatedBy(), book);
                });
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
    public boolean hasPendingReservation(Book book) {
        return reservationRepository.findActiveByBook(book)
                .stream()
                .anyMatch(Reservation::isActive);
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
