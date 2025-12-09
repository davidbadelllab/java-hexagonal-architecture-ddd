package com.davidbadell.hexagonal.application.port.in;

import com.davidbadell.hexagonal.application.dto.CreateOrderCommand;
import com.davidbadell.hexagonal.application.dto.OrderResponse;

/**
 * Input Port: Create Order Use Case
 * Hexagonal Architecture: Input Port (Driving Port)
 * 
 * This interface defines the contract for creating orders.
 * It will be implemented by the application service and
 * used by adapters (REST, CLI, etc.)
 */
public interface CreateOrderUseCase {
    
    /**
     * Create a new order
     * 
     * @param command The command containing order creation data
     * @return The created order response
     */
    OrderResponse createOrder(CreateOrderCommand command);
}
