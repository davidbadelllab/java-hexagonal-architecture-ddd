package com.davidbadell.hexagonal.infrastructure.adapter.out.persistence;

import com.davidbadell.hexagonal.application.port.out.OrderRepository;
import com.davidbadell.hexagonal.domain.model.*;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repository Adapter
 * Hexagonal Architecture: Output Adapter (Driven Adapter)
 * 
 * This adapter implements the OrderRepository port using JPA.
 */
@Component
public class OrderRepositoryAdapter implements OrderRepository {
    
    private final OrderJpaRepository jpaRepository;

    public OrderRepositoryAdapter(OrderJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Order save(Order order) {
        OrderEntity entity = toEntity(order);
        OrderEntity savedEntity = jpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Order> findById(OrderId orderId) {
        return jpaRepository.findById(orderId.getValue())
                .map(this::toDomain);
    }

    @Override
    public List<Order> findByCustomerId(CustomerId customerId) {
        return jpaRepository.findByCustomerId(customerId.getValue())
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(OrderId orderId) {
        jpaRepository.deleteById(orderId.getValue());
    }

    @Override
    public boolean existsById(OrderId orderId) {
        return jpaRepository.existsById(orderId.getValue());
    }

    // Mapping methods
    private OrderEntity toEntity(Order order) {
        OrderEntity entity = new OrderEntity(
                order.getId().getValue(),
                order.getCustomerId().getValue(),
                OrderStatusEntity.valueOf(order.getStatus().name()),
                order.getTotal().getAmount(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
        
        for (OrderLine line : order.getOrderLines()) {
            OrderLineEntity lineEntity = new OrderLineEntity(
                    line.getProductId().getValue(),
                    line.getProductName(),
                    line.getQuantity().getValue(),
                    line.getUnitPrice().getAmount(),
                    line.getSubtotal().getAmount()
            );
            entity.addOrderLine(lineEntity);
        }
        
        return entity;
    }

    private Order toDomain(OrderEntity entity) {
        Order order = new Order(
                OrderId.of(entity.getId()),
                CustomerId.of(entity.getCustomerId())
        );
        
        // Add order lines
        for (OrderLineEntity lineEntity : entity.getOrderLines()) {
            OrderLine line = new OrderLine(
                    ProductId.of(lineEntity.getProductId()),
                    lineEntity.getProductName(),
                    Quantity.of(lineEntity.getQuantity()),
                    Money.of(lineEntity.getUnitPrice())
            );
            order.addOrderLine(line);
        }
        
        // Set status based on persisted status
        switch (entity.getStatus()) {
            case CONFIRMED -> order.confirm();
            case SHIPPED -> { order.confirm(); order.ship(); }
            case DELIVERED -> { order.confirm(); order.ship(); order.deliver(); }
            case CANCELLED -> order.cancel();
            default -> {} // PENDING is the default
        }
        
        // Clear events generated during reconstruction
        order.clearDomainEvents();
        
        return order;
    }
}
