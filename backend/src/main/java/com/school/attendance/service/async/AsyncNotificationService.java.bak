package com.school.attendance.service.async;

import com.school.attendance.event.AttendanceEvents;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Async Notification Service
 * Handles background notification processing for attendance events
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncNotificationService {

    /**
     * Handle attendance marked event asynchronously
     */
    @Async("notificationTaskExecutor")
    @EventListener
    public void handleAttendanceMarked(AttendanceEvents.AttendanceMarkedEvent event) {
        log.info("Processing attendance marked notification for student: {} on date: {}", 
                event.getStudentId(), event.getAttendanceDate());
        
        try {
            // Simulate notification processing
            Thread.sleep(1000); // Simulate email/SMS sending time
            
            if ("ABSENT".equals(event.getAttendanceStatus())) {
                sendAbsentNotification(event);
            } else if ("LATE".equals(event.getAttendanceStatus())) {
                sendLateNotification(event);
            }
            
            log.info("Attendance notification sent successfully for student: {}", event.getStudentId());
        } catch (Exception e) {
            log.error("Failed to send attendance notification for student: {} - {}", 
                    event.getStudentId(), e.getMessage(), e);
        }
    }

    /**
     * Handle bulk attendance marked event asynchronously
     */
    @Async("notificationTaskExecutor")
    @EventListener
    public void handleBulkAttendanceMarked(AttendanceEvents.BulkAttendanceMarkedEvent event) {
        log.info("Processing bulk attendance notification for class: {} on date: {}", 
                event.getClassId(), event.getAttendanceDate());
        
        try {
            // Simulate bulk notification processing
            Thread.sleep(2000); // Simulate bulk processing time
            
            sendBulkAttendanceSummary(event);
            
            log.info("Bulk attendance notification sent successfully for class: {}", event.getClassId());
        } catch (Exception e) {
            log.error("Failed to send bulk attendance notification for class: {} - {}", 
                    event.getClassId(), e.getMessage(), e);
        }
    }

    /**
     * Handle low attendance alert asynchronously
     */
    @Async("notificationTaskExecutor")
    @EventListener
    public void handleLowAttendanceAlert(AttendanceEvents.LowAttendanceAlert event) {
        log.warn("Processing low attendance alert for student: {} - Attendance: {}%", 
                event.getStudentId(), event.getAttendancePercentage());
        
        try {
            // Simulate alert processing
            Thread.sleep(1500); // Simulate alert processing time
            
            sendLowAttendanceAlert(event);
            
            log.info("Low attendance alert sent successfully for student: {}", event.getStudentId());
        } catch (Exception e) {
            log.error("Failed to send low attendance alert for student: {} - {}", 
                    event.getStudentId(), e.getMessage(), e);
        }
    }

    /**
     * Handle daily summary event asynchronously
     */
    @Async("notificationTaskExecutor")
    @EventListener
    public void handleDailySummary(AttendanceEvents.DailyAttendanceSummaryEvent event) {
        log.info("Processing daily attendance summary for school: {} on date: {}", 
                event.getSchoolId(), event.getDate());
        
        try {
            // Simulate summary processing
            Thread.sleep(3000); // Simulate summary generation time
            
            sendDailySummaryToAdministrators(event);
            
            log.info("Daily summary notification sent successfully for school: {}", event.getSchoolId());
        } catch (Exception e) {
            log.error("Failed to send daily summary for school: {} - {}", 
                    event.getSchoolId(), e.getMessage(), e);
        }
    }

    // ========== PRIVATE NOTIFICATION METHODS ==========

    /**
     * Send notification for absent student
     */
    private void sendAbsentNotification(AttendanceEvents.AttendanceMarkedEvent event) {
        log.info("Sending absent notification - Student: {}, Date: {}", 
                event.getStudentId(), event.getAttendanceDate());
        
        // Implementation would include:
        // 1. Get student details from database
        // 2. Get parent contact information
        // 3. Send SMS/Email notification
        // 4. Log notification in database
        
        // Mock notification
        String message = String.format(
            "Dear Parent, your child (ID: %d) was marked absent on %s. Please contact school if this is incorrect.",
            event.getStudentId(), event.getAttendanceDate()
        );
        
        simulateNotificationSending("SMS", message);
        simulateNotificationSending("EMAIL", message);
    }

    /**
     * Send notification for late student
     */
    private void sendLateNotification(AttendanceEvents.AttendanceMarkedEvent event) {
        log.info("Sending late notification - Student: {}, Date: {}", 
                event.getStudentId(), event.getAttendanceDate());
        
        String message = String.format(
            "Dear Parent, your child (ID: %d) arrived late on %s. Note: %s",
            event.getStudentId(), event.getAttendanceDate(), 
            event.getNote() != null ? event.getNote() : "No additional notes"
        );
        
        simulateNotificationSending("SMS", message);
    }

    /**
     * Send bulk attendance summary
     */
    private void sendBulkAttendanceSummary(AttendanceEvents.BulkAttendanceMarkedEvent event) {
        log.info("Sending bulk attendance summary - Class: {}, Date: {}", 
                event.getClassId(), event.getAttendanceDate());
        
        String message = String.format(
            "Class %d Attendance Summary for %s: Total: %d, Present: %d, Absent: %d",
            event.getClassId(), event.getAttendanceDate(), 
            event.getTotalStudents(), event.getPresentCount(), event.getAbsentCount()
        );
        
        simulateNotificationSending("EMAIL", message);
    }

    /**
     * Send low attendance alert
     */
    private void sendLowAttendanceAlert(AttendanceEvents.LowAttendanceAlert event) {
        log.warn("Sending low attendance alert - Student: {}, Percentage: {}%", 
                event.getStudentId(), event.getAttendancePercentage());
        
        String message = String.format(
            "ALERT: Student %s (GR: %s) has low attendance of %.1f%% (below threshold of %.1f%%) from %s to %s",
            event.getStudentName(), event.getGrNo(), event.getAttendancePercentage(),
            event.getThreshold(), event.getFromDate(), event.getToDate()
        );
        
        simulateNotificationSending("EMAIL", message);
        simulateNotificationSending("SMS", message);
        
        // Also notify school administrators
        simulateNotificationSending("ADMIN_EMAIL", message);
    }

    /**
     * Send daily summary to administrators
     */
    private void sendDailySummaryToAdministrators(AttendanceEvents.DailyAttendanceSummaryEvent event) {
        log.info("Sending daily summary to administrators - School: {}, Date: {}", 
                event.getSchoolId(), event.getDate());
        
        String message = String.format(
            "Daily Attendance Summary for %s:\nTotal Students: %d\nPresent: %d\nAbsent: %d\nLate: %d\nAttendance Rate: %.1f%%",
            event.getDate(), event.getTotalStudents(), event.getPresentStudents(),
            event.getAbsentStudents(), event.getLateStudents(), event.getAttendancePercentage()
        );
        
        simulateNotificationSending("ADMIN_EMAIL", message);
        simulateNotificationSending("DASHBOARD_NOTIFICATION", message);
    }

    /**
     * Simulate notification sending (replace with actual implementation)
     */
    private void simulateNotificationSending(String type, String message) {
        try {
            // Simulate network delay
            Thread.sleep(100);
            log.debug("SIMULATED {} SENT: {}", type, message.substring(0, Math.min(50, message.length())) + "...");
            
            // In real implementation, this would:
            // - Use email service (like SendGrid, AWS SES)
            // - Use SMS service (like Twilio, AWS SNS)
            // - Send push notifications
            // - Log to notification history table
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Notification sending interrupted: {}", e.getMessage());
        }
    }

    /**
     * Get notification statistics (for monitoring)
     */
    public void logNotificationStats() {
        log.info("Notification service statistics logged - implement with metrics if needed");
        // In production, this would track:
        // - Notifications sent per type
        // - Success/failure rates
        // - Average processing times
        // - Queue sizes
    }
}
