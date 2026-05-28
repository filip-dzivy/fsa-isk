package sk.posam.fsa.isk.jobs;

import jakarta.transaction.Transactional;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(MembershipExpiryNotificationJob.class);
    private static final int NOTIFY_BEFORE_DAYS = 7;

    private final MemberRepository memberRepository;
    private final NotificationPort notificationPort;

    public MembershipExpiryNotificationJob(MemberRepository memberRepository,
                                           NotificationPort notificationPort) {
        this.memberRepository = memberRepository;
        this.notificationPort = notificationPort;
    }

    @Scheduled(cron = "0 10 0 * * *", zone = "Europe/Bratislava")
    @SchedulerLock(name = "membershipExpiryNotify", lockAtLeastFor = "PT1M", lockAtMostFor = "PT15M")
    @Transactional
    public Integer notifyExpiringMemberships() {
        LocalDate today = LocalDate.now();
        int notified = 0;
        for (Member member : memberRepository.findAll()) {
            Membership membership = member.getMembership();
            if (membership == null || membership.getExpiryDate() == null) continue;
            long daysLeft = ChronoUnit.DAYS.between(today, membership.getExpiryDate());
            if (daysLeft == NOTIFY_BEFORE_DAYS) {
                notificationPort.notifyMembershipExpiringSoon(member, (int) daysLeft);
                notified++;
            }
        }
        log.info("MembershipExpiryNotificationJob: notified {} members of membership expiring in {} days", notified, NOTIFY_BEFORE_DAYS);
        return notified;
    }
}
