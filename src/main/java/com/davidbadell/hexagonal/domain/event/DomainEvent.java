package com.davidbadell.hexagonal.domain.event;

import java.time.LocalDateTime;

/**
 * Base interface for all Domain Events
 * DDD Pattern: Domain Event
 * 
 * Domain Events represent something that happened in the domain
 * that domain experts care about.
 */
public interface DomainEvent {
    
    /**
     * Get the timestamp when the event occurred
     */
    LocalDateTime getOccurredOn();
    
    /**
     * Get the event type name
     */
    String getEventType();
}
