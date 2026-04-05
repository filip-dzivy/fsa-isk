package sk.posam.fsa.isk.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sk.posam.fsa.isk.domain.catalog.BookRepository;
import sk.posam.fsa.isk.domain.finance.FineFactory;
import sk.posam.fsa.isk.domain.finance.FineRepository;
import sk.posam.fsa.isk.domain.finance.Money;
import sk.posam.fsa.isk.domain.lending.LoanFactory;
import sk.posam.fsa.isk.domain.lending.LoanRepository;
import sk.posam.fsa.isk.domain.lending.service.LoanFacade;
import sk.posam.fsa.isk.domain.lending.service.LoanService;
import sk.posam.fsa.isk.domain.member.MemberRepository;
import sk.posam.fsa.isk.domain.shared.DomainEventPublisher;

@Configuration
public class LendingBeanConfiguration {

    @Bean
    public Money finesDailyRate() {
        return Money.of(0.50, "EUR");
    }

    @Bean
    public LoanFactory loanFactory() {
        return new LoanFactory();
    }

    @Bean
    public FineFactory fineFactory() {return new FineFactory();}

    @Bean
    public LoanFacade loanFacade(BookRepository bookRepository,
                                 MemberRepository memberRepository,
                                 LoanRepository loanRepository,
                                 LoanFactory loanFactory,
                                 FineRepository fineRepository,
                                 DomainEventPublisher eventPublisher,
                                 Money finesDailyRate,
                                 FineFactory fineFactory) {
        return new LoanService(bookRepository, memberRepository, loanRepository,
                loanFactory, fineRepository, eventPublisher, finesDailyRate, fineFactory);
    }
}
