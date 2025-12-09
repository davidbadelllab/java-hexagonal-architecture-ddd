package com.davidbadell.hexagonal.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object representing the Customer identifier
 * DDD Pattern: Value Object
 */
public class CustomerId {
    
    private final String value;

    public CustomerId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("CustomerId cannot be null or blank");
        }
        this.value = value;
    }

    public static CustomerId generate() {
        return new CustomerId(UUID.randomUUID().toString());
    }

    public static CustomerId of(String value) {
        return new CustomerId(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerId that = (CustomerId) o;
        return Objects.equals(value, that.value);
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
