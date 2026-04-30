package com.example.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.dto.Restaurant;

@FeignClient(name = "RESTAURANT-SERVICE")
public interface RestaurantClient {

    @GetMapping("/restaurants/{id}")
    Restaurant getRestaurantById(@PathVariable("id") Long id);
}