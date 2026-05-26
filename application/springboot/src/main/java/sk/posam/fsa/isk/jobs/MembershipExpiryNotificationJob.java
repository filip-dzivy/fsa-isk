package sk.posam.fsa.isk.jobs;

import jakarta.transaction.Transactional;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.domain.member.MemberRepository;
import sk.posam.fsa.isk.domain.member.Membership;
import sk.posam.fsa.isk.domain.shared.NotificationPort;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class MembershipExpiryNotificationJob {

    private static final int NOTIFY_BEFORE_DAYS = 7;

    private final MemberRepository memberRepository;
    private final NotificationPort notificationPort;

    public MembershipExpiryNotificationJob(MemberRepository memberRepository,
                                           NotificationPort notificationPort) {
        this.memberRepository = memberRepository;
        this.notificationPort = notificationPort;
    }

    @Scheduled(cron = "0 10 0 * * *")
    @SchedulerLock(name = "membershipExpiryNotify", lockAtLeastFor = "PT1M", lockAtMostFor = "PT15M")
    @Transactional
    public void notifyExpiringMemberships() {
        LocalDate today = LocalDate.now();
        for (Member member : memberRepository.findAll()) {
            Membership membership = member.getMembership();
            if (membership == null || membership.getExpiryDate() == null) continue;
            long daysLeft = ChronoUnit.DAYS.between(today, membership.getExpiryDate());
            if (daysLeft == NOTIFY_BEFORE_DAYS) {
                notificationPort.notifyMembershipExpiringSoon(member, (int) daysLeft);
            }
        }
    }
}
