package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.entity.Order;
import com.example.service.OrderService;

import jakarta.validation.Valid;

import com.example.dto.OrderResponse;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService service;

    @PostMapping
    public Order create(@Valid @RequestBody Order order) {
        return service.createOrder(order);
    }

    @PutMapping("/{id}/payment")
    public Order updatePayment(@PathVariable("id") Long id, 
                               @RequestParam String razorpayOrderId, 
                               @RequestParam String status) {
        return service.updatePaymentStatus(id, razorpayOrderId, status);
    }

    @GetMapping
    public List<OrderResponse> getOrders(
            @RequestParam(required = false, name = "restaurantId") Long restaurantId) {

        if (restaurantId != null) {
            return service.getOrdersByRestaurantId(restaurantId);
        }

        return service.getAllOrders();
    }

    // ✅ NEW API (OWNER ONLY via gateway)
    @PutMapping("/{id}")
    public Order updateOrderStatus(@PathVariable("id") Long id, @RequestBody Order request) {
        return service.updateOrderStatus(id, request.getStatus());
    }
}