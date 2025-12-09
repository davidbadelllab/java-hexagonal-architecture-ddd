package com.davidbadell.hexagonal.domain.service;

import com.davidbadell.hexagonal.domain.model.Money;
import com.davidbadell.hexagonal.domain.model.Order;
import com.davidbadell.hexagonal.domain.model.OrderLine;

import java.math.BigDecimal;

/**
 * Domain Service for pricing calculations
 * DDD Pattern: Domain Service
 * 
 * Used when business logic doesn't naturally belong to a single entity.
 * This service handles complex pricing rules like discounts, taxes, etc.
 */
public class PricingService {
    
    private static final BigDecimal TAX_RATE = new BigDecimal("0.21"); // 21% VAT
    private static final BigDecimal BULK_DISCOUNT_THRESHOLD = new BigDecimal("100.00");
    private static final BigDecimal BULK_DISCOUNT_RATE = new BigDecimal("0.10"); // 10% discount

    /**
     * Calculate the final price including taxes
     */
    public Money calculateFinalPrice(Order order) {
        Money subtotal = order.getTotal();
        Money discount = calculateDiscount(order);
        Money afterDiscount = subtotal.subtract(discount);
        Money tax = calculateTax(afterDiscount);
        
        return afterDiscount.add(tax);
    }

    /**
     * Calculate discount based on order total
     * Business Rule: Orders over $100 get 10% discount
     */
    public Money calculateDiscount(Order order) {
        Money subtotal = order.getTotal();
        
        if (subtotal.getAmount().compareTo(BULK_DISCOUNT_THRESHOLD) > 0) {
            return subtotal.multiply(BULK_DISCOUNT_RATE);
        }
        
        return Money.ZERO;
    }

    /**
     * Calculate tax based on subtotal
     */
    public Money calculateTax(Money amount) {
        return amount.multiply(TAX_RATE);
    }

    /**
     * Validate if order meets minimum order requirements
     */
    public boolean meetsMinimumOrderRequirements(Order order) {
        // Business Rule: Minimum order amount is $10
        Money minimumAmount = Money.of(10.00);
        return order.getTotal().isGreaterThan(minimumAmount) || 
               order.getTotal().equals(minimumAmount);
    }

    /**
     * Calculate shipping cost based on order total
     */
    public Money calculateShippingCost(Order order) {
        Money freeShippingThreshold = Money.of(50.00);
        Money standardShipping = Money.of(5.99);
        
        if (order.getTotal().isGreaterThan(freeShippingThreshold)) {
            return Money.ZERO;
        }
        
        return standardShipping;
    }
}
