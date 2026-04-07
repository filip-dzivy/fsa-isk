package sk.posam.fsa.isk.domain.reservation.service;

import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.domain.reservation.Reservation;

import java.util.List;

public interface ReservationFacade {

    public void create(Member member, Book book);

    public void create(Member requestingMember, Long targetMemberId, Book book);

    public void cancel(Reservation reservation, Member requestedBy);

    public void notifyNextInQueue(Book book);

    public void expireReadyReservations();

    public List<Reservation> findByMember(Member member);

    public List<Reservation> findAll();

    public List<Reservation> findByBook(Book book);

    Reservation find(long id);

    public boolean hasPendingReservation(Book book);

    public List<Reservation> findVisible(Member requestingMember, Long targetMemberId);

}
