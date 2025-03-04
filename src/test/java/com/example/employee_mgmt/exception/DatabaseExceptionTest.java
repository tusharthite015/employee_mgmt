package com.example.employee_mgmt.exception;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class DatabaseExceptionTest {
    @Test
    void testDatabaseExceptionWithMessage() {
        DatabaseException exception = new DatabaseException("Database error occurred");
        assertEquals("Database error occurred", exception.getMessage());
    }
    
    @Test
    void testDatabaseExceptionWithMessageAndCause() {
        Throwable cause = new RuntimeException("SQL Error");
        DatabaseException exception = new DatabaseException("Database operation failed", cause);
        assertEquals("Database operation failed", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
} 