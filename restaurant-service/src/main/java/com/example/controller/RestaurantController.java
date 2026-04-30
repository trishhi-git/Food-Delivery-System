package com.example.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.entity.Restaurant;
import com.example.repository.RestaurantRepository;

import jakarta.validation.Valid;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {

    @Autowired
    private RestaurantRepository repo;

    private static final Logger log = LoggerFactory.getLogger(RestaurantController.class);

    private static final String SECRET = "mysecretkeymysecretkeymysecretkey";

    // ✅ CREATE RESTAURANT (OWNER ONLY)
    @PostMapping
    public Restaurant addRestaurant(@Valid @RequestBody Restaurant r,
                                    @RequestHeader("Authorization") String authHeader) {

        log.info("Incoming request to create restaurant");

        String token = authHeader.substring(7);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(SECRET.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();

        String role = claims.get("role", String.class);
        Long userId = claims.get("userId", Integer.class).longValue(); // ⚠️ cast fix

        if (!"OWNER".equals(role)) {
            throw new RuntimeException("Only OWNER can create restaurant");
        }

        // ✅ SET OWNER ID FROM TOKEN
        r.setOwnerId(userId);

        Restaurant saved = repo.save(r);

        log.info("Restaurant saved with id={}, ownerId={}", saved.getId(), userId);

        return saved;
    }

    @GetMapping("/{id}")
    public Restaurant getById(@PathVariable("id") Long id) {

        log.info("Fetching restaurant with id={}", id);

        return repo.findById(id).orElseThrow();
    }

    @GetMapping
    public List<Restaurant> getAll() {

        log.info("Fetching all restaurants");

        return repo.findAll();
    }

    @DeleteMapping("/cleanup")
    public String cleanup() {
        log.info("Cleaning up restaurants without email or password");
        repo.deleteByEmailIsNull();
        repo.deleteByPasswordIsNull();
        return "Cleanup successful";
    }
}