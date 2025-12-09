package com.davidbadell.hexagonal.application.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Command for creating a new order
 * CQRS Pattern: Command
 * 
 * Commands are used to mutate state in the system.
 */
public class CreateOrderCommand {
    
    private final String customerId;
    private final List<OrderItemCommand> items;

    public CreateOrderCommand(String customerId, List<OrderItemCommand> items) {
        this.customerId = customerId;
        this.items = items;
    }

    public String getCustomerId() {
        return customerId;
    }

    public List<OrderItemCommand> getItems() {
        return items;
    }

    /**
     * Nested class for order item data
     */
    public static class OrderItemCommand {
        private final String productId;
        private final String productName;
        private final int quantity;
        private final BigDecimal price;

        public OrderItemCommand(String productId, String productName, int quantity, BigDecimal price) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.price = price;
        }

        public String getProductId() { return productId; }
        public String getProductName() { return productName; }
        public int getQuantity() { return quantity; }
        public BigDecimal getPrice() { return price; }
    }

    @Override
    public String toString() {
        return String.format("CreateOrderCommand{customerId='%s', items=%d}", customerId, items.size());
    }
}
