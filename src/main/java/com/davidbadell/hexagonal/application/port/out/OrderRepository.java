package com.davidbadell.hexagonal.application.port.out;

import com.davidbadell.hexagonal.domain.model.CustomerId;
import com.davidbadell.hexagonal.domain.model.Order;
import com.davidbadell.hexagonal.domain.model.OrderId;

import java.util.List;
import java.util.Optional;

/**
 * Output Port: Order Repository
 * Hexagonal Architecture: Output Port (Driven Port)
 * 
 * This interface defines the contract for order persistence.
 * The implementation will be in the infrastructure layer.
 */
public interface OrderRepository {
    
    /**
     * Save an order
     * 
     * @param order The order to save
     * @return The saved order
     */
    Order save(Order order);
    
    /**
     * Find an order by its ID
     * 
     * @param orderId The order ID
     * @return Optional containing the order if found
     */
    Optional<Order> findById(OrderId orderId);
    
    /**
     * Find all orders for a customer
     * 
     * @param customerId The customer ID
     * @return List of orders
     */
    List<Order> findByCustomerId(CustomerId customerId);
    
    /**
     * Find all orders
     * 
     * @return List of all orders
     */
    List<Order> findAll();
    
    /**
     * Delete an order
     * 
     * @param orderId The order ID to delete
     */
    void deleteById(OrderId orderId);
    
    /**
     * Check if an order exists
     * 
     * @param orderId The order ID
     * @return true if the order exists
     */
    boolean existsById(OrderId orderId);
}
