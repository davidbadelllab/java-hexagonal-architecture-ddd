package com.davidbadell.hexagonal.application.service;

import com.davidbadell.hexagonal.application.dto.OrderResponse;
import com.davidbadell.hexagonal.application.port.in.CancelOrderUseCase;
import com.davidbadell.hexagonal.application.port.out.EventPublisher;
import com.davidbadell.hexagonal.application.port.out.OrderRepository;
import com.davidbadell.hexagonal.domain.event.DomainEvent;
import com.davidbadell.hexagonal.domain.exception.OrderNotFoundException;
import com.davidbadell.hexagonal.domain.model.Order;
import com.davidbadell.hexagonal.domain.model.OrderId;

/**
 * Application Service: Cancel Order
 * Hexagonal Architecture: Use Case Implementation
 * 
 * This service handles order cancellation use case.
 */
public class CancelOrderService implements CancelOrderUseCase {
    
    private final OrderRepository orderRepository;
    private final EventPublisher eventPublisher;

    public CancelOrderService(OrderRepository orderRepository, EventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public OrderResponse cancelOrder(String orderId) {
        return cancelOrder(orderId, null);
    }

    @Override
    public OrderResponse cancelOrder(String orderId, String reason) {
        OrderId orderIdObj = OrderId.of(orderId);
        
        // Find the order
        Order order = orderRepository.findById(orderIdObj)
                .orElseThrow(() -> new OrderNotFoundException(orderIdObj));

        // Cancel the order (domain logic)
        order.cancel();

        // Save the order
        Order savedOrder = orderRepository.save(order);

        // Publish domain events
        for (DomainEvent event : savedOrder.getDomainEvents()) {
            eventPublisher.publish(event);
        }
        savedOrder.clearDomainEvents();

        return OrderResponse.fromDomain(savedOrder);
    }
}
