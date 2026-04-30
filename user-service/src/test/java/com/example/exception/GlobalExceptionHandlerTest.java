package com.example.exception;

import org.junit.jupiter.api.Test;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    @Test
    void testRuntimeException() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        RuntimeException ex = new RuntimeException("Test error");

        Map<String, String> result = handler.handleRuntimeException(ex);

        assertEquals("Test error", result.get("error"));
    }
}
