package com.davidbadell.hexagonal.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object representing the Product identifier
 * DDD Pattern: Value Object
 */
public class ProductId {
    
    private final String value;

    public ProductId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ProductId cannot be null or blank");
        }
        this.value = value;
    }

    public static ProductId generate() {
        return new ProductId(UUID.randomUUID().toString());
    }

    public static ProductId of(String value) {
        return new ProductId(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductId productId = (ProductId) o;
        return Objects.equals(value, productId.value);
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
