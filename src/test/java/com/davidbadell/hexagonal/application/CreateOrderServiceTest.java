package com.davidbadell.hexagonal.application;

import com.davidbadell.hexagonal.application.dto.CreateOrderCommand;
import com.davidbadell.hexagonal.application.dto.OrderResponse;
import com.davidbadell.hexagonal.application.port.out.EventPublisher;
import com.davidbadell.hexagonal.application.port.out.OrderRepository;
import com.davidbadell.hexagonal.application.service.CreateOrderService;
import com.davidbadell.hexagonal.domain.event.DomainEvent;
import com.davidbadell.hexagonal.domain.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit Tests for CreateOrderService
 * 
 * Tests use case implementation with mocked ports.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Create Order Service Tests")
class CreateOrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private EventPublisher eventPublisher;

    private CreateOrderService createOrderService;

    @BeforeEach
    void setUp() {
        createOrderService = new CreateOrderService(orderRepository, eventPublisher);
    }

    @Test
    @DisplayName("Should create order successfully")
    void shouldCreateOrderSuccessfully() {
        // Given
        CreateOrderCommand command = createSampleCommand();
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        OrderResponse response = createOrderService.createOrder(command);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCustomerId()).isEqualTo("customer-123");
        assertThat(response.getStatus()).isEqualTo("PENDING");
        assertThat(response.getItems()).hasSize(1);
    }

    @Test
    @DisplayName("Should save order via repository")
    void shouldSaveOrderViaRepository() {
        // Given
        CreateOrderCommand command = createSampleCommand();
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        createOrderService.createOrder(command);

        // Then
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, times(1)).save(orderCaptor.capture());
        
        Order savedOrder = orderCaptor.getValue();
        assertThat(savedOrder.getCustomerId().getValue()).isEqualTo("customer-123");
        assertThat(savedOrder.getOrderLines()).hasSize(1);
    }

    @Test
    @DisplayName("Should publish domain events")
    void shouldPublishDomainEvents() {
        // Given
        CreateOrderCommand command = createSampleCommand();
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        createOrderService.createOrder(command);

        // Then
        verify(eventPublisher, atLeastOnce()).publish(any(DomainEvent.class));
    }

    @Test
    @DisplayName("Should calculate order total correctly")
    void shouldCalculateOrderTotalCorrectly() {
        // Given
        CreateOrderCommand command = new CreateOrderCommand(
                "customer-123",
                List.of(
                        new CreateOrderCommand.OrderItemCommand("prod-1", "Product 1", 2, BigDecimal.valueOf(10.00)),
                        new CreateOrderCommand.OrderItemCommand("prod-2", "Product 2", 3, BigDecimal.valueOf(5.00))
                )
        );
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        OrderResponse response = createOrderService.createOrder(command);

        // Then
        assertThat(response.getTotal()).isEqualByComparingTo(BigDecimal.valueOf(35.00));
    }

    // Helper method
    private CreateOrderCommand createSampleCommand() {
        return new CreateOrderCommand(
                "customer-123",
                List.of(
                        new CreateOrderCommand.OrderItemCommand(
                                "product-1",
                                "Test Product",
                                2,
                                BigDecimal.valueOf(29.99)
                        )
                )
        );
    }
}
