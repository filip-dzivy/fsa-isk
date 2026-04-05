package sk.posam.fsa.isk;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sk.posam.fsa.isk.domain.catalog.BookRepository;
import sk.posam.fsa.isk.domain.lending.LoanFactory;
import sk.posam.fsa.isk.domain.lending.LoanRepository;
import sk.posam.fsa.isk.domain.lending.service.FineService;
import sk.posam.fsa.isk.domain.lending.service.LoanFacade;
import sk.posam.fsa.isk.domain.lending.service.LoanService;
import sk.posam.fsa.isk.domain.member.MemberRepository;
import sk.posam.fsa.isk.domain.reservation.service.ReservationService;

@Configuration
public class LendingBeanConfiguration {

    @Bean
    public FineService fineService() {
        return new FineService();
    }

    @Bean
    public LoanFactory loanFactory() {
        return new LoanFactory();
    }

    @Bean
    public LoanFacade loanFacade(BookRepository bookRepository,
                                 MemberRepository memberRepository,
                                 LoanRepository loanRepository,
                                 LoanFactory loanFactory,
                                 FineService fineService,
                                 ReservationService reservationService) {
        return new LoanService(bookRepository, memberRepository, loanRepository,
                loanFactory, fineService, reservationService);
    }
}
