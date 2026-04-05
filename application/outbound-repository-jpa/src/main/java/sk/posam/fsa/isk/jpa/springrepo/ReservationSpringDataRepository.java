package sk.posam.fsa.isk.jpa.springrepo;

import org.springframework.data.jpa.repository.JpaRepository;
import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.domain.reservation.Reservation;
import sk.posam.fsa.isk.domain.reservation.ReservationStatus;

import java.util.Collection;
import java.util.List;

public interface ReservationSpringDataRepository extends JpaRepository<Reservation, Long> {

    Collection<Reservation> findByCreatedBy(Member member);

    Collection<Reservation> findByBook(Book book);

    Collection<Reservation> findByBookAndStatusIn(Book book, List<ReservationStatus> statuses);
}
