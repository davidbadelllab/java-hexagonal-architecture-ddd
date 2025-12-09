package com.davidbadell.hexagonal.domain.exception;

import com.davidbadell.hexagonal.domain.model.OrderId;

/**
 * Exception thrown when an order is not found
 */
public class OrderNotFoundException extends DomainException {
    
    private final OrderId orderId;

    public OrderNotFoundException(OrderId orderId) {
        super(String.format("Order not found with id: %s", orderId.getValue()));
        this.orderId = orderId;
    }

    public OrderId getOrderId() {
        return orderId;
    }
}
