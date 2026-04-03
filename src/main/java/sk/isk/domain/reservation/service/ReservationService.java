package sk.isk.domain.reservation.service;

import sk.isk.domain.catalog.Book;
import sk.isk.domain.membership.Member;
import sk.isk.domain.membership.predicate.HasActiveMembershipPredicate;
import sk.isk.domain.membership.predicate.HasNoUnpaidFinesPredicate;
import sk.isk.domain.reservation.NotificationPort;
import sk.isk.domain.reservation.Reservation;
import sk.isk.domain.reservation.ReservationRepository;
import sk.isk.domain.reservation.predicate.IsPendingReservationPredicate;
import sk.isk.domain.shared.DomainException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static sk.isk.domain.shared.DomainException.*;

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
                .anyMatch(r -> r.getCreatedBy().equals(member.getId()));
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
        List<Reservation> active = new ArrayList<>(reservationRepository.findActiveByBook(book));
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
