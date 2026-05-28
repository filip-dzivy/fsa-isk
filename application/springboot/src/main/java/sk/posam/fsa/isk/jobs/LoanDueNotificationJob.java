package sk.posam.fsa.isk.jobs;

import jakarta.transaction.Transactional;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sk.posam.fsa.isk.domain.lending.Loan;
import sk.posam.fsa.isk.domain.lending.LoanRepository;
import sk.posam.fsa.isk.domain.shared.NotificationPort;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class LoanDueNotificationJob {

    private static final Logger log = LoggerFactory.getLogger(LoanDueNotificationJob.class);
    private static final int NOTIFY_BEFORE_DAYS = 3;

    private final LoanRepository loanRepository;
    private final NotificationPort notificationPort;

    public LoanDueNotificationJob(LoanRepository loanRepository,
                                  NotificationPort notificationPort) {
        this.loanRepository = loanRepository;
        this.notificationPort = notificationPort;
    }

    @Scheduled(cron = "0 15 0 * * *", zone = "Europe/Bratislava")
    @SchedulerLock(name = "loanDueNotify", lockAtLeastFor = "PT1M", lockAtMostFor = "PT15M")
    @Transactional
    public Integer notifyLoansDueSoon() {
        LocalDate today = LocalDate.now();
        int notified = 0;
        for (Loan loan : loanRepository.findUnreturnedLoans()) {
            long daysLeft = ChronoUnit.DAYS.between(today, loan.getDueDate());
            if (daysLeft == NOTIFY_BEFORE_DAYS) {
                notificationPort.notifyLoanDueSoon(loan.getLoanedTo(), loan, (int) daysLeft);
                notified++;
            }
        }
        log.info("LoanDueNotificationJob: notified {} members of loans due in {} days", notified, NOTIFY_BEFORE_DAYS);
        return notified;
    }
}
