package sk.isk.domain.reservation.service;

import sk.isk.domain.catalog.Book;
import sk.isk.domain.membership.Member;
import sk.isk.domain.reservation.Reservation;

import java.util.List;

public interface ReservationFacade {

    public void create(Member member, Book book);

    public void cancel(Reservation reservation);

    public void notifyNextInQueue(Book book);

    public void expireReadyReservations();

    public List<Reservation> findByMember(Member member);

    public boolean hasPendingReservation(Book book);

}
