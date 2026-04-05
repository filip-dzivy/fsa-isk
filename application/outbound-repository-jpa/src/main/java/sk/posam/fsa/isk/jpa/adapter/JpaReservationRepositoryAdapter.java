package sk.posam.fsa.isk.jpa.adapter;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.domain.reservation.Reservation;
import sk.posam.fsa.isk.domain.reservation.ReservationRepository;
import sk.posam.fsa.isk.domain.reservation.ReservationStatus;
import sk.posam.fsa.isk.jpa.springrepo.ReservationSpringDataRepository;

import java.util.Collection;
import java.util.Optional;

@Repository
public class JpaReservationRepositoryAdapter implements ReservationRepository {

    private final ReservationSpringDataRepository reservationSpringDataRepository;
    private final EntityManager entityManager;

    public JpaReservationRepositoryAdapter(ReservationSpringDataRepository reservationSpringDataRepository,
                                           EntityManager entityManager) {
        this.reservationSpringDataRepository = reservationSpringDataRepository;
        this.entityManager = entityManager;
    }

    @Override
    public Optional<Reservation> find(long id) {
        return reservationSpringDataRepository.findById(id);
    }

    @Override
    public Collection<Reservation> findByMember(Member member) {
        return reservationSpringDataRepository.findByCreatedBy(member);
    }

    @Override
    public Collection<Reservation> findByBook(Book book) {
        return reservationSpringDataRepository.findByBook(book);
    }

    @Override
    public Collection<Reservation> findActiveByBook(Book book) {
        return reservationSpringDataRepository.findByBookAndStatusIn(
                book,
                java.util.List.of(ReservationStatus.PENDING, ReservationStatus.READY_FOR_PICKUP)
        );
    }

    @Override
    public Collection<Reservation> findAll() {
        return reservationSpringDataRepository.findAll();
    }

    @Override
    @Transactional
    public void save(Reservation reservation) {
        attachReferences(reservation);
        entityManager.merge(reservation);
    }

    private void attachReferences(Reservation reservation) {
        if (reservation.getCreatedBy() != null) {
            reservation.setCreatedBy(
                    entityManager.getReference(Member.class, reservation.getCreatedBy().getId())
            );
        }
        if (reservation.getBook() != null) {
            reservation.setBook(
                    entityManager.getReference(Book.class, reservation.getBook().getId())
            );
        }
    }
}
