package sk.posam.fsa.isk.jobs;

import jakarta.transaction.Transactional;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sk.posam.fsa.isk.domain.lending.Loan;
import sk.posam.fsa.isk.domain.lending.LoanRepository;
import sk.posam.fsa.isk.domain.shared.NotificationPort;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class LoanDueNotificationJob {

    private static final int NOTIFY_BEFORE_DAYS = 3;

    private final LoanRepository loanRepository;
    private final NotificationPort notificationPort;

    public LoanDueNotificationJob(LoanRepository loanRepository,
                                  NotificationPort notificationPort) {
        this.loanRepository = loanRepository;
        this.notificationPort = notificationPort;
    }

    @Scheduled(cron = "0 15 0 * * *")
    @SchedulerLock(name = "loanDueNotify", lockAtLeastFor = "PT1M", lockAtMostFor = "PT15M")
    @Transactional
    public void notifyLoansDueSoon() {
        LocalDate today = LocalDate.now();
        for (Loan loan : loanRepository.findUnreturnedLoans()) {
            long daysLeft = ChronoUnit.DAYS.between(today, loan.getDueDate());
            if (daysLeft == NOTIFY_BEFORE_DAYS) {
                notificationPort.notifyLoanDueSoon(loan.getLoanedTo(), loan, (int) daysLeft);
            }
        }
    }
}
