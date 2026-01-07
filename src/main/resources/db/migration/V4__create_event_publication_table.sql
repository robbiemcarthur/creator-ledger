-- Spring Modulith event publication registry
-- This table stores published events for guaranteed delivery and audit trail
CREATE TABLE event_publication (
    id UUID PRIMARY KEY,
    event_type VARCHAR(512) NOT NULL,
    listener_id VARCHAR(512) NOT NULL,
    publication_date TIMESTAMP NOT NULL,
    serialized_event TEXT NOT NULL,
    completion_date TIMESTAMP
);

-- Index for querying serialized event content
CREATE INDEX idx_event_publication_serialized_event
ON event_publication(serialized_event);

-- Index for querying completed/pending events
CREATE INDEX idx_event_publication_completion_date
ON event_publication(completion_date);
