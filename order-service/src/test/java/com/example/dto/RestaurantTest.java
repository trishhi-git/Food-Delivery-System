package com.example.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RestaurantTest {

    @Test
    void testGettersSetters() {

        Restaurant r = new Restaurant();
        r.setName("Dominos");

        assertEquals("Dominos", r.getName());
    }
}
