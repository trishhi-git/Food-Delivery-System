package com.example.service;

import com.example.entity.Order;
import com.example.repository.OrderRepository;
import com.example.feign.RestaurantClient;
import com.example.dto.Restaurant;
import com.example.dto.OrderResponse;
import com.example.status.OrderStatus;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.dto.OrderCreateResponse;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository repo;

    @Mock
    private RestaurantClient restaurantClient;

    @Mock
    private RazorpayService razorpayService;

    @InjectMocks
    private OrderService service;

    // ✅ TEST CREATE ORDER
    @Test
    void testCreateOrder() throws Exception {

        Order order = new Order();
        order.setUserId(1L);
        order.setRestaurantId(1L);
        order.setTotalAmount(200.0);
        order.setStatus(OrderStatus.PLACED);

        Restaurant restaurant = new Restaurant();
        restaurant.setName("Dominos");

        when(restaurantClient.getRestaurantById(1L)).thenReturn(restaurant);
        when(razorpayService.createOrder(anyDouble(), anyString())).thenReturn("order_abc123");
        when(repo.save(order)).thenReturn(order);

        OrderCreateResponse result = service.createOrder(order);

        assertEquals("PLACED", result.getStatus());
        assertEquals("order_abc123", result.getRazorpayOrderId());
        verify(repo, times(1)).save(order);
    }

    // ✅ TEST GET ORDERS
    @Test
    void testGetOrders() {

        Order order = new Order();
        order.setId(1L);
        order.setRestaurantId(1L);
        order.setTotalAmount(200.0);
        order.setStatus(OrderStatus.PLACED);

        Restaurant restaurant = new Restaurant();
        restaurant.setName("Dominos");

        when(repo.findAll()).thenReturn(List.of(order));
        when(restaurantClient.getRestaurantById(1L)).thenReturn(restaurant);

        List<OrderResponse> list = service.getAllOrders();

        assertNotNull(list);
        assertEquals("Dominos", list.get(0).getRestaurantName());
    }
    
    //create order null status case
    @Test
    void testCreateOrder_StatusNull() throws Exception {

        Order order = new Order();
        order.setUserId(1L);
        order.setRestaurantId(1L);
        order.setTotalAmount(200.0);
        order.setStatus(null); // IMPORTANT

        Restaurant restaurant = new Restaurant();
        restaurant.setName("Dominos");

        when(restaurantClient.getRestaurantById(1L)).thenReturn(restaurant);
        when(razorpayService.createOrder(anyDouble(), anyString())).thenReturn("order_xyz789");
        when(repo.save(order)).thenReturn(order);

        OrderCreateResponse result = service.createOrder(order);

        assertEquals("PLACED", result.getStatus());
        assertEquals("order_xyz789", result.getRazorpayOrderId());
    }
    
    
    //update order status success
    @Test
    void testUpdateOrderStatus() {

        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PLACED);

        when(repo.findById(1L)).thenReturn(java.util.Optional.of(order));
        when(repo.save(order)).thenReturn(order);

        Order updated = service.updateOrderStatus(1L, OrderStatus.DELIVERED);

        assertEquals(OrderStatus.DELIVERED, updated.getStatus());
    }
    
    
    //update order status not found 
    @Test
    void testUpdateOrderStatus_NotFound() {

        when(repo.findById(1L)).thenReturn(java.util.Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            service.updateOrderStatus(1L, OrderStatus.DELIVERED);
        });
    }
    
    
    
    //get order by restaurant
    @Test
    void testGetOrdersByRestaurantId() {

        Order order = new Order();
        order.setId(1L);
        order.setRestaurantId(1L);
        order.setTotalAmount(200.0);
        order.setStatus(OrderStatus.PLACED);

        Restaurant restaurant = new Restaurant();
        restaurant.setName("Dominos");

        when(repo.findByRestaurantId(1L)).thenReturn(List.of(order));
        when(restaurantClient.getRestaurantById(1L)).thenReturn(restaurant);

        List<OrderResponse> list = service.getOrdersByRestaurantId(1L);

        assertEquals(1, list.size());
        assertEquals("Dominos", list.get(0).getRestaurantName());
    }
}
