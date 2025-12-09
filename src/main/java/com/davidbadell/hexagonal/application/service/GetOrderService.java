package com.davidbadell.hexagonal.application.service;

import com.davidbadell.hexagonal.application.dto.OrderQuery;
import com.davidbadell.hexagonal.application.dto.OrderResponse;
import com.davidbadell.hexagonal.application.port.in.GetOrderUseCase;
import com.davidbadell.hexagonal.application.port.out.OrderRepository;
import com.davidbadell.hexagonal.domain.model.CustomerId;
import com.davidbadell.hexagonal.domain.model.Order;
import com.davidbadell.hexagonal.domain.model.OrderId;
import com.davidbadell.hexagonal.domain.model.OrderStatus;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Application Service: Get Order
 * Hexagonal Architecture: Use Case Implementation (Query Side - CQRS)
 * 
 * This service handles all order query operations.
 */
public class GetOrderService implements GetOrderUseCase {
    
    private final OrderRepository orderRepository;

    public GetOrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Optional<OrderResponse> getOrderById(String orderId) {
        return orderRepository.findById(OrderId.of(orderId))
                .map(OrderResponse::fromDomain);
    }

    @Override
    public List<OrderResponse> getOrdersByCustomerId(String customerId) {
        return orderRepository.findByCustomerId(CustomerId.of(customerId))
                .stream()
                .map(OrderResponse::fromDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> queryOrders(OrderQuery query) {
        List<Order> orders = orderRepository.findAll();
        
        // Apply filters
        return orders.stream()
                .filter(order -> filterByCustomerId(order, query.getCustomerId()))
                .filter(order -> filterByStatus(order, query.getStatus()))
                .filter(order -> filterByDateRange(order, query))
                .skip((long) query.getPage() * query.getSize())
                .limit(query.getSize())
                .map(OrderResponse::fromDomain)
                .collect(Collectors.toList());
    }

    private boolean filterByCustomerId(Order order, String customerId) {
        if (customerId == null || customerId.isBlank()) {
            return true;
        }
        return order.getCustomerId().getValue().equals(customerId);
    }

    private boolean filterByStatus(Order order, String status) {
        if (status == null || status.isBlank()) {
            return true;
        }
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            return order.getStatus() == orderStatus;
        } catch (IllegalArgumentException e) {
            return true;
        }
    }

    private boolean filterByDateRange(Order order, OrderQuery query) {
        if (query.getFromDate() != null && order.getCreatedAt().isBefore(query.getFromDate())) {
            return false;
        }
        if (query.getToDate() != null && order.getCreatedAt().isAfter(query.getToDate())) {
            return false;
        }
        return true;
    }
}
