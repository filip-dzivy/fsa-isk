// src/main/java/sk/librasys/domain/shared/DomainEvent.java
package sk.isk.domain.shared;

import java.time.LocalDateTime;

public interface DomainEvent {
    LocalDateTime occurredOn();
}