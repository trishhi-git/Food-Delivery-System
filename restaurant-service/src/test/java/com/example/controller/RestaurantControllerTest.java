package com.example.controller;

import com.example.entity.Restaurant;
import com.example.repository.RestaurantRepository;
import com.example.exception.GlobalExceptionHandler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.List;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RestaurantController.class)
@Import(GlobalExceptionHandler.class)
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestaurantRepository repo;

    private static final String SECRET = "mysecretkeymysecretkeymysecretkey";

    // ✅ Helper method to generate JWT
    private String generateToken(String role, int userId) {
        return "Bearer " + Jwts.builder()
                .claim("role", role)
                .claim("userId", userId)
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()))
                .compact();
    }

    // ✅ CREATE SUCCESS (OWNER)
    @Test
    void testCreateRestaurant() throws Exception {

        Restaurant r = new Restaurant();
        r.setId(1L);
        r.setName("Dominos");
        r.setLocation("Bangalore");

        when(repo.save(org.mockito.ArgumentMatchers.any(Restaurant.class)))
                .thenReturn(r);

        String token = generateToken("OWNER", 101);

        String json = """
        {
          "name": "Dominos",
          "location": "Bangalore"
        }
        """;

        mockMvc.perform(post("/restaurants")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }

    // ❌ VALIDATION FAIL
    @Test
    void testValidationFail() throws Exception {

        String token = generateToken("OWNER", 101);

        String json = """
        {
          "name": "",
          "location": ""
        }
        """;

        mockMvc.perform(post("/restaurants")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    // ❌ NOT OWNER (should fail)
    @Test
    void testCreateRestaurant_NotOwner() throws Exception {

        String token = generateToken("CUSTOMER", 101);

        String json = """
        {
          "name": "Dominos",
          "location": "Bangalore"
        }
        """;

        mockMvc.perform(post("/restaurants")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isInternalServerError());
    }

    // ✅ GET BY ID SUCCESS
    @Test
    void testGetById() throws Exception {

        Restaurant r = new Restaurant();
        r.setId(1L);
        r.setName("Dominos");
        r.setLocation("Bangalore");

        when(repo.findById(1L)).thenReturn(Optional.of(r));

        mockMvc.perform(get("/restaurants/1"))
                .andExpect(status().isOk());
    }

    // ❌ GET BY ID NOT FOUND
    @Test
    void testGetById_NotFound() throws Exception {

        when(repo.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/restaurants/1"))
                .andExpect(status().isInternalServerError());
    }

    // ✅ GET ALL
    @Test
    void testGetAll() throws Exception {

        Restaurant r = new Restaurant();
        r.setId(1L);
        r.setName("Dominos");
        r.setLocation("Bangalore");

        when(repo.findAll()).thenReturn(List.of(r));

        mockMvc.perform(get("/restaurants"))
                .andExpect(status().isOk());
    }
}