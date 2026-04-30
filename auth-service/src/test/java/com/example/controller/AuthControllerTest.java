package com.example.controller;

import com.example.entity.User;
import com.example.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    // ✅ 1. SUCCESS LOGIN
    @Test
    void testLoginSuccess() throws Exception {

        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword("123456");

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        String json = """
        {
          "email": "test@gmail.com",
          "password": "123456"
        }
        """;

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }

    // ❌ 2. USER NOT FOUND
    @Test
    void testUserNotFound() throws Exception {

        when(userRepository.findByEmail("wrong@gmail.com"))
                .thenReturn(Optional.empty());

        String json = """
        {
          "email": "wrong@gmail.com",
          "password": "123456"
        }
        """;

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }

    // ❌ 3. INVALID PASSWORD
    @Test
    void testInvalidPassword() throws Exception {

        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword("correct");

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        String json = """
        {
          "email": "test@gmail.com",
          "password": "wrong"
        }
        """;

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }

    // ❌ 4. VALIDATION FAIL
    @Test
    void testValidationFail() throws Exception {

        String json = """
        {
          "email": "abc",
          "password": ""
        }
        """;

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }
}