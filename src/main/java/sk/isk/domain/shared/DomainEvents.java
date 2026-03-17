// src/main/java/sk/librasys/domain/shared/DomainEvents.java
package sk.isk.domain.shared;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DomainEvents {

    private static final List<Consumer<DomainEvent>> handlers = new ArrayList<>();

    public static void register(Consumer<DomainEvent> handler) {
        handlers.add(handler);
    }

    public static void publish(DomainEvent event) {
        for (Consumer<DomainEvent> handler : handlers) {
            handler.accept(event);
        }
    }

    public static void clearHandlers() {
        handlers.clear();
    }
}