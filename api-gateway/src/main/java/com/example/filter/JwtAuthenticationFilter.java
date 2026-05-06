package com.example.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import io.jsonwebtoken.Jwts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.cloud.gateway.filter.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * JWT authentication filter applied globally to the API Gateway.
 * - Public paths (/auth/**, /users/**) bypass token validation.
 * - All other requests must carry a valid Bearer token.
 * - Secret is now injected from application.yml (no hardcoded value).
 */
@Component
public class JwtAuthenticationFilter implements GlobalFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Value("${app.jwt.secret}")
    private String secret;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        log.info("Incoming request: {}", path);

        // ✅ Allow PUBLIC APIs — no token required
        if (path.startsWith("/auth") || path.startsWith("/users")) {
            return chain.filter(exchange);
        }

        // ✅ Check Authorization header exists
        if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            log.warn("Missing Authorization header for path: {}", path);
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            Key key = Keys.hmacShaKeyFor(secret.getBytes());

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String role = claims.get("role", String.class);
            log.info("Token validated. Role: {}", role);

        } catch (Exception e) {
            log.error("JWT validation failed: {}", e.getMessage());
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        return chain.filter(exchange);
    }
}