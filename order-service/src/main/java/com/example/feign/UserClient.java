package com.example.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.dto.User;

@FeignClient(name = "USER-SERVICE")
public interface UserClient {

    @GetMapping("/users/{id}")
    User getUserById(@PathVariable("id") Long id);
}
