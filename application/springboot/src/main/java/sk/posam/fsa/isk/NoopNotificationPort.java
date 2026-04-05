package sk.posam.fsa.isk;

import org.springframework.stereotype.Component;
import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.domain.reservation.NotificationPort;

@Component
public class NoopNotificationPort implements NotificationPort {

    @Override
    public void notifyReservationReady(Member member, Book book) {
        // TODO: doimplmentovat
    }

    @Override
    public void notifyMembershipExpiringSoon(Member member, int daysLeft) {

    }
}
