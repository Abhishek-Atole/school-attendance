package com.school.attendance.controller;

import com.school.attendance.dto.*;
import com.school.attendance.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "http://localhost:5173")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/attendance/trends")
    public ResponseEntity<List<AttendanceTrendDto>> getAttendanceTrends(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(defaultValue = "student") String type) {
        
        List<AttendanceTrendDto> trends = analyticsService.getAttendanceTrends(start, end, type);
        return ResponseEntity.ok(trends);
    }

    @GetMapping("/gender-ratio")
    public ResponseEntity<GenderRatioDto> getGenderRatio(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        
        GenderRatioDto ratio = analyticsService.getGenderRatio(start, end);
        return ResponseEntity.ok(ratio);
    }

    @GetMapping("/class-performance")
    public ResponseEntity<List<ClassPerformanceDto>> getClassPerformance(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        
        List<ClassPerformanceDto> performance = analyticsService.getClassPerformance(start, end);
        return ResponseEntity.ok(performance);
    }

    @GetMapping("/top-absentees")
    public ResponseEntity<List<TopAbsenteeDto>> getTopAbsentees(
            @RequestParam(defaultValue = "5") int limit) {
        
        List<TopAbsenteeDto> absentees = analyticsService.getTopAbsentees(limit);
        return ResponseEntity.ok(absentees);
    }

    @GetMapping("/dashboard-stats")
    public ResponseEntity<DashboardStatsDto> getDashboardStats() {
        DashboardStatsDto stats = analyticsService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Analytics API is running!");
    }
}
