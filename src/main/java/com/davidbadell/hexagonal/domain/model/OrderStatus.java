package com.davidbadell.hexagonal.domain.model;

/**
 * Enum representing the possible states of an Order
 * DDD Pattern: Value Object (Enum)
 */
public enum OrderStatus {
    
    PENDING("Pending", "Order has been created but not yet confirmed"),
    CONFIRMED("Confirmed", "Order has been confirmed by the customer"),
    SHIPPED("Shipped", "Order has been shipped"),
    DELIVERED("Delivered", "Order has been delivered to the customer"),
    CANCELLED("Cancelled", "Order has been cancelled");

    private final String displayName;
    private final String description;

    OrderStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Check if order can be modified
     */
    public boolean isModifiable() {
        return this == PENDING;
    }

    /**
     * Check if order can be cancelled
     */
    public boolean isCancellable() {
        return this != DELIVERED && this != CANCELLED;
    }

    /**
     * Check if order is in a final state
     */
    public boolean isFinal() {
        return this == DELIVERED || this == CANCELLED;
    }
}
