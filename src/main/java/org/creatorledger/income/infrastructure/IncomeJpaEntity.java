package org.creatorledger.income.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * JPA entity for Income persistence.
 * <p>
 * This is an infrastructure concern and is kept separate from the domain Income.
 * The IncomeEntityMapper handles conversion between domain and persistence models.
 * </p>
 */
@Entity
@Table(name = "income")
public class IncomeJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Column(name = "received_date", nullable = false)
    private LocalDate receivedDate;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    /**
     * Default constructor for JPA.
     */
    protected IncomeJpaEntity() {
    }

    public IncomeJpaEntity(UUID id, UUID userId, UUID eventId, BigDecimal amount, String currency,
                           String description, LocalDate receivedDate, String status) {
        this.id = id;
        this.userId = userId;
        this.eventId = eventId;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
        this.receivedDate = receivedDate;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(LocalDate receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
