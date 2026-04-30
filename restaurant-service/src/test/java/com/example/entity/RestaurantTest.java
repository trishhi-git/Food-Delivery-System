package com.example.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RestaurantTest {

    @Test
    void testGettersSetters() {

        Restaurant r = new Restaurant();

        r.setId(1L);
        r.setName("Dominos");
        r.setLocation("Bangalore");

        assertEquals(1L, r.getId());
        assertEquals("Dominos", r.getName());
        assertEquals("Bangalore", r.getLocation());
    }
}
