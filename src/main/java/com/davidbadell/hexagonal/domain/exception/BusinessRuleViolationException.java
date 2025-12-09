package com.davidbadell.hexagonal.domain.exception;

/**
 * Exception thrown when a business rule is violated
 */
public class BusinessRuleViolationException extends DomainException {
    
    private final String ruleName;

    public BusinessRuleViolationException(String ruleName, String message) {
        super(String.format("Business rule '%s' violated: %s", ruleName, message));
        this.ruleName = ruleName;
    }

    public String getRuleName() {
        return ruleName;
    }
}
