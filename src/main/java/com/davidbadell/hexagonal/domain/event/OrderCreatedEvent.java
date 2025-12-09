package com.davidbadell.hexagonal.domain.event;

import com.davidbadell.hexagonal.domain.model.CustomerId;
import com.davidbadell.hexagonal.domain.model.OrderId;

import java.time.LocalDateTime;

/**
 * Domain Event: Order Created
 * DDD Pattern: Domain Event
 * 
 * Raised when a new order is created in the system.
 */
public class OrderCreatedEvent implements DomainEvent {
    
    private final OrderId orderId;
    private final CustomerId customerId;
    private final LocalDateTime occurredOn;

    public OrderCreatedEvent(OrderId orderId, CustomerId customerId, LocalDateTime occurredOn) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.occurredOn = occurredOn;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    @Override
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }

    @Override
    public String getEventType() {
        return "OrderCreated";
    }

    @Override
    public String toString() {
        return String.format("OrderCreatedEvent{orderId=%s, customerId=%s, occurredOn=%s}",
                orderId, customerId, occurredOn);
    }
}
