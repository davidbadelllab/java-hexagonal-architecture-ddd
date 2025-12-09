package com.davidbadell.hexagonal.domain;

import com.davidbadell.hexagonal.domain.exception.DomainException;
import com.davidbadell.hexagonal.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit Tests for Order Aggregate
 * 
 * Tests domain logic without any infrastructure dependencies.
 */
@DisplayName("Order Aggregate Tests")
class OrderTest {

    private Order order;
    private OrderId orderId;
    private CustomerId customerId;

    @BeforeEach
    void setUp() {
        orderId = OrderId.generate();
        customerId = CustomerId.of("customer-123");
        order = new Order(orderId, customerId);
    }

    @Nested
    @DisplayName("Order Creation")
    class OrderCreation {

        @Test
        @DisplayName("Should create order with PENDING status")
        void shouldCreateOrderWithPendingStatus() {
            assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        }

        @Test
        @DisplayName("Should create order with zero total")
        void shouldCreateOrderWithZeroTotal() {
            assertThat(order.getTotal()).isEqualTo(Money.ZERO);
        }

        @Test
        @DisplayName("Should create order with empty order lines")
        void shouldCreateOrderWithEmptyOrderLines() {
            assertThat(order.getOrderLines()).isEmpty();
        }

        @Test
        @DisplayName("Should register OrderCreatedEvent on creation")
        void shouldRegisterOrderCreatedEvent() {
            assertThat(order.getDomainEvents()).hasSize(1);
            assertThat(order.getDomainEvents().get(0).getEventType()).isEqualTo("OrderCreated");
        }
    }

    @Nested
    @DisplayName("Adding Order Lines")
    class AddingOrderLines {

        @Test
        @DisplayName("Should calculate total when adding item")
        void whenAddingItem_thenTotalIsCalculated() {
            // Given
            OrderLine item = new OrderLine(
                    ProductId.of("prod-1"),
                    "Product 1",
                    Quantity.of(2),
                    Money.of(BigDecimal.valueOf(29.99))
            );

            // When
            order.addOrderLine(item);

            // Then
            assertThat(order.getTotal().getAmount())
                    .isEqualByComparingTo(BigDecimal.valueOf(59.98));
        }

        @Test
        @DisplayName("Should add multiple items and calculate correct total")
        void shouldAddMultipleItemsAndCalculateCorrectTotal() {
            // Given
            OrderLine item1 = new OrderLine(
                    ProductId.of("prod-1"),
                    "Product 1",
                    Quantity.of(2),
                    Money.of(BigDecimal.valueOf(10.00))
            );
            OrderLine item2 = new OrderLine(
                    ProductId.of("prod-2"),
                    "Product 2",
                    Quantity.of(3),
                    Money.of(BigDecimal.valueOf(5.00))
            );

            // When
            order.addOrderLine(item1);
            order.addOrderLine(item2);

            // Then
            assertThat(order.getTotal().getAmount())
                    .isEqualByComparingTo(BigDecimal.valueOf(35.00));
            assertThat(order.getOrderLines()).hasSize(2);
        }

        @Test
        @DisplayName("Should not allow adding items to non-pending orders")
        void shouldNotAllowAddingItemsToNonPendingOrders() {
            // Given
            addItemToOrder();
            order.confirm();

            // When/Then
            OrderLine newItem = new OrderLine(
                    ProductId.of("prod-2"),
                    "Product 2",
                    Quantity.of(1),
                    Money.of(BigDecimal.valueOf(10.00))
            );

            assertThatThrownBy(() -> order.addOrderLine(newItem))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("non-pending");
        }
    }

    @Nested
    @DisplayName("Order Status Transitions")
    class OrderStatusTransitions {

        @Test
        @DisplayName("Should confirm pending order")
        void shouldConfirmPendingOrder() {
            // Given
            addItemToOrder();

            // When
            order.confirm();

            // Then
            assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        }

        @Test
        @DisplayName("Should not confirm empty order")
        void shouldNotConfirmEmptyOrder() {
            assertThatThrownBy(() -> order.confirm())
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("empty");
        }

        @Test
        @DisplayName("Should not confirm already confirmed order")
        void shouldNotConfirmAlreadyConfirmedOrder() {
            // Given
            addItemToOrder();
            order.confirm();

            // When/Then
            assertThatThrownBy(() -> order.confirm())
                    .isInstanceOf(DomainException.class);
        }

        @Test
        @DisplayName("Should ship confirmed order")
        void shouldShipConfirmedOrder() {
            // Given
            addItemToOrder();
            order.confirm();

            // When
            order.ship();

            // Then
            assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPED);
        }

        @Test
        @DisplayName("Should not ship non-confirmed order")
        void shouldNotShipNonConfirmedOrder() {
            // Given
            addItemToOrder();

            // When/Then
            assertThatThrownBy(() -> order.ship())
                    .isInstanceOf(DomainException.class);
        }

        @Test
        @DisplayName("Should deliver shipped order")
        void shouldDeliverShippedOrder() {
            // Given
            addItemToOrder();
            order.confirm();
            order.ship();

            // When
            order.deliver();

            // Then
            assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        }
    }

    @Nested
    @DisplayName("Order Cancellation")
    class OrderCancellation {

        @Test
        @DisplayName("Should cancel pending order")
        void shouldCancelPendingOrder() {
            // When
            order.cancel();

            // Then
            assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        }

        @Test
        @DisplayName("Should cancel confirmed order")
        void shouldCancelConfirmedOrder() {
            // Given
            addItemToOrder();
            order.confirm();

            // When
            order.cancel();

            // Then
            assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        }

        @Test
        @DisplayName("Should not cancel delivered order")
        void shouldNotCancelDeliveredOrder() {
            // Given
            addItemToOrder();
            order.confirm();
            order.ship();
            order.deliver();

            // When/Then
            assertThatThrownBy(() -> order.cancel())
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("Delivered");
        }

        @Test
        @DisplayName("Should register OrderCancelledEvent on cancellation")
        void shouldRegisterOrderCancelledEvent() {
            // Given
            order.clearDomainEvents(); // Clear creation event

            // When
            order.cancel();

            // Then
            assertThat(order.getDomainEvents()).hasSize(1);
            assertThat(order.getDomainEvents().get(0).getEventType()).isEqualTo("OrderCancelled");
        }
    }

    // Helper method
    private void addItemToOrder() {
        OrderLine item = new OrderLine(
                ProductId.of("prod-1"),
                "Test Product",
                Quantity.of(1),
                Money.of(BigDecimal.valueOf(10.00))
        );
        order.addOrderLine(item);
    }
}
