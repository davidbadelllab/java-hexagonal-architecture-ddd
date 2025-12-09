package com.davidbadell.hexagonal.application.dto;

import java.time.LocalDateTime;

/**
 * Query for searching orders
 * CQRS Pattern: Query
 * 
 * Queries are used to retrieve data without modifying state.
 */
public class OrderQuery {
    
    private final String customerId;
    private final String status;
    private final LocalDateTime fromDate;
    private final LocalDateTime toDate;
    private final int page;
    private final int size;

    private OrderQuery(Builder builder) {
        this.customerId = builder.customerId;
        this.status = builder.status;
        this.fromDate = builder.fromDate;
        this.toDate = builder.toDate;
        this.page = builder.page;
        this.size = builder.size;
    }

    public String getCustomerId() { return customerId; }
    public String getStatus() { return status; }
    public LocalDateTime getFromDate() { return fromDate; }
    public LocalDateTime getToDate() { return toDate; }
    public int getPage() { return page; }
    public int getSize() { return size; }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder pattern for OrderQuery
     */
    public static class Builder {
        private String customerId;
        private String status;
        private LocalDateTime fromDate;
        private LocalDateTime toDate;
        private int page = 0;
        private int size = 20;

        public Builder customerId(String customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder fromDate(LocalDateTime fromDate) {
            this.fromDate = fromDate;
            return this;
        }

        public Builder toDate(LocalDateTime toDate) {
            this.toDate = toDate;
            return this;
        }

        public Builder page(int page) {
            this.page = page;
            return this;
        }

        public Builder size(int size) {
            this.size = size;
            return this;
        }

        public OrderQuery build() {
            return new OrderQuery(this);
        }
    }
}
