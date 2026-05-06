package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.entity.User;
import com.example.repository.UserRepository;

import jakarta.validation.Valid;

/**
 * UserController — handles registration and user listing.
 * Passwords are now BCrypt-hashed before saving to the database.
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository repo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // ✅ REGISTER USER
    @PostMapping
    public ResponseEntity<?> register(@Valid @RequestBody User user) {

        log.info("Registering user: {}", user.getEmail());

        // Check for duplicate email
        if (repo.findByEmail(user.getEmail()).isPresent()) {
            log.warn("User already exists: {}", user.getEmail());
            return ResponseEntity.badRequest().body("User already exists");
        }

        if (user.getRole() == null) {
            user.setRole("USER"); // default role
        }

        // ✅ Hash password with BCrypt before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = repo.save(user);

        log.info("User registered successfully: {}", savedUser.getEmail());

        return ResponseEntity.ok(savedUser);
    }

    // ✅ GET ALL USERS
    @GetMapping
    public List<User> getAllUsers() {

        log.info("Fetching all users");

        List<User> list = repo.findAll();

        log.info("Total users: {}", list.size());

        return list;
    }
}
