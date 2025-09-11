package com.school.attendance.service.notification;

import com.school.attendance.entity.NotificationLog;
import com.school.attendance.entity.NotificationSettings;
import com.school.attendance.entity.School;
import com.school.attendance.repository.NotificationLogRepository;
import com.school.attendance.repository.NotificationSettingsRepository;
import com.school.attendance.repository.SchoolRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class NotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private SmsService smsService;
    
    @Autowired
    private NotificationLogRepository notificationLogRepository;
    
    @Autowired
    private NotificationSettingsRepository settingsRepository;
    
    @Autowired
    private SchoolRepository schoolRepository;

    /**
     * Send email notification
     */
    public boolean sendEmail(String to, String subject, String body, Long studentId, Long schoolId) {
        logger.info("Sending email to: {}", to);
        
        boolean success = emailService.sendEmail(to, subject, body);
        
        // Log the notification
        try {
            NotificationLog log;
            if (success) {
                log = new NotificationLog(NotificationLog.NotificationType.EMAIL, to, subject, body, studentId, schoolId);
            } else {
                log = new NotificationLog(NotificationLog.NotificationType.EMAIL, to, subject, body, "Email sending failed", studentId, schoolId);
            }
            notificationLogRepository.save(log);
        } catch (Exception e) {
            logger.error("Error logging email notification: {}", e.getMessage());
        }
        
        return success;
    }

    /**
     * Send email notification (overloaded for controller compatibility)
     */
    public boolean sendEmail(String to, String subject, String body) {
        return sendEmail(to, subject, body, null, null);
    }

    /**
     * Send SMS notification
     */
    public boolean sendSms(String to, String message, Long studentId, Long schoolId) {
        logger.info("Sending SMS to: {}", to);
        
        boolean success = smsService.sendSms(to, message);
        
        // Log the notification using logNotification method
        try {
            NotificationLog log;
            if (success) {
                log = new NotificationLog(NotificationLog.NotificationType.SMS, to, "SMS", message, studentId, schoolId);
            } else {
                log = new NotificationLog(NotificationLog.NotificationType.SMS, to, "SMS", message, "SMS sending failed", studentId, schoolId);
            }
            notificationLogRepository.save(log);
        } catch (Exception e) {
            logger.error("Error logging SMS notification: {}", e.getMessage());
        }
        
        return success;
    }

    /**
     * Send SMS notification (overloaded for controller compatibility)
     */
    public boolean sendSms(String to, String message) {
        return sendSms(to, message, null, null);
    }

    /**
     * Get notification logs with pagination
     */
    public Page<NotificationLog> getNotificationLogs(Pageable pageable) {
        return notificationLogRepository.findAll(pageable);
    }

    /**
     * Get notification logs by school
     */
    public Page<NotificationLog> getNotificationLogsBySchool(Long schoolId, Pageable pageable) {
        return notificationLogRepository.findAll(pageable); // For now, return all logs
    }

    /**
     * Get notification statistics
     */
    public Map<String, Object> getNotificationStats() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalNotifications = notificationLogRepository.count();
        long sentNotifications = notificationLogRepository.countByStatus(NotificationLog.NotificationStatus.SUCCESS);
        long failedNotifications = notificationLogRepository.countByStatus(NotificationLog.NotificationStatus.FAILED);
        long emailNotifications = notificationLogRepository.countByType(NotificationLog.NotificationType.EMAIL);
        long smsNotifications = notificationLogRepository.countByType(NotificationLog.NotificationType.SMS);
        
        stats.put("totalNotifications", totalNotifications);
        stats.put("sentNotifications", sentNotifications);
        stats.put("failedNotifications", failedNotifications);
        stats.put("emailNotifications", emailNotifications);
        stats.put("smsNotifications", smsNotifications);
        
        return stats;
    }

    /**
     * Get notification settings for a school
     */
    public NotificationSettings getNotificationSettings(Long schoolId) {
        Optional<NotificationSettings> settings = settingsRepository.findBySchoolId(schoolId);
        if (settings.isPresent()) {
            return settings.get();
        }
        
        // Create default settings if none exist
        Optional<School> schoolOpt = schoolRepository.findById(schoolId);
        if (schoolOpt.isPresent()) {
            NotificationSettings defaultSettings = new NotificationSettings(schoolOpt.get());
            return settingsRepository.save(defaultSettings);
        }
        
        throw new RuntimeException("School not found with ID: " + schoolId);
    }

    /**
     * Test email configuration
     */
    public boolean testEmailConfiguration(String testEmail) {
        return emailService.sendEmail(testEmail, "Test Email", "This is a test email from the School Attendance System.");
    }

    /**
     * Test SMS configuration
     */
    public boolean testSmsConfiguration(String testPhone) {
        return smsService.sendSms(testPhone, "Test SMS from School Attendance System");
    }

    /**
     * Get notification logs by type (for controller compatibility)
     */
    public List<NotificationLog> getNotificationLogsByType(NotificationLog.NotificationType type) {
        return notificationLogRepository.findAll(); // Return all for now as a List
    }

    /**
     * Update notification settings (for controller compatibility)
     */
    public NotificationSettings updateNotificationSettings(Long schoolId, Map<String, Object> updates) {
        NotificationSettings settings = getNotificationSettings(schoolId);
        
        // Update the settings based on the provided map
        if (updates.containsKey("emailEnabled")) {
            settings.setEmailEnabled((Boolean) updates.get("emailEnabled"));
        }
        if (updates.containsKey("smsEnabled")) {
            settings.setSmsEnabled((Boolean) updates.get("smsEnabled"));
        }
        if (updates.containsKey("dailyAbsenteeAlerts")) {
            settings.setDailyAbsenteeAlerts((Boolean) updates.get("dailyAbsenteeAlerts"));
        }
        if (updates.containsKey("lowAttendanceAlerts")) {
            settings.setLowAttendanceAlerts((Boolean) updates.get("lowAttendanceAlerts"));
        }
        if (updates.containsKey("holidayNotifications")) {
            settings.setHolidayNotifications((Boolean) updates.get("holidayNotifications"));
        }
        if (updates.containsKey("attendanceThreshold")) {
            settings.setAttendanceThreshold(((Number) updates.get("attendanceThreshold")).doubleValue());
        }
        if (updates.containsKey("notificationTime")) {
            settings.setNotificationTime((String) updates.get("notificationTime"));
        }
        
        return settingsRepository.save(settings);
    }
}
