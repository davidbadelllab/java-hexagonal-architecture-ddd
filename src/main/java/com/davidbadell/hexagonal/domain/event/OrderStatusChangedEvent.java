package com.davidbadell.hexagonal.domain.event;

import com.davidbadell.hexagonal.domain.model.OrderId;
import com.davidbadell.hexagonal.domain.model.OrderStatus;

import java.time.LocalDateTime;

/**
 * Domain Event: Order Status Changed
 * DDD Pattern: Domain Event
 * 
 * Raised when an order status changes.
 */
public class OrderStatusChangedEvent implements DomainEvent {
    
    private final OrderId orderId;
    private final OrderStatus previousStatus;
    private final OrderStatus newStatus;
    private final LocalDateTime occurredOn;

    public OrderStatusChangedEvent(OrderId orderId, OrderStatus previousStatus, 
                                   OrderStatus newStatus, LocalDateTime occurredOn) {
        this.orderId = orderId;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.occurredOn = occurredOn;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public OrderStatus getPreviousStatus() {
        return previousStatus;
    }

    public OrderStatus getNewStatus() {
        return newStatus;
    }

    @Override
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }

    @Override
    public String getEventType() {
        return "OrderStatusChanged";
    }

    @Override
    public String toString() {
        return String.format("OrderStatusChangedEvent{orderId=%s, from=%s, to=%s, occurredOn=%s}",
                orderId, previousStatus, newStatus, occurredOn);
    }
}
