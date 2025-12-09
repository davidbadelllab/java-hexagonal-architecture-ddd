package com.davidbadell.hexagonal.domain.model;

import java.util.Objects;

/**
 * Value Object representing quantity
 * DDD Pattern: Value Object
 * 
 * Self-validating: ensures quantity is always positive
 */
public class Quantity {
    
    private final int value;

    public Quantity(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.value = value;
    }

    public static Quantity of(int value) {
        return new Quantity(value);
    }

    /**
     * Add quantities
     */
    public Quantity add(Quantity other) {
        return new Quantity(this.value + other.value);
    }

    /**
     * Subtract quantities (result must be positive)
     */
    public Quantity subtract(Quantity other) {
        int result = this.value - other.value;
        if (result <= 0) {
            throw new IllegalArgumentException("Resulting quantity must be positive");
        }
        return new Quantity(result);
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quantity quantity = (Quantity) o;
        return value == quantity.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
