package com.davidbadell.hexagonal.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object representing the Order identifier
 * DDD Pattern: Value Object
 * 
 * Characteristics:
 * - Immutable
 * - Equality based on value, not identity
 * - Self-validating
 */
public class OrderId {
    
    private final String value;

    public OrderId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("OrderId cannot be null or blank");
        }
        this.value = value;
    }

    /**
     * Factory method to generate a new OrderId
     */
    public static OrderId generate() {
        return new OrderId(UUID.randomUUID().toString());
    }

    /**
     * Factory method to create OrderId from string
     */
    public static OrderId of(String value) {
        return new OrderId(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderId orderId = (OrderId) o;
        return Objects.equals(value, orderId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
