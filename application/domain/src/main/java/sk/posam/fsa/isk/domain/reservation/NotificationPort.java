package sk.posam.fsa.isk.domain.reservation;

import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.member.Member;

public interface NotificationPort {

    void notifyReservationReady(Member member, Book book);

    void notifyMembershipExpiringSoon(Member member, int daysLeft);
}

