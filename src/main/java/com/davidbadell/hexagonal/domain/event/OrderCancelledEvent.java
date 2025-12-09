package com.davidbadell.hexagonal.domain.event;

import com.davidbadell.hexagonal.domain.model.OrderId;

import java.time.LocalDateTime;

/**
 * Domain Event: Order Cancelled
 * DDD Pattern: Domain Event
 * 
 * Raised when an order is cancelled.
 */
public class OrderCancelledEvent implements DomainEvent {
    
    private final OrderId orderId;
    private final LocalDateTime occurredOn;
    private final String reason;

    public OrderCancelledEvent(OrderId orderId, LocalDateTime occurredOn) {
        this(orderId, occurredOn, null);
    }

    public OrderCancelledEvent(OrderId orderId, LocalDateTime occurredOn, String reason) {
        this.orderId = orderId;
        this.occurredOn = occurredOn;
        this.reason = reason;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }

    @Override
    public String getEventType() {
        return "OrderCancelled";
    }

    @Override
    public String toString() {
        return String.format("OrderCancelledEvent{orderId=%s, occurredOn=%s, reason='%s'}",
                orderId, occurredOn, reason);
    }
}
