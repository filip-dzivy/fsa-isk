package sk.posam.fsa.isk.jobs;

import jakarta.transaction.Transactional;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sk.posam.fsa.isk.domain.finance.Fine;
import sk.posam.fsa.isk.domain.finance.FineFactory;
import sk.posam.fsa.isk.domain.finance.FineRepository;
import sk.posam.fsa.isk.domain.finance.Money;
import sk.posam.fsa.isk.domain.lending.Loan;
import sk.posam.fsa.isk.domain.lending.LoanRepository;
import sk.posam.fsa.isk.domain.member.Member;
import sk.posam.fsa.isk.domain.member.MemberRepository;

import java.util.List;

@Component
public class FineAccrualJob {

    private static final Logger log = LoggerFactory.getLogger(FineAccrualJob.class);

    private final LoanRepository loanRepository;
    private final MemberRepository memberRepository;
    private final FineRepository fineRepository;
    private final FineFactory fineFactory;
    private final Money finesDailyRate;

    public FineAccrualJob(LoanRepository loanRepository,
                          MemberRepository memberRepository,
                          FineRepository fineRepository,
                          FineFactory fineFactory,
                          Money finesDailyRate) {
        this.loanRepository = loanRepository;
        this.memberRepository = memberRepository;
        this.fineRepository = fineRepository;
        this.fineFactory = fineFactory;
        this.finesDailyRate = finesDailyRate;
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Europe/Bratislava")
    @SchedulerLock(name = "accrueOverdueFines", lockAtLeastFor = "PT1M", lockAtMostFor = "PT15M")
    @Transactional
    public Integer accrueOverdueFines() {
        List<Loan> overdue = loanRepository.findUnreturnedLoans().stream()
                .filter(Loan::isOverdue)
                .toList();
        log.info("FineAccrualJob: found {} overdue loans to process", overdue.size());
        overdue.forEach(this::processLoan);
        log.info("FineAccrualJob: processed {} overdue loans", overdue.size());
        return overdue.size();
    }

    private void processLoan(Loan loan) {
        loan.markOverdue();
        loanRepository.save(loan);

        fineRepository.findPendingByLoan(loan)
                .ifPresentOrElse(
                        existing -> {
                            fineFactory.updateFor(existing, loan, finesDailyRate);
                            fineRepository.save(existing);
                        },
                        () -> {
                            Fine fine = fineFactory.createFor(loan, finesDailyRate);
                            Member member = loan.getLoanedTo();
                            member.addFine(fine);
                            memberRepository.save(member);
                        }
                );
    }
}
