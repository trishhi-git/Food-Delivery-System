package com.example.entity;

import org.junit.jupiter.api.Test;

import com.example.status.OrderStatus;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void testGettersSetters() {

        Order order = new Order();

        order.setUserId(1L);
        order.setTotalAmount(200.0);
        order.setStatus(OrderStatus.PLACED);

        assertEquals(1L, order.getUserId());
        assertEquals(200.0, order.getTotalAmount());
        assertEquals("PLACED", order.getStatus());
    }
}