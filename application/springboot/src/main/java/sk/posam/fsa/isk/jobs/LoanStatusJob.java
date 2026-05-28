package sk.posam.fsa.isk.jobs;

import jakarta.transaction.Transactional;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sk.posam.fsa.isk.domain.lending.Loan;
import sk.posam.fsa.isk.domain.lending.LoanRepository;

import java.util.List;

@Component
public class LoanStatusJob {

    private static final Logger log = LoggerFactory.getLogger(LoanStatusJob.class);

    private final LoanRepository loanRepository;

    public LoanStatusJob(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Scheduled(cron = "0 55 23 * * *", zone = "Europe/Bratislava")
    @SchedulerLock(name = "updateLoanStatuses", lockAtLeastFor = "PT1M", lockAtMostFor = "PT15M")
    @Transactional
    public Integer updateLoanStatuses() {
        List<Loan> newlyOverdue = loanRepository.findUnreturnedLoans().stream()
                .filter(Loan::isOverdue)
                .toList();
        log.info("LoanStatusJob: found {} loans to mark as overdue", newlyOverdue.size());
        newlyOverdue.forEach(loan -> {
            loan.markOverdue();
            loanRepository.save(loan);
        });
        log.info("LoanStatusJob: marked {} loans as overdue", newlyOverdue.size());
        return newlyOverdue.size();
    }
}