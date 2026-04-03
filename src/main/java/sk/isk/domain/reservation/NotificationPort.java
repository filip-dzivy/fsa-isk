package sk.isk.domain.reservation;

import sk.isk.domain.catalog.Book;
import sk.isk.domain.membership.Member;

public interface NotificationPort {

    void notifyReservationReady(Member member, Book book);

    void notifyMembershipExpiringSoon(Member member, int daysLeft);
}

