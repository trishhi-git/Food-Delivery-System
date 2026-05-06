package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.entity.Order;
import com.example.service.OrderService;
import com.example.dto.OrderCreateResponse;
import com.example.dto.OrderResponse;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService service;

    // ✅ Create order — returns Razorpay data for frontend checkout modal
    @PostMapping
    public OrderCreateResponse create(@Valid @RequestBody Order order) {
        return service.createOrder(order);
    }

    // ✅ Update payment status after Razorpay confirms payment
    @PutMapping("/{id}/payment")
    public Order updatePayment(@PathVariable("id") Long id,
                               @RequestParam("razorpayOrderId") String razorpayOrderId,
                               @RequestParam("status") String status) {
        return service.updatePaymentStatus(id, razorpayOrderId, status);
    }

    // ✅ Get orders — filter by restaurantId if provided
    @GetMapping
    public List<OrderResponse> getOrders(
            @RequestParam(required = false, name = "restaurantId") Long restaurantId) {

        if (restaurantId != null) {
            return service.getOrdersByRestaurantId(restaurantId);
        }

        return service.getAllOrders();
    }

    // ✅ Update order status (OWNER only via gateway)
    @PutMapping("/{id}")
    public Order updateOrderStatus(@PathVariable("id") Long id, @RequestBody Order request) {
        return service.updateOrderStatus(id, request.getStatus());
    }

    // ✅ Cancel an order
    @PutMapping("/{id}/cancel")
    public Order cancelOrder(@PathVariable("id") Long id) {
        return service.cancelOrder(id);
    }

    // ✅ Rate an order
    @PutMapping("/{id}/rate")
    public Order rateOrder(@PathVariable("id") Long id, @RequestParam("rating") Integer rating) {
        return service.rateOrder(id, rating);
    }
}