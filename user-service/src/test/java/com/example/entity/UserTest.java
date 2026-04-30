package com.example.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testGettersSetters() {

        User user = new User();

        user.setId(1L);
        user.setName("Test");
        user.setEmail("test@gmail.com");
        user.setPassword("123456");

        assertEquals(1L, user.getId());
        assertEquals("Test", user.getName());
        assertEquals("test@gmail.com", user.getEmail());
        assertEquals("123456", user.getPassword());
    }
}