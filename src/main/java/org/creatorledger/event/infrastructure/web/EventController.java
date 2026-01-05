package org.creatorledger.event.infrastructure.web;

import org.creatorledger.event.application.CreateEventCommand;
import org.creatorledger.event.application.EventApplicationService;
import org.creatorledger.event.application.UpdateEventCommand;
import org.creatorledger.event.api.EventId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

/**
 * REST controller for event-related endpoints.
 * <p>
 * This controller provides HTTP API access to event functionality,
 * translating HTTP requests/responses to application commands/queries.
 * </p>
 */
@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventApplicationService eventApplicationService;

    public EventController(EventApplicationService eventApplicationService) {
        this.eventApplicationService = eventApplicationService;
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody final CreateEventRequest request) {
        try {
            CreateEventCommand command = new CreateEventCommand(
                    request.date(),
                    request.clientName(),
                    request.description()
            );
            EventId eventId = eventApplicationService.create(command);

            URI location = URI.create("/api/events/" + eventId.value());
            return ResponseEntity.created(location).build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable final String id, @RequestBody final UpdateEventRequest request) {
        try {
            UUID uuid = UUID.fromString(id);
            EventId eventId = EventId.of(uuid);

            UpdateEventCommand command = new UpdateEventCommand(
                    eventId,
                    request.date(),
                    request.clientName(),
                    request.description()
            );
            eventApplicationService.update(command);

            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Gets an event by its ID.
     *
     * @param id the event ID
     * @return 200 OK with event data if found, 404 Not Found otherwise
     */
    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEvent(@PathVariable final String id) {
        try {
            UUID uuid = UUID.fromString(id);
            EventId eventId = EventId.of(uuid);

            return eventApplicationService.findById(eventId)
                    .map(EventResponse::from)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Checks if an event exists with the given ID.
     *
     * @param id the event ID
     * @return 200 OK with boolean indicating existence
     */
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existsById(@PathVariable final String id) {
        try {
            UUID uuid = UUID.fromString(id);
            EventId eventId = EventId.of(uuid);

            boolean exists = eventApplicationService.existsById(eventId);
            return ResponseEntity.ok(exists);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
