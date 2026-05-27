package sk.posam.fsa.isk.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sk.posam.fsa.isk.domain.catalog.BookRepository;
import sk.posam.fsa.isk.domain.catalog.service.CatalogFacade;
import sk.posam.fsa.isk.domain.catalog.service.CatalogService;
import sk.posam.fsa.isk.domain.lending.LoanRepository;
import sk.posam.fsa.isk.domain.reservation.ReservationRepository;
import sk.posam.fsa.isk.domain.shared.DomainEventPublisher;
import sk.posam.fsa.isk.domain.shared.PhotoStoragePort;

@Configuration
public class CatalogBeanConfiguration {

    @Bean
    public CatalogFacade catalogFacade(BookRepository bookRepository,
                                       LoanRepository loanRepository,
                                       ReservationRepository reservationRepository,
                                       DomainEventPublisher eventPublisher,
                                       PhotoStoragePort photoStoragePort) {
        return new CatalogService(bookRepository, loanRepository, reservationRepository,
                eventPublisher, photoStoragePort);
    }
}
