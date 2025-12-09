package com.davidbadell.hexagonal.infrastructure.adapter.out.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * JPA Entity for OrderLine persistence
 */
@Entity
@Table(name = "order_lines")
public class OrderLineEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;
    
    @Column(name = "product_id", nullable = false)
    private String productId;
    
    @Column(name = "product_name")
    private String productName;
    
    @Column(name = "quantity", nullable = false)
    private int quantity;
    
    @Column(name = "unit_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal unitPrice;
    
    @Column(name = "subtotal", nullable = false, precision = 19, scale = 2)
    private BigDecimal subtotal;

    // Default constructor for JPA
    protected OrderLineEntity() {}

    public OrderLineEntity(String productId, String productName, int quantity, 
                          BigDecimal unitPrice, BigDecimal subtotal) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = subtotal;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public OrderEntity getOrder() { return order; }
    public void setOrder(OrderEntity order) { this.order = order; }
    
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}
