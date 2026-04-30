package com.example.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class RazorpayService {

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    private RazorpayClient client;

    @PostConstruct
    public void init() throws RazorpayException {
        this.client = new RazorpayClient(keyId, keySecret);
    }

    @CircuitBreaker(name = "razorpayService", fallbackMethod = "razorpayFallback")
    public String createOrder(double amount, String receipt) throws RazorpayException {
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", (int)(amount * 100)); // amount in the smallest currency unit (paise)
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", receipt);

        Order order = client.orders.create(orderRequest);
        return order.get("id");
    }

    public String razorpayFallback(double amount, String receipt, Exception t) {
        return "PAYMENT_SERVICE_DOWN";
    }
}
