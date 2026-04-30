package com.example.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    @Test
    void testGenerateToken() {
        String token = JwtUtil.generateToken("test@gmail.com", "USER",1L);
        assertNotNull(token);
    }
}