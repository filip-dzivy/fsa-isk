package sk.posam.fsa.isk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import sk.posam.fsa.isk.domain.catalog.Book;
import sk.posam.fsa.isk.domain.lending.Loan;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.domain.shared.NotificationPort;

/* Fallback Adapter*/
@Component
@ConditionalOnProperty(name = "notifications.email.enabled", havingValue = "false", matchIfMissing = true)
public class NoopNotificationPort implements NotificationPort {

    private static final Logger log = LoggerFactory.getLogger(NoopNotificationPort.class);

    @Override
    public void notifyReservationReady(Member member, Book book) {
        log.info("[noop-notify] reservation ready: member={} book={}", member.getEmail(), book.getIsbn());
    }

    @Override
    public void notifyMembershipExpiringSoon(Member member, int daysLeft) {
        log.info("[noop-notify] membership expiring: member={} daysLeft={}", member.getEmail(), daysLeft);
    }

    @Override
    public void notifyLoanDueSoon(Member member, Loan loan, int daysLeft) {
        log.info("[noop-notify] loan due soon: member={} loan={} daysLeft={}",
                member.getEmail(), loan.getId(), daysLeft);
    }
}
