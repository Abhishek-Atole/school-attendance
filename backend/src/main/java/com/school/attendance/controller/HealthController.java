package com.school.attendance.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    public String health() {
        return "Backend is running successfully!";
    }
    
    @GetMapping("/cors-test")
    public String corsTest() {
        return "CORS is working!";
    }
}
