package org.creatorledger.event.infrastructure;

import org.creatorledger.event.domain.ClientName;
import org.creatorledger.event.domain.Event;
import org.creatorledger.event.domain.EventDate;
import org.creatorledger.event.api.EventId;

/**
 * Mapper for converting between domain Event and EventJpaEntity.
 * <p>
 * This keeps the domain model clean from persistence annotations and
 * allows the infrastructure to evolve independently from the domain.
 * </p>
 */
public class EventEntityMapper {

    /**
     * Converts a domain Event to a JPA entity.
     *
     * @param event the domain event
     * @return the JPA entity
     */
    public static EventJpaEntity toEntity(final Event event) {
        if (event == null) {
            return null;
        }
        return new EventJpaEntity(
                event.id().value(),
                event.date().value(),
                event.clientName().value(),
                event.description()
        );
    }

    /**
     * Converts a JPA entity to a domain Event.
     *
     * @param entity the JPA entity
     * @return the domain event
     */
    public static Event toDomain(EventJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Event.create(
                EventId.of(entity.getId()),
                EventDate.of(entity.getEventDate()),
                ClientName.of(entity.getClientName()),
                entity.getDescription()
        );
    }
}
