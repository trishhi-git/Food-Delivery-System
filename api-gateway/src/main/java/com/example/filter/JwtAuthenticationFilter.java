package com.example.filter;

import io.jsonwebtoken.Claims;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.jsonwebtoken.security.Keys;
import java.security.Key;
import io.jsonwebtoken.Jwts;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.cloud.gateway.filter.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter {

    private final String SECRET = "mysecretkeymysecretkeymysecretkey";
    
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        log.info("Incoming request: {}", path);

        // ✅ Allow PUBLIC APIs
        if (path.startsWith("/auth") || path.startsWith("/users")) {
            return chain.filter(exchange);
        }

        // ✅ Check Authorization Header
        if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            throw new RuntimeException("Missing Authorization Header");
        }

        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            String role = claims.get("role", String.class);

        } catch (Exception e) {
            log.error("JWT ERROR: {}", e.getMessage());
            throw new RuntimeException("Invalid Token");
        }

        return chain.filter(exchange);
    }
}