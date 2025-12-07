package com.davidbadell.hexagonal.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Order Aggregate Root - Domain Model
 * DDD Pattern: Aggregate
 */
public class Order {
    private OrderId id;
    private CustomerId customerId;
    private List<OrderLine> orderLines;
    private OrderStatus status;
    private Money total;
    private LocalDateTime createdAt;

    public Order(OrderId id, CustomerId customerId) {
        this.id = id;
        this.customerId = customerId;
        this.orderLines = new ArrayList<>();
        this.status = OrderStatus.PENDING;
        this.total = Money.ZERO;
        this.createdAt = LocalDateTime.now();
    }

    public void addOrderLine(OrderLine orderLine) {
        this.orderLines.add(orderLine);
        calculateTotal();
    }

    public void removeOrderLine(OrderLine orderLine) {
        this.orderLines.remove(orderLine);
        calculateTotal();
    }

    private void calculateTotal() {
        this.total = orderLines.stream()
                .map(OrderLine::getSubtotal)
                .reduce(Money.ZERO, Money::add);
    }

    public void confirm() {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("Only pending orders can be confirmed");
        }
        this.status = OrderStatus.CONFIRMED;
    }

    public void cancel() {
        if (this.status == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Delivered orders cannot be cancelled");
        }
        this.status = OrderStatus.CANCELLED;
    }

    // Getters
    public OrderId getId() { return id; }
    public CustomerId getCustomerId() { return customerId; }
    public List<OrderLine> getOrderLines() { return new ArrayList<>(orderLines); }
    public OrderStatus getStatus() { return status; }
    public Money getTotal() { return total; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

// Value Objects
class OrderId {
    private final String value;
    public OrderId(String value) { this.value = value; }
    public String getValue() { return value; }
}

class CustomerId {
    private final String value;
    public CustomerId(String value) { this.value = value; }
    public String getValue() { return value; }
}

class OrderLine {
    private final ProductId productId;
    private final Quantity quantity;
    private final Money price;

    public OrderLine(ProductId productId, Quantity quantity, Money price) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public Money getSubtotal() {
        return price.multiply(quantity.getValue());
    }

    public ProductId getProductId() { return productId; }
    public Quantity getQuantity() { return quantity; }
    public Money getPrice() { return price; }
}

class Money {
    public static final Money ZERO = new Money(BigDecimal.ZERO);
    private final BigDecimal amount;

    public Money(BigDecimal amount) { this.amount = amount; }
    public Money add(Money other) { return new Money(this.amount.add(other.amount)); }
    public Money multiply(int factor) { return new Money(this.amount.multiply(BigDecimal.valueOf(factor))); }
    public BigDecimal getAmount() { return amount; }
}

class ProductId {
    private final String value;
    public ProductId(String value) { this.value = value; }
    public String getValue() { return value; }
}

class Quantity {
    private final int value;
    public Quantity(int value) {
        if (value <= 0) throw new IllegalArgumentException("Quantity must be positive");
        this.value = value;
    }
    public int getValue() { return value; }
}

enum OrderStatus {
    PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
}
