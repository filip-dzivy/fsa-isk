package sk.posam.fsa.isk.domain.shared;

import java.time.Instant;

public abstract class DomainEvent {

    private final Instant occurredOn;

    protected DomainEvent() {
        this.occurredOn = Instant.now();
    }

    public Instant getOccurredOn() {
        return occurredOn;
    }
}
