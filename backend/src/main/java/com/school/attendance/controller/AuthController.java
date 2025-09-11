package com.school.attendance.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"}, allowCredentials = "true")
public class AuthController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "healthy");
        response.put("message", "Authentication service is running");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        Map<String, Object> response = new HashMap<>();
        
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        
        // Temporary test logic - replace with real authentication
        if ("admin".equals(username) && "admin123".equals(password)) {
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("user", Map.of(
                "id", 1,
                "username", username,
                "role", "ADMIN"
            ));
            response.put("token", "fake-jwt-token-" + System.currentTimeMillis());
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.ok(response);
        } else {
            // Authentication failed
            response.put("success", false);
            response.put("message", "Invalid username or password");
            response.put("error", "INVALID_CREDENTIALS");
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}