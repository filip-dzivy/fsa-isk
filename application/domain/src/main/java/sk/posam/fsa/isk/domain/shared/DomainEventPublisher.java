package sk.posam.fsa.isk.domain.shared;

public interface DomainEventPublisher {

    void publish(DomainEvent event);
}
