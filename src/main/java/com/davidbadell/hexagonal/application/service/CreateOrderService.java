package com.davidbadell.hexagonal.application.service;

import com.davidbadell.hexagonal.application.dto.CreateOrderCommand;
import com.davidbadell.hexagonal.application.dto.OrderResponse;
import com.davidbadell.hexagonal.application.port.in.CreateOrderUseCase;
import com.davidbadell.hexagonal.application.port.out.EventPublisher;
import com.davidbadell.hexagonal.application.port.out.OrderRepository;
import com.davidbadell.hexagonal.domain.event.DomainEvent;
import com.davidbadell.hexagonal.domain.model.*;

/**
 * Application Service: Create Order
 * Hexagonal Architecture: Use Case Implementation
 * 
 * This service orchestrates the order creation use case.
 * It uses ports to interact with external systems.
 */
public class CreateOrderService implements CreateOrderUseCase {
    
    private final OrderRepository orderRepository;
    private final EventPublisher eventPublisher;

    public CreateOrderService(OrderRepository orderRepository, EventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public OrderResponse createOrder(CreateOrderCommand command) {
        // Create the order aggregate
        Order order = new Order(
                OrderId.generate(),
                CustomerId.of(command.getCustomerId())
        );

        // Add order lines
        for (CreateOrderCommand.OrderItemCommand item : command.getItems()) {
            OrderLine orderLine = new OrderLine(
                    ProductId.of(item.getProductId()),
                    item.getProductName(),
                    Quantity.of(item.getQuantity()),
                    Money.of(item.getPrice())
            );
            order.addOrderLine(orderLine);
        }

        // Persist the order
        Order savedOrder = orderRepository.save(order);

        // Publish domain events
        for (DomainEvent event : savedOrder.getDomainEvents()) {
            eventPublisher.publish(event);
        }
        savedOrder.clearDomainEvents();

        // Return response
        return OrderResponse.fromDomain(savedOrder);
    }
}
