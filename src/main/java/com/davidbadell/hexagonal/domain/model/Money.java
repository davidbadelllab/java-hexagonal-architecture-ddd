package com.davidbadell.hexagonal.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

/**
 * Value Object representing monetary values
 * DDD Pattern: Value Object
 * 
 * Characteristics:
 * - Immutable
 * - Handles currency properly
 * - Provides monetary operations
 */
public class Money {
    
    public static final Money ZERO = new Money(BigDecimal.ZERO);
    
    private final BigDecimal amount;
    private final Currency currency;

    public Money(BigDecimal amount) {
        this(amount, Currency.getInstance("USD"));
    }

    public Money(BigDecimal amount, Currency currency) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (currency == null) {
            throw new IllegalArgumentException("Currency cannot be null");
        }
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
        this.currency = currency;
    }

    public static Money of(double amount) {
        return new Money(BigDecimal.valueOf(amount));
    }

    public static Money of(BigDecimal amount) {
        return new Money(amount);
    }

    public static Money of(String amount) {
        return new Money(new BigDecimal(amount));
    }

    /**
     * Add two monetary values
     */
    public Money add(Money other) {
        validateSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    /**
     * Subtract monetary value
     */
    public Money subtract(Money other) {
        validateSameCurrency(other);
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    /**
     * Multiply by a factor (e.g., quantity)
     */
    public Money multiply(int factor) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(factor)), this.currency);
    }

    /**
     * Multiply by a decimal factor
     */
    public Money multiply(BigDecimal factor) {
        return new Money(this.amount.multiply(factor), this.currency);
    }

    /**
     * Check if this amount is greater than other
     */
    public boolean isGreaterThan(Money other) {
        validateSameCurrency(other);
        return this.amount.compareTo(other.amount) > 0;
    }

    /**
     * Check if this amount is less than other
     */
    public boolean isLessThan(Money other) {
        validateSameCurrency(other);
        return this.amount.compareTo(other.amount) < 0;
    }

    /**
     * Check if amount is positive
     */
    public boolean isPositive() {
        return this.amount.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Check if amount is negative
     */
    public boolean isNegative() {
        return this.amount.compareTo(BigDecimal.ZERO) < 0;
    }

    /**
     * Check if amount is zero
     */
    public boolean isZero() {
        return this.amount.compareTo(BigDecimal.ZERO) == 0;
    }

    private void validateSameCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                String.format("Cannot operate on different currencies: %s vs %s", 
                    this.currency.getCurrencyCode(), other.currency.getCurrencyCode()));
        }
    }

    // Getters
    public BigDecimal getAmount() { return amount; }
    public Currency getCurrency() { return currency; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return Objects.equals(amount, money.amount) && Objects.equals(currency, money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return String.format("%s %s", currency.getSymbol(), amount.toPlainString());
    }
}
