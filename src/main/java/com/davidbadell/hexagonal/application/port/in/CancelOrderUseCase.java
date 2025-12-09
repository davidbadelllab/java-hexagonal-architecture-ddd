package com.davidbadell.hexagonal.application.port.in;

import com.davidbadell.hexagonal.application.dto.OrderResponse;

/**
 * Input Port: Cancel Order Use Case
 * Hexagonal Architecture: Input Port (Driving Port)
 * 
 * This interface defines the contract for cancelling orders.
 */
public interface CancelOrderUseCase {
    
    /**
     * Cancel an order by its ID
     * 
     * @param orderId The order ID to cancel
     * @return The updated order response
     */
    OrderResponse cancelOrder(String orderId);
    
    /**
     * Cancel an order with a reason
     * 
     * @param orderId The order ID to cancel
     * @param reason The cancellation reason
     * @return The updated order response
     */
    OrderResponse cancelOrder(String orderId, String reason);
}
