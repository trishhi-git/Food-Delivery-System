package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // ✅ Get orders by restaurant
    List<Order> findByRestaurantId(Long restaurantId);

    // 🔥 (Optional - future use)
    List<Order> findByUserId(Long userId);
}
