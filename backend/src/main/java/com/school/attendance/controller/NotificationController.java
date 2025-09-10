package com.school.attendance.controller;

import com.school.attendance.entity.NotificationLog;
import com.school.attendance.entity.NotificationSettings;
import com.school.attendance.service.notification.NotificationService;
import com.school.attendance.service.notification.NotificationSchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class NotificationController {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private NotificationSchedulerService schedulerService;

    /**
     * Get notification logs with pagination
     */
    @GetMapping("/logs")
    public ResponseEntity<Page<NotificationLog>> getNotificationLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "sentAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        logger.info("Fetching notification logs - page: {}, size: {}, sortBy: {}, sortDir: {}", 
                   page, size, sortBy, sortDir);
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                       Sort.by(sortBy).descending() : 
                       Sort.by(sortBy).ascending();
            
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<NotificationLog> logs = notificationService.getNotificationLogs(pageable);
            
            return ResponseEntity.ok(logs);
            
        } catch (Exception e) {
            logger.error("Error fetching notification logs: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get notification logs by type
     */
    @GetMapping("/logs/type/{type}")
    public ResponseEntity<List<NotificationLog>> getNotificationLogsByType(
            @PathVariable NotificationLog.NotificationType type) {
        
        logger.info("Fetching notification logs by type: {}", type);
        
        try {
            List<NotificationLog> logs = notificationService.getNotificationLogsByType(type);
            return ResponseEntity.ok(logs);
            
        } catch (Exception e) {
            logger.error("Error fetching notification logs by type: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get notification settings for a school
     */
    @GetMapping("/settings/{schoolId}")
    public ResponseEntity<NotificationSettings> getNotificationSettings(@PathVariable Long schoolId) {
        
        logger.info("Fetching notification settings for school: {}", schoolId);
        
        try {
            NotificationSettings settings = notificationService.getNotificationSettings(schoolId);
            return ResponseEntity.ok(settings);
            
        } catch (Exception e) {
            logger.error("Error fetching notification settings: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Update notification settings for a school
     */
    @PutMapping("/settings/{schoolId}")
    public ResponseEntity<NotificationSettings> updateNotificationSettings(
            @PathVariable Long schoolId,
            @RequestBody Map<String, Object> settingsData) {
        
        logger.info("Updating notification settings for school: {}", schoolId);
        
        try {
            NotificationSettings updatedSettings = notificationService.updateNotificationSettings(schoolId, settingsData);
            return ResponseEntity.ok(updatedSettings);
            
        } catch (Exception e) {
            logger.error("Error updating notification settings: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Send test email
     */
    @PostMapping("/test/email")
    public ResponseEntity<Map<String, Object>> sendTestEmail(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String message) {
        
        logger.info("Sending test email to: {}", to);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean success = notificationService.sendEmail(to, subject, message);
            
            response.put("success", success);
            response.put("message", success ? "Test email sent successfully" : "Failed to send test email");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error sending test email: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error sending test email: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Send test SMS
     */
    @PostMapping("/test/sms")
    public ResponseEntity<Map<String, Object>> sendTestSms(
            @RequestParam String to,
            @RequestParam String message) {
        
        logger.info("Sending test SMS to: {}", to);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean success = notificationService.sendSms(to, message);
            
            response.put("success", success);
            response.put("message", success ? "Test SMS sent successfully" : "Failed to send test SMS");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error sending test SMS: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error sending test SMS: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Trigger daily absentee alerts manually (for testing)
     */
    @PostMapping("/trigger/daily-alerts")
    public ResponseEntity<Map<String, Object>> triggerDailyAlerts() {
        
        logger.info("Manually triggering daily absentee alerts");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            schedulerService.triggerDailyAlerts();
            
            response.put("success", true);
            response.put("message", "Daily alerts triggered successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error triggering daily alerts: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error triggering daily alerts: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Trigger low attendance alerts manually (for testing)
     */
    @PostMapping("/trigger/low-attendance-alerts")
    public ResponseEntity<Map<String, Object>> triggerLowAttendanceAlerts() {
        
        logger.info("Manually triggering low attendance alerts");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            schedulerService.triggerLowAttendanceAlerts();
            
            response.put("success", true);
            response.put("message", "Low attendance alerts triggered successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error triggering low attendance alerts: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error triggering low attendance alerts: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Get notification statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getNotificationStats() {
        
        logger.info("Fetching notification statistics");
        
        try {
            Map<String, Object> stats = notificationService.getNotificationStats();
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            logger.error("Error fetching notification stats: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
