package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class FallbackController {

    @GetMapping("/fallback/auth")
    public Mono<String> authFallback() {
        return Mono.just("Authentication Service is taking too long to respond or is down. Please try again later.");
    }

    @GetMapping("/fallback/user")
    public Mono<String> userFallback() {
        return Mono.just("User Service is taking too long to respond or is down. Please try again later.");
    }

    @GetMapping("/fallback/restaurant")
    public Mono<String> restaurantFallback() {
        return Mono.just("Restaurant Service is taking too long to respond or is down. Please try again later.");
    }

    @GetMapping("/fallback/order")
    public Mono<String> orderFallback() {
        return Mono.just("Order Service is taking too long to respond or is down. Please try again later.");
    }
}
