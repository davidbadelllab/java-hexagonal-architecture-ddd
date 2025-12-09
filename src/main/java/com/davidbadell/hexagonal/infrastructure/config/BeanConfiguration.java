package com.davidbadell.hexagonal.infrastructure.config;

import com.davidbadell.hexagonal.application.port.in.CancelOrderUseCase;
import com.davidbadell.hexagonal.application.port.in.CreateOrderUseCase;
import com.davidbadell.hexagonal.application.port.in.GetOrderUseCase;
import com.davidbadell.hexagonal.application.port.out.EventPublisher;
import com.davidbadell.hexagonal.application.port.out.OrderRepository;
import com.davidbadell.hexagonal.application.service.CancelOrderService;
import com.davidbadell.hexagonal.application.service.CreateOrderService;
import com.davidbadell.hexagonal.application.service.GetOrderService;
import com.davidbadell.hexagonal.domain.service.PricingService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Bean Configuration
 * Infrastructure Layer: Configuration
 * 
 * This class configures the dependency injection for the application.
 * It wires together the use cases with their dependencies (ports).
 */
@Configuration
public class BeanConfiguration {
    
    /**
     * Create Order Use Case
     */
    @Bean
    public CreateOrderUseCase createOrderUseCase(OrderRepository orderRepository, 
                                                  EventPublisher eventPublisher) {
        return new CreateOrderService(orderRepository, eventPublisher);
    }
    
    /**
     * Get Order Use Case
     */
    @Bean
    public GetOrderUseCase getOrderUseCase(OrderRepository orderRepository) {
        return new GetOrderService(orderRepository);
    }
    
    /**
     * Cancel Order Use Case
     */
    @Bean
    public CancelOrderUseCase cancelOrderUseCase(OrderRepository orderRepository,
                                                  EventPublisher eventPublisher) {
        return new CancelOrderService(orderRepository, eventPublisher);
    }
    
    /**
     * Domain Service: Pricing Service
     */
    @Bean
    public PricingService pricingService() {
        return new PricingService();
    }
}
