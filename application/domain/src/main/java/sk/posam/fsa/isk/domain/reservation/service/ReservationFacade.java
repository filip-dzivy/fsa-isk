package sk.posam.fsa.isk.domain.reservation.service;

import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.domain.reservation.Reservation;

import java.util.List;

public interface ReservationFacade {

    public void create(Member member, Book book);

    public void cancel(Reservation reservation);

    public void notifyNextInQueue(Book book);

    public void expireReadyReservations();

    public List<Reservation> findByMember(Member member);

    public boolean hasPendingReservation(Book book);

}
