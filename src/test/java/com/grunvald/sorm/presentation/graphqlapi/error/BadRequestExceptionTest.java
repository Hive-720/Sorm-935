package com.grunvald.sorm.presentation.graphqlapi.error;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BadRequestExceptionTest {

    @Test
    void isRuntimeException() {
        assertTrue(RuntimeException.class.isAssignableFrom(BadRequestException.class));
    }

    @Test
    void constructor_storesMessage() {
        BadRequestException exception = new BadRequestException("Invalid page size");
        assertEquals("Invalid page size", exception.getMessage());
    }

    @Test
    void constructor_acceptsNullMessage() {
        BadRequestException exception = new BadRequestException(null);
        assertNull(exception.getMessage());
    }

    @Test
    void constructor_acceptsCauseThroughRuntimeExceptionApi() {
        IllegalStateException cause = new IllegalStateException("root cause");
        BadRequestException exception = new BadRequestException("wrapped");
        exception.initCause(cause);

        assertEquals(cause, exception.getCause());
    }
}
