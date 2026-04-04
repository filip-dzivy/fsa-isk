package sk.posam.fsa.isk.domain.reservation;

import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.membership.Member;

import java.util.Collection;
import java.util.Optional;

public interface ReservationRepository {

    Optional<Reservation> find(long id);

    Collection<Reservation> findByMember(Member member);

    Collection<Reservation> findByBook(Book book);

    Collection<Reservation> findActiveByBook(Book book);

    Collection<Reservation> findAll();

    void save(Reservation reservation);
}
