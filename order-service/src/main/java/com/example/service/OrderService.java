package com.example.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.entity.Order;
import com.example.repository.OrderRepository;
import com.example.status.OrderStatus;
import com.example.feign.RestaurantClient;
import com.example.dto.*;

import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository repo;

    @Autowired
    private RestaurantClient restaurantClient;

    @Autowired
    private RazorpayService razorpayService;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    /**
     * Creates an order and a Razorpay payment order.
     * Returns an OrderCreateResponse so the frontend can open the Razorpay modal.
     */
    public OrderCreateResponse createOrder(Order order) {

        log.info("Received order request: userId={}, restaurantId={}, amount={}",
                order.getUserId(), order.getRestaurantId(), order.getTotalAmount());

        // Feign call to validate restaurant exists
        log.info("Calling restaurant-service for restaurantId={}", order.getRestaurantId());
        Restaurant restaurant = restaurantClient.getRestaurantById(order.getRestaurantId());
        log.info("Restaurant fetched: {}", restaurant.getName());

        if (order.getStatus() == null) {
            order.setStatus(OrderStatus.PENDING);
        }

        // ✅ INTEGRATE RAZORPAY
        try {
            String razorpayOrderId = razorpayService.createOrder(
                    order.getTotalAmount(), "order_rcptid_" + System.currentTimeMillis());
            order.setRazorpayOrderId(razorpayOrderId);
            order.setPaymentStatus("CREATED");
            log.info("Razorpay order created: {}", razorpayOrderId);
        } catch (Exception e) {
            log.error("Failed to create Razorpay order", e);
            order.setPaymentStatus("FAILED");
        }

        Order saved = repo.save(order);
        log.info("Order created successfully with id={}", saved.getId());

        // ✅ Build response for frontend (includes razorpayKeyId for checkout modal)
        OrderCreateResponse response = new OrderCreateResponse();
        response.setOrderId(saved.getId());
        response.setRazorpayOrderId(saved.getRazorpayOrderId());
        response.setRazorpayKeyId(razorpayKeyId);
        response.setTotalAmount(saved.getTotalAmount());
        response.setStatus(saved.getStatus().name());
        response.setPaymentStatus(saved.getPaymentStatus());

        return response;
    }

    public Order updatePaymentStatus(Long id, String razorpayOrderId, String paymentStatus) {
        log.info("Updating payment status: orderId={}, razorpayOrderId={}, status={}", id, razorpayOrderId, paymentStatus);
        Order order = repo.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        order.setPaymentStatus(paymentStatus);
        if ("SUCCESS".equalsIgnoreCase(paymentStatus)) {
            order.setStatus(OrderStatus.PLACED);
        } else if ("FAILED".equalsIgnoreCase(paymentStatus) || "CANCELLED_BY_USER".equalsIgnoreCase(paymentStatus)) {
            order.setStatus(OrderStatus.CANCELLED);
        }
        return repo.save(order);
    }

    public List<OrderResponse> getAllOrders() {

        log.info("Fetching all orders");

        List<OrderResponse> list = repo.findAll().stream().map(order -> {

            Restaurant restaurant = null;
            try {
                if (order.getRestaurantId() != null) {
                    restaurant = restaurantClient.getRestaurantById(order.getRestaurantId());
                }
            } catch (Exception e) {
                log.warn("Could not fetch restaurant for id: {}", order.getRestaurantId());
            }

            OrderResponse response = new OrderResponse();
            response.setOrderId(order.getId());
            response.setRestaurantName(restaurant != null ? restaurant.getName() : "Unknown Restaurant");
            response.setTotalAmount(order.getTotalAmount());
            response.setStatus(order.getStatus() != null ? order.getStatus().name() : "UNKNOWN");
            response.setUserId(order.getUserId());
            response.setItems(order.getItems());
            response.setDeliveryAddress(order.getDeliveryAddress());
            response.setRating(order.getRating());

            return response;

        }).toList();

        log.info("Total orders fetched: {}", list.size());

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

                    Restaurant restaurant = null;
                    try {
                        if (order.getRestaurantId() != null) {
                            restaurant = restaurantClient.getRestaurantById(order.getRestaurantId());
                        }
                    } catch (Exception e) {
                        log.warn("Could not fetch restaurant for id: {}", order.getRestaurantId());
                    }

                    OrderResponse response = new OrderResponse();
                    response.setOrderId(order.getId());
                    response.setRestaurantName(restaurant != null ? restaurant.getName() : "Unknown Restaurant");
                    response.setTotalAmount(order.getTotalAmount());
                    response.setStatus(order.getStatus() != null ? order.getStatus().name() : "UNKNOWN");
                    response.setUserId(order.getUserId());
                    response.setItems(order.getItems());
                    response.setDeliveryAddress(order.getDeliveryAddress());
                    response.setRating(order.getRating());

                    return response;
                }).toList();

        return list;
    }

    public Order cancelOrder(Long id) {
        log.info("Cancelling order id={}", id);
        Order order = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.PLACED) {
            throw new RuntimeException("Cannot cancel order at this stage. Food is already preparing or delivered.");
        }

        order.setStatus(OrderStatus.CANCELLED);

        if ("SUCCESS".equalsIgnoreCase(order.getPaymentStatus())) {
            order.setPaymentStatus("REFUNDED");
        }

        return repo.save(order);
    }

    public Order rateOrder(Long id, Integer rating) {
        log.info("Rating order id={}, rating={}", id, rating);
        Order order = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new RuntimeException("Cannot rate an order that is not delivered.");
        }

        if (rating < 1 || rating > 5) {
            throw new RuntimeException("Rating must be between 1 and 5.");
        }

        order.setRating(rating);
        return repo.save(order);
    }
}