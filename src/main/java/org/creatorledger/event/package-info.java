/**
 * Event module - creator events and engagements.
 * <p>
 * This module manages professional events, workshops, performances,
 * and other engagements for creatives.
 * </p>
 * <p>
 * <strong>Dependencies:</strong>
 * </p>
 * <ul>
 *   <li>{@code common} - shared kernel (EventId, UserId)</li>
 * </ul>
 */
@org.springframework.modulith.ApplicationModule(
    displayName = "Event",
    allowedDependencies = {"common"}
)
package org.creatorledger.event;
