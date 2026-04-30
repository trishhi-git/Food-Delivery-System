package com.example.controller;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    @Test
    void testGettersSetters() {
        LoginRequest req = new LoginRequest();
        req.setEmail("test@gmail.com");
        req.setPassword("123456");

        assertEquals("test@gmail.com", req.getEmail());
        assertEquals("123456", req.getPassword());
    }
}