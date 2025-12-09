package com.davidbadell.hexagonal.domain.model;

import java.util.Objects;

/**
 * Entity representing a line item in an Order
 * DDD Pattern: Entity (part of Order Aggregate)
 * 
 * OrderLine belongs to Order aggregate and cannot exist independently.
 */
public class OrderLine {
    
    private final ProductId productId;
    private final Quantity quantity;
    private final Money unitPrice;
    private final String productName;

    public OrderLine(ProductId productId, String productName, Quantity quantity, Money unitPrice) {
        if (productId == null) {
            throw new IllegalArgumentException("ProductId cannot be null");
        }
        if (quantity == null) {
            throw new IllegalArgumentException("Quantity cannot be null");
        }
        if (unitPrice == null) {
            throw new IllegalArgumentException("Unit price cannot be null");
        }
        
        this.productId = productId;
        this.productName = productName != null ? productName : "";
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    /**
     * Calculate the subtotal for this line
     */
    public Money getSubtotal() {
        return unitPrice.multiply(quantity.getValue());
    }

    // Getters
    public ProductId getProductId() { return productId; }
    public String getProductName() { return productName; }
    public Quantity getQuantity() { return quantity; }
    public Money getUnitPrice() { return unitPrice; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderLine orderLine = (OrderLine) o;
        return Objects.equals(productId, orderLine.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }

    @Override
    public String toString() {
        return String.format("OrderLine{productId=%s, productName='%s', quantity=%d, unitPrice=%s}",
                productId, productName, quantity.getValue(), unitPrice);
    }
}
