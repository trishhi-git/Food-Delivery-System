package com.example.controller;

import com.example.entity.User;
import com.example.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    // ✅ 1. CREATE USER SUCCESS
    @Test
    void testCreateUserSuccess() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setName("Test");
        user.setEmail("test@gmail.com");
        user.setPassword("123456");

        when(userRepository.save(org.mockito.ArgumentMatchers.any(User.class)))
                .thenReturn(user);

        String json = """
        {
          "name": "Test",
          "email": "test@gmail.com",
          "password": "123456"
        }
        """;

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }

    // ❌ 2. VALIDATION FAIL
    @Test
    void testValidationFail() throws Exception {

        String json = """
        {
          "name": "",
          "email": "abc",
          "password": "123"
        }
        """;

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    // ✅ 3. GET USERS
    @Test
    void testGetUsers() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setName("Test");
        user.setEmail("test@gmail.com");
        user.setPassword("123456");

        when(userRepository.findAll()).thenReturn(Arrays.asList(user));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
    }
}
