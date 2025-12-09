package com.davidbadell.hexagonal.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.davidbadell.hexagonal.domain.event.DomainEvent;
import com.davidbadell.hexagonal.domain.event.OrderCancelledEvent;
import com.davidbadell.hexagonal.domain.event.OrderCreatedEvent;
import com.davidbadell.hexagonal.domain.exception.DomainException;

/**
 * Order Aggregate Root - Domain Model
 * DDD Pattern: Aggregate Root
 * 
 * This is the main aggregate in our bounded context.
 * All modifications to OrderLines must go through Order.
 */
public class Order {
    
    private final OrderId id;
    private final CustomerId customerId;
    private final List<OrderLine> orderLines;
    private OrderStatus status;
    private Money total;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private final List<DomainEvent> domainEvents;

    public Order(OrderId id, CustomerId customerId) {
        this.id = id;
        this.customerId = customerId;
        this.orderLines = new ArrayList<>();
        this.status = OrderStatus.PENDING;
        this.total = Money.ZERO;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.domainEvents = new ArrayList<>();
        
        // Register creation event
        registerEvent(new OrderCreatedEvent(id, customerId, createdAt));
    }

    /**
     * Add an order line to the order
     * Business Rule: Can only add items to pending orders
     */
    public void addOrderLine(OrderLine orderLine) {
        if (this.status != OrderStatus.PENDING) {
            throw new DomainException("Cannot add items to non-pending orders");
        }
        this.orderLines.add(orderLine);
        recalculateTotal();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Remove an order line from the order
     * Business Rule: Can only remove items from pending orders
     */
    public void removeOrderLine(OrderLine orderLine) {
        if (this.status != OrderStatus.PENDING) {
            throw new DomainException("Cannot remove items from non-pending orders");
        }
        this.orderLines.remove(orderLine);
        recalculateTotal();
        this.updatedAt = LocalDateTime.now();
    }

    private void recalculateTotal() {
        this.total = orderLines.stream()
                .map(OrderLine::getSubtotal)
                .reduce(Money.ZERO, Money::add);
    }

    /**
     * Confirm the order
     * Business Rule: Only pending orders with at least one item can be confirmed
     */
    public void confirm() {
        if (this.status != OrderStatus.PENDING) {
            throw new DomainException("Only pending orders can be confirmed");
        }
        if (this.orderLines.isEmpty()) {
            throw new DomainException("Cannot confirm an empty order");
        }
        this.status = OrderStatus.CONFIRMED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Ship the order
     * Business Rule: Only confirmed orders can be shipped
     */
    public void ship() {
        if (this.status != OrderStatus.CONFIRMED) {
            throw new DomainException("Only confirmed orders can be shipped");
        }
        this.status = OrderStatus.SHIPPED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Mark order as delivered
     * Business Rule: Only shipped orders can be marked as delivered
     */
    public void deliver() {
        if (this.status != OrderStatus.SHIPPED) {
            throw new DomainException("Only shipped orders can be delivered");
        }
        this.status = OrderStatus.DELIVERED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Cancel the order
     * Business Rule: Delivered orders cannot be cancelled
     */
    public void cancel() {
        if (this.status == OrderStatus.DELIVERED) {
            throw new DomainException("Delivered orders cannot be cancelled");
        }
        if (this.status == OrderStatus.CANCELLED) {
            throw new DomainException("Order is already cancelled");
        }
        this.status = OrderStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
        
        // Register cancellation event
        registerEvent(new OrderCancelledEvent(id, LocalDateTime.now()));
    }

    private void registerEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void clearDomainEvents() {
        this.domainEvents.clear();
    }

    // Getters - No setters to maintain encapsulation
    public OrderId getId() { return id; }
    public CustomerId getCustomerId() { return customerId; }
    public List<OrderLine> getOrderLines() { return Collections.unmodifiableList(orderLines); }
    public OrderStatus getStatus() { return status; }
    public Money getTotal() { return total; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return id.equals(order.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
