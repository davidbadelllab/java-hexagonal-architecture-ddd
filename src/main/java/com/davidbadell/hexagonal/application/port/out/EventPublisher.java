package com.davidbadell.hexagonal.application.port.out;

import com.davidbadell.hexagonal.domain.event.DomainEvent;

/**
 * Output Port: Event Publisher
 * Hexagonal Architecture: Output Port (Driven Port)
 * 
 * This interface defines the contract for publishing domain events.
 * The implementation can use RabbitMQ, Kafka, or any other messaging system.
 */
public interface EventPublisher {
    
    /**
     * Publish a domain event
     * 
     * @param event The event to publish
     */
    void publish(DomainEvent event);
    
    /**
     * Publish a domain event to a specific topic/exchange
     * 
     * @param event The event to publish
     * @param topic The topic/exchange name
     */
    void publish(DomainEvent event, String topic);
}
