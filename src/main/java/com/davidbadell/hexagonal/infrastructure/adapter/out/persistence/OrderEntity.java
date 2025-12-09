package com.davidbadell.hexagonal.infrastructure.adapter.out.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA Entity for Order persistence
 * Infrastructure Layer: Persistence Entity
 * 
 * This entity is used for ORM mapping and should not be used in domain logic.
 */
@Entity
@Table(name = "orders")
public class OrderEntity {
    
    @Id
    @Column(name = "id", nullable = false)
    private String id;
    
    @Column(name = "customer_id", nullable = false)
    private String customerId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatusEntity status;
    
    @Column(name = "total", nullable = false, precision = 19, scale = 2)
    private BigDecimal total;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLineEntity> orderLines = new ArrayList<>();

    // Default constructor for JPA
    protected OrderEntity() {}

    public OrderEntity(String id, String customerId, OrderStatusEntity status, 
                      BigDecimal total, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.customerId = customerId;
        this.status = status;
        this.total = total;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void addOrderLine(OrderLineEntity orderLine) {
        orderLines.add(orderLine);
        orderLine.setOrder(this);
    }

    public void removeOrderLine(OrderLineEntity orderLine) {
        orderLines.remove(orderLine);
        orderLine.setOrder(null);
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    
    public OrderStatusEntity getStatus() { return status; }
    public void setStatus(OrderStatusEntity status) { this.status = status; }
    
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public List<OrderLineEntity> getOrderLines() { return orderLines; }
    public void setOrderLines(List<OrderLineEntity> orderLines) { this.orderLines = orderLines; }
}

/**
 * Enum for Order Status in persistence layer
 */
enum OrderStatusEntity {
    PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
}
