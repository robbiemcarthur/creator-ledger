package org.creatorledger.event.application;

import org.creatorledger.event.domain.Event;
import org.creatorledger.event.api.EventId;

import java.util.Optional;

/**
 * Repository interface for Event aggregate persistence.
 * <p>
 * This is a port (in hexagonal architecture) that defines the contract
 * for persisting and retrieving Event aggregates. The infrastructure layer
 * provides the actual implementation (adapter).
 * </p>
 */
public interface EventRepository {

    Event save(Event event);
    Optional<Event> findById(EventId id);
    boolean existsById(EventId id);
    void delete(Event event);
}
