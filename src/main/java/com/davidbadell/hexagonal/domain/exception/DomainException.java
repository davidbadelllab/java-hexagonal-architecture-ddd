package com.davidbadell.hexagonal.domain.exception;

/**
 * Base exception for domain layer errors
 * DDD Pattern: Domain Exception
 * 
 * This exception is thrown when business rules are violated.
 */
public class DomainException extends RuntimeException {
    
    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
