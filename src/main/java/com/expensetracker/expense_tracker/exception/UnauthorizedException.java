package com.expensetracker.expense_tracker.exception;

/**
 * Exception thrown when a user attempts to access a resource they don't own.
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
