package com.davidbadell.hexagonal.infrastructure.adapter.in.cli;

import com.davidbadell.hexagonal.application.dto.CreateOrderCommand;
import com.davidbadell.hexagonal.application.dto.OrderQuery;
import com.davidbadell.hexagonal.application.dto.OrderResponse;
import com.davidbadell.hexagonal.application.port.in.CancelOrderUseCase;
import com.davidbadell.hexagonal.application.port.in.CreateOrderUseCase;
import com.davidbadell.hexagonal.application.port.in.GetOrderUseCase;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.math.BigDecimal;
import java.util.List;

/**
 * CLI Adapter using Spring Shell
 * Hexagonal Architecture: Input Adapter (Driving Adapter)
 * 
 * This adapter exposes the application use cases via command line.
 */
@ShellComponent
public class OrderCLI {
    
    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;

    public OrderCLI(CreateOrderUseCase createOrderUseCase,
                   GetOrderUseCase getOrderUseCase,
                   CancelOrderUseCase cancelOrderUseCase) {
        this.createOrderUseCase = createOrderUseCase;
        this.getOrderUseCase = getOrderUseCase;
        this.cancelOrderUseCase = cancelOrderUseCase;
    }

    /**
     * Create a simple order from CLI
     */
    @ShellMethod(value = "Create a new order", key = "order create")
    public String createOrder(
            @ShellOption(value = "--customer", help = "Customer ID") String customerId,
            @ShellOption(value = "--product", help = "Product ID") String productId,
            @ShellOption(value = "--name", help = "Product name", defaultValue = "Product") String productName,
            @ShellOption(value = "--quantity", help = "Quantity", defaultValue = "1") int quantity,
            @ShellOption(value = "--price", help = "Unit price", defaultValue = "10.00") String price) {
        
        CreateOrderCommand.OrderItemCommand item = new CreateOrderCommand.OrderItemCommand(
                productId, productName, quantity, new BigDecimal(price)
        );
        
        CreateOrderCommand command = new CreateOrderCommand(customerId, List.of(item));
        OrderResponse response = createOrderUseCase.createOrder(command);
        
        return formatOrderResponse(response);
    }

    /**
     * Get order by ID
     */
    @ShellMethod(value = "Get an order by ID", key = "order get")
    public String getOrder(@ShellOption(value = "--id", help = "Order ID") String orderId) {
        return getOrderUseCase.getOrderById(orderId)
                .map(this::formatOrderResponse)
                .orElse("Order not found: " + orderId);
    }

    /**
     * List all orders
     */
    @ShellMethod(value = "List all orders", key = "order list")
    public String listOrders(
            @ShellOption(value = "--customer", help = "Filter by customer ID", defaultValue = "") String customerId,
            @ShellOption(value = "--status", help = "Filter by status", defaultValue = "") String status) {
        
        OrderQuery query = OrderQuery.builder()
                .customerId(customerId.isEmpty() ? null : customerId)
                .status(status.isEmpty() ? null : status)
                .build();
        
        List<OrderResponse> orders = getOrderUseCase.queryOrders(query);
        
        if (orders.isEmpty()) {
            return "No orders found.";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("Orders:\n");
        sb.append("--------\n");
        for (OrderResponse order : orders) {
            sb.append(formatOrderSummary(order)).append("\n");
        }
        return sb.toString();
    }

    /**
     * Cancel an order
     */
    @ShellMethod(value = "Cancel an order", key = "order cancel")
    public String cancelOrder(
            @ShellOption(value = "--id", help = "Order ID") String orderId,
            @ShellOption(value = "--reason", help = "Cancellation reason", defaultValue = "") String reason) {
        
        try {
            OrderResponse response = cancelOrderUseCase.cancelOrder(orderId, reason.isEmpty() ? null : reason);
            return "Order cancelled successfully:\n" + formatOrderResponse(response);
        } catch (Exception e) {
            return "Error cancelling order: " + e.getMessage();
        }
    }

    private String formatOrderResponse(OrderResponse order) {
        StringBuilder sb = new StringBuilder();
        sb.append("┌─────────────────────────────────────────┐\n");
        sb.append("│ Order Details                           │\n");
        sb.append("├─────────────────────────────────────────┤\n");
        sb.append(String.format("│ ID:        %s\n", order.getOrderId()));
        sb.append(String.format("│ Customer:  %s\n", order.getCustomerId()));
        sb.append(String.format("│ Status:    %s\n", order.getStatus()));
        sb.append(String.format("│ Total:     $%s\n", order.getTotal()));
        sb.append(String.format("│ Created:   %s\n", order.getCreatedAt()));
        sb.append("├─────────────────────────────────────────┤\n");
        sb.append("│ Items:                                  │\n");
        for (OrderResponse.OrderLineResponse item : order.getItems()) {
            sb.append(String.format("│   - %s x%d @ $%s = $%s\n",
                    item.getProductName(), item.getQuantity(), 
                    item.getUnitPrice(), item.getSubtotal()));
        }
        sb.append("└─────────────────────────────────────────┘");
        return sb.toString();
    }

    private String formatOrderSummary(OrderResponse order) {
        return String.format("  [%s] %s - Customer: %s - Total: $%s",
                order.getStatus(), order.getOrderId(), 
                order.getCustomerId(), order.getTotal());
    }
}
