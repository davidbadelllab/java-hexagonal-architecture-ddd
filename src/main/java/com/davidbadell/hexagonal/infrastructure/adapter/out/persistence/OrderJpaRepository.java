package com.davidbadell.hexagonal.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA Repository for Order
 * Infrastructure Layer: Repository Interface
 */
@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, String> {
    
    /**
     * Find orders by customer ID
     */
    List<OrderEntity> findByCustomerId(String customerId);
    
    /**
     * Find orders by status
     */
    List<OrderEntity> findByStatus(OrderStatusEntity status);
    
    /**
     * Find orders by customer ID and status
     */
    List<OrderEntity> findByCustomerIdAndStatus(String customerId, OrderStatusEntity status);
}
