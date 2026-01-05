package org.creatorledger.event.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

/**
 * JPA entity for Event persistence.
 * <p>
 * This is an infrastructure concern and is kept separate from the domain Event.
 * The EventEntityMapper handles conversion between domain and persistence models.
 * </p>
 */
@Entity
@Table(name = "events")
public class EventJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;

    @Column(name = "client_name", nullable = false, length = 200)
    private String clientName;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    /**
     * Default constructor for JPA.
     */
    protected EventJpaEntity() {
    }

    public EventJpaEntity(final UUID id, final LocalDate eventDate, final String clientName, final String description) {
        this.id = id;
        this.eventDate = eventDate;
        this.clientName = clientName;
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(final String clientName) {
        this.clientName = clientName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }
}
