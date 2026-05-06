package com.example.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    // Use the same secret format as application.properties
    private static final String TEST_SECRET = "FoodHubSecretKey2024SuperSecureLongKeyForHS256";

    @BeforeEach
    void setUp() {
        // Instantiate JwtUtil directly — no Spring context needed for unit tests
        jwtUtil = new JwtUtil(TEST_SECRET);
    }

    @Test
    void testGenerateToken_notNull() {
        String token = jwtUtil.generateToken("test@gmail.com", "USER", 1L);
        assertNotNull(token, "Generated token should not be null");
    }

    @Test
    void testGenerateToken_hasThreeParts() {
        String token = jwtUtil.generateToken("test@gmail.com", "USER", 1L);
        // A valid JWT always has exactly 3 dot-separated parts
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT should have header.payload.signature format");
    }

    @Test
    void testGenerateToken_differentUsersGetDifferentTokens() {
        String token1 = jwtUtil.generateToken("user1@gmail.com", "USER", 1L);
        String token2 = jwtUtil.generateToken("user2@gmail.com", "OWNER", 2L);
        assertNotEquals(token1, token2, "Different users should get different tokens");
    }
}