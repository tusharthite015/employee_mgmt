package com.example.employee_mgmt.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class ValidationExceptionTest {
    @Test
    void shouldCreateExceptionWithMessage() {
        String errorMessage = "Validation failed";
        ValidationException exception = new ValidationException(errorMessage);
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void shouldCreateExceptionWithMessageAndCause() {
        String errorMessage = "Validation failed";
        IllegalArgumentException cause = new IllegalArgumentException("Invalid input");
        ValidationException exception = new ValidationException(errorMessage, cause);
        assertEquals(errorMessage, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
} 