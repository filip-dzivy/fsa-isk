package sk.posam.fsa.isk;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sk.posam.fsa.isk.domain.catalog.BookRepository;
import sk.posam.fsa.isk.domain.catalog.service.CatalogFacade;
import sk.posam.fsa.isk.domain.catalog.service.CatalogService;

@Configuration
public class CatalogBeanConfiguration {

    @Bean
    public CatalogFacade catalogFacade(BookRepository bookRepository) {
        return new CatalogService(bookRepository);
    }
}
