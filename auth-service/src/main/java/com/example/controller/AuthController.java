package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import com.example.entity.User;
import com.example.repository.UserRepository;
import com.example.util.JwtUtil;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {
	
	private static final Logger log = LoggerFactory.getLogger(AuthController.class);

	@Autowired
	private UserRepository userRepository;

	@PostMapping("/login")
    public String login(@Valid @RequestBody LoginRequest request) {

        log.info("Login attempt for email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = JwtUtil.generateToken(user.getEmail(), user.getRole(),user.getId());

        log.info("Token generated for user: {}", user.getEmail());

        return token;
    }
	
	
}