package com.davidbadell.hexagonal.infrastructure.adapter.in.rest;

import com.davidbadell.hexagonal.application.dto.CreateOrderCommand;
import com.davidbadell.hexagonal.application.dto.OrderQuery;
import com.davidbadell.hexagonal.application.dto.OrderResponse;
import com.davidbadell.hexagonal.application.port.in.CancelOrderUseCase;
import com.davidbadell.hexagonal.application.port.in.CreateOrderUseCase;
import com.davidbadell.hexagonal.application.port.in.GetOrderUseCase;
import com.davidbadell.hexagonal.domain.exception.DomainException;
import com.davidbadell.hexagonal.domain.exception.OrderNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST Controller Adapter
 * Hexagonal Architecture: Input Adapter (Driving Adapter)
 * 
 * This adapter exposes the application use cases via REST API.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;

    public OrderController(CreateOrderUseCase createOrderUseCase,
                          GetOrderUseCase getOrderUseCase,
                          CancelOrderUseCase cancelOrderUseCase) {
        this.createOrderUseCase = createOrderUseCase;
        this.getOrderUseCase = getOrderUseCase;
        this.cancelOrderUseCase = cancelOrderUseCase;
    }

    /**
     * Create a new order
     * POST /api/orders
     */
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        CreateOrderCommand command = mapToCommand(request);
        OrderResponse response = createOrderUseCase.createOrder(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get order by ID
     * GET /api/orders/{orderId}
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable String orderId) {
        return getOrderUseCase.getOrderById(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get orders by customer ID
     * GET /api/orders/customer/{customerId}
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByCustomerId(@PathVariable String customerId) {
        List<OrderResponse> orders = getOrderUseCase.getOrdersByCustomerId(customerId);
        return ResponseEntity.ok(orders);
    }

    /**
     * Query orders with filters
     * GET /api/orders
     */
    @GetMapping
    public ResponseEntity<List<OrderResponse>> queryOrders(
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        OrderQuery query = OrderQuery.builder()
                .customerId(customerId)
                .status(status)
                .page(page)
                .size(size)
                .build();
        
        List<OrderResponse> orders = getOrderUseCase.queryOrders(query);
        return ResponseEntity.ok(orders);
    }

    /**
     * Cancel an order
     * POST /api/orders/{orderId}/cancel
     */
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable String orderId,
            @RequestBody(required = false) CancelOrderRequest request) {
        
        String reason = request != null ? request.getReason() : null;
        OrderResponse response = cancelOrderUseCase.cancelOrder(orderId, reason);
        return ResponseEntity.ok(response);
    }

    /**
     * Exception handlers
     */
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFound(OrderNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("ORDER_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(DomainException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("DOMAIN_ERROR", ex.getMessage()));
    }

    // Request/Response DTOs for REST layer
    private CreateOrderCommand mapToCommand(CreateOrderRequest request) {
        List<CreateOrderCommand.OrderItemCommand> items = request.getItems().stream()
                .map(item -> new CreateOrderCommand.OrderItemCommand(
                        item.getProductId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getPrice()
                ))
                .toList();
        
        return new CreateOrderCommand(request.getCustomerId(), items);
    }

    // Inner classes for REST DTOs
    public static class CreateOrderRequest {
        private String customerId;
        private List<OrderItemRequest> items;

        public String getCustomerId() { return customerId; }
        public void setCustomerId(String customerId) { this.customerId = customerId; }
        public List<OrderItemRequest> getItems() { return items; }
        public void setItems(List<OrderItemRequest> items) { this.items = items; }
    }

    public static class OrderItemRequest {
        private String productId;
        private String productName;
        private int quantity;
        private BigDecimal price;

        public String getProductId() { return productId; }
        public void setProductId(String productId) { this.productId = productId; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
    }

    public static class CancelOrderRequest {
        private String reason;

        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }

    public static class ErrorResponse {
        private final String code;
        private final String message;

        public ErrorResponse(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public String getCode() { return code; }
        public String getMessage() { return message; }
    }
}
