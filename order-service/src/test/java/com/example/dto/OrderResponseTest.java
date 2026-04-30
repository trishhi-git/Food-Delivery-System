package com.example.dto;

import com.example.status.OrderStatus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OrderResponseTest {

    @Test
    void testGettersSetters() {

        OrderResponse res = new OrderResponse();
        res.setStatus("PLACED");

        assertEquals(OrderStatus.PLACED, res.getStatus());
    }
}
