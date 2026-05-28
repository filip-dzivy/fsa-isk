package sk.posam.fsa.isk.jobs;

import jakarta.transaction.Transactional;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sk.posam.fsa.isk.domain.member.service.MemberFacade;

@Component
public class MembershipExpirationJob {

    private static final Logger log = LoggerFactory.getLogger(MembershipExpirationJob.class);

    private final MemberFacade memberFacade;

    public MembershipExpirationJob(MemberFacade memberFacade) {
        this.memberFacade = memberFacade;
    }

    @Scheduled(cron = "0 20 0 * * *", zone = "Europe/Bratislava")
    @SchedulerLock(name = "expireOverdueMemberships", lockAtLeastFor = "PT1M", lockAtMostFor = "PT15M")
    @Transactional
    public Integer expireOverdueMemberships() {
        int count = memberFacade.expireOverdueMemberships();
        log.info("MembershipExpirationJob: marked {} memberships as EXPIRED", count);
        return count;
    }
}