package com.example.controller;

import com.example.entity.Order;
import com.example.service.OrderService;
import com.example.dto.OrderResponse;
import com.example.status.OrderStatus;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService service;

    // ✅ 1. CREATE ORDER SUCCESS
    @Test
    void testCreateOrder() throws Exception {

        Order order = new Order();
        order.setId(1L);
        order.setUserId(1L);
        order.setTotalAmount(200.0);
        order.setStatus(OrderStatus.PLACED);

        when(service.createOrder(org.mockito.ArgumentMatchers.any(Order.class)))
                .thenReturn(order);

        String json = """
        {
          "userId": 1,
          "totalAmount": 200,
          "status": "PLACED"
        }
        """;

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }

    // ❌ 2. VALIDATION FAIL
    @Test
    void testValidationFail() throws Exception {

        String json = """
        {
          "userId": null,
          "totalAmount": -100,
          "status": ""
        }
        """;

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    // ✅ 3. GET ORDERS
    @Test
    void testGetOrders() throws Exception {

        OrderResponse res = new OrderResponse();
        res.setStatus("PLACED");

        when(service.getAllOrders()).thenReturn(Arrays.asList(res));

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk());
    }
}
