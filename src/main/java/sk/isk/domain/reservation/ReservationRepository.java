package sk.isk.domain.reservation;

import sk.isk.domain.catalog.Book;
import sk.isk.domain.membership.Member;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository {

    Optional<Reservation> find(long id);

    Collection<Reservation> findByMember(Member member);

    Collection<Reservation> findByBook(Book book);

    Collection<Reservation> findActiveByBook(Book book);

    Collection<Reservation> findAll();

    void save(Reservation reservation);
}
