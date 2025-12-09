package com.davidbadell.hexagonal.application.dto;

import com.davidbadell.hexagonal.domain.model.Order;
import com.davidbadell.hexagonal.domain.model.OrderLine;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Response DTO for Order data
 * Used to transfer order data between layers
 */
public class OrderResponse {
    
    private final String orderId;
    private final String customerId;
    private final String status;
    private final BigDecimal total;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final List<OrderLineResponse> items;

    private OrderResponse(Builder builder) {
        this.orderId = builder.orderId;
        this.customerId = builder.customerId;
        this.status = builder.status;
        this.total = builder.total;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.items = builder.items;
    }

    /**
     * Factory method to create OrderResponse from Order domain object
     */
    public static OrderResponse fromDomain(Order order) {
        List<OrderLineResponse> items = order.getOrderLines().stream()
                .map(OrderLineResponse::fromDomain)
                .collect(Collectors.toList());

        return builder()
                .orderId(order.getId().getValue())
                .customerId(order.getCustomerId().getValue())
                .status(order.getStatus().name())
                .total(order.getTotal().getAmount())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(items)
                .build();
    }

    // Getters
    public String getOrderId() { return orderId; }
    public String getCustomerId() { return customerId; }
    public String getStatus() { return status; }
    public BigDecimal getTotal() { return total; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public List<OrderLineResponse> getItems() { return items; }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Nested class for order line response
     */
    public static class OrderLineResponse {
        private final String productId;
        private final String productName;
        private final int quantity;
        private final BigDecimal unitPrice;
        private final BigDecimal subtotal;

        public OrderLineResponse(String productId, String productName, int quantity, 
                                 BigDecimal unitPrice, BigDecimal subtotal) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.subtotal = subtotal;
        }

        public static OrderLineResponse fromDomain(OrderLine orderLine) {
            return new OrderLineResponse(
                    orderLine.getProductId().getValue(),
                    orderLine.getProductName(),
                    orderLine.getQuantity().getValue(),
                    orderLine.getUnitPrice().getAmount(),
                    orderLine.getSubtotal().getAmount()
            );
        }

        public String getProductId() { return productId; }
        public String getProductName() { return productName; }
        public int getQuantity() { return quantity; }
        public BigDecimal getUnitPrice() { return unitPrice; }
        public BigDecimal getSubtotal() { return subtotal; }
    }

    /**
     * Builder for OrderResponse
     */
    public static class Builder {
        private String orderId;
        private String customerId;
        private String status;
        private BigDecimal total;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<OrderLineResponse> items;

        public Builder orderId(String orderId) { this.orderId = orderId; return this; }
        public Builder customerId(String customerId) { this.customerId = customerId; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder total(BigDecimal total) { this.total = total; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder items(List<OrderLineResponse> items) { this.items = items; return this; }

        public OrderResponse build() {
            return new OrderResponse(this);
        }
    }
}
