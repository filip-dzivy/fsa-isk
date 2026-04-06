package sk.posam.fsa.isk.events;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import sk.posam.fsa.isk.domain.shared.DomainEvent;
import sk.posam.fsa.isk.domain.shared.DomainEventPublisher;

@Component
public class SpringDomainEventPublisher implements DomainEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public SpringDomainEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void publish(DomainEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}
