package com.example.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testGettersSetters() {

        User u = new User();
        u.setEmail("test@gmail.com");

        assertEquals("test@gmail.com", u.getEmail());
    }
}