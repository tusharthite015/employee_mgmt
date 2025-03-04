package com.example.employee_mgmt.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

 class ServiceExceptionTest {
    @Test
    void testServiceExceptionWithMessage() {
        String message = "Service error";
        ServiceException exception = new ServiceException(message);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testServiceExceptionWithMessageAndCause() {
        String message = "Service error";
        RuntimeException cause = new RuntimeException("Cause");
        ServiceException exception = new ServiceException(message, cause);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
    
}
