package com.davidbadell.hexagonal.application.port.in;

import com.davidbadell.hexagonal.application.dto.OrderQuery;
import com.davidbadell.hexagonal.application.dto.OrderResponse;

import java.util.List;
import java.util.Optional;

/**
 * Input Port: Get Order Use Case
 * Hexagonal Architecture: Input Port (Driving Port)
 * 
 * This interface defines the contract for querying orders.
 * Following CQRS pattern - this is the Query side.
 */
public interface GetOrderUseCase {
    
    /**
     * Get an order by its ID
     * 
     * @param orderId The order ID
     * @return Optional containing the order if found
     */
    Optional<OrderResponse> getOrderById(String orderId);
    
    /**
     * Get orders by customer ID
     * 
     * @param customerId The customer ID
     * @return List of orders for the customer
     */
    List<OrderResponse> getOrdersByCustomerId(String customerId);
    
    /**
     * Query orders based on criteria
     * 
     * @param query The query parameters
     * @return List of matching orders
     */
    List<OrderResponse> queryOrders(OrderQuery query);
}
