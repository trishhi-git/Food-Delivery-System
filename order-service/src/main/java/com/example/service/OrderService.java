package com.example.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Order;
import com.example.repository.OrderRepository;
import com.example.status.OrderStatus;
import com.example.feign.RestaurantClient;
import com.example.dto.*;

import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrderService {

    @Autowired
    private OrderRepository repo;

    @Autowired
    private RestaurantClient restaurantClient;

    @Autowired
    private RazorpayService razorpayService;
    
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    public Order createOrder(Order order) {

        log.info("Received order request: userId={}, restaurantId={}, amount={}",
                order.getUserId(), order.getRestaurantId(), order.getTotalAmount());

        // Feign call
        log.info("Calling restaurant-service for restaurantId={}", order.getRestaurantId());
        Restaurant restaurant = restaurantClient.getRestaurantById(order.getRestaurantId());

        log.info("Restaurant fetched: {}", restaurant.getName());

        if (order.getStatus() == null) {
            order.setStatus(OrderStatus.PLACED);
        }

        // ✅ INTEGRATE RAZORPAY
        try {
            String razorpayOrderId = razorpayService.createOrder(order.getTotalAmount(), "order_rcptid_" + System.currentTimeMillis());
            order.setRazorpayOrderId(razorpayOrderId);
            order.setPaymentStatus("CREATED");
            log.info("Razorpay order created: {}", razorpayOrderId);
        } catch (Exception e) {
            log.error("Failed to create Razorpay order", e);
            order.setPaymentStatus("FAILED");
        }

        Order saved = repo.save(order);

        log.info("Order created successfully with id={}", saved.getId());

        return saved;
    }

    public Order updatePaymentStatus(Long id, String razorpayOrderId, String paymentStatus) {
        log.info("Updating payment status: orderId={}, razorpayOrderId={}, status={}", id, razorpayOrderId, paymentStatus);
        Order order = repo.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        order.setPaymentStatus(paymentStatus);
        if ("SUCCESS".equalsIgnoreCase(paymentStatus)) {
            order.setStatus(OrderStatus.PLACED); // Or PAID
        }
        return repo.save(order);
    }

    public List<OrderResponse> getAllOrders() {

        log.info("Fetching all orders");

        List<OrderResponse> list = repo.findAll().stream().map(order -> {

            Restaurant restaurant =
                restaurantClient.getRestaurantById(order.getRestaurantId());

            OrderResponse response = new OrderResponse();
            response.setOrderId(order.getId());
            response.setRestaurantName(restaurant.getName());
            response.setTotalAmount(order.getTotalAmount());
            response.setStatus(order.getStatus().name());
            response.setUserId(order.getUserId());
            response.setItems(order.getItems());

            return response;

        }).toList();

        log.info("Total orders fetched: {}", list.size());
       
        log.info(" TEST LOG WORKING");

        return list;
    }
    
    public Order updateOrderStatus(Long id, OrderStatus status) {

        log.info("Updating order status: id={}, status={}", id, status);

        Order order = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(status);

        Order updated = repo.save(order);

        log.info("Order updated successfully: id={}", updated.getId());

        return updated;
    }
    
    public List<OrderResponse> getOrdersByRestaurantId(Long restaurantId) {

        log.info("Fetching orders for restaurantId={}", restaurantId);

        List<OrderResponse> list = repo.findByRestaurantId(restaurantId)
                .stream()
                .map(order -> {

                    Restaurant restaurant =
                            restaurantClient.getRestaurantById(order.getRestaurantId());

                    OrderResponse response = new OrderResponse();
                    response.setOrderId(order.getId());
                    response.setRestaurantName(restaurant.getName());
                    response.setTotalAmount(order.getTotalAmount());
                    response.setStatus(order.getStatus().name());
                    response.setUserId(order.getUserId());
                    response.setItems(order.getItems());

                    return response;
                }).toList();

        return list;
    }
}