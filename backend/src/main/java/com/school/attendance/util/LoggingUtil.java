package com.school.attendance.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Logging utility class for structured logging
 */
public class LoggingUtil {
    
    private static final Logger SECURITY_LOGGER = LoggerFactory.getLogger("SECURITY");
    private static final Logger PERFORMANCE_LOGGER = LoggerFactory.getLogger("PERFORMANCE");
    private static final Logger NOTIFICATION_LOGGER = LoggerFactory.getLogger("NOTIFICATION");
    
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // MDC Keys for structured logging
    public static final String USER_ID = "userId";
    public static final String SESSION_ID = "sessionId";
    public static final String REQUEST_ID = "requestId";
    public static final String IP_ADDRESS = "ipAddress";
    public static final String USER_AGENT = "userAgent";
    
    /**
     * Set MDC context for request tracking
     */
    public static void setRequestContext(HttpServletRequest request, String userId) {
        MDC.put(REQUEST_ID, generateRequestId());
        MDC.put(IP_ADDRESS, getClientIpAddress(request));
        MDC.put(USER_AGENT, request.getHeader("User-Agent"));
        MDC.put(SESSION_ID, request.getSession().getId());
        if (userId != null) {
            MDC.put(USER_ID, userId);
        }
    }
    
    /**
     * Clear MDC context
     */
    public static void clearContext() {
        MDC.clear();
    }
    
    /**
     * Log security events
     */
    public static void logSecurityEvent(String event, String userId, String details) {
        SECURITY_LOGGER.info("SECURITY_EVENT | Event: {} | UserId: {} | Details: {} | Timestamp: {}", 
                event, userId, details, getCurrentTimestamp());
    }
    
    /**
     * Log user login
     */
    public static void logUserLogin(String userId, String ipAddress, boolean success) {
        String status = success ? "SUCCESS" : "FAILED";
        SECURITY_LOGGER.info("USER_LOGIN | UserId: {} | IP: {} | Status: {} | Timestamp: {}", 
                userId, ipAddress, status, getCurrentTimestamp());
    }
    
    /**
     * Log user logout
     */
    public static void logUserLogout(String userId, String sessionId) {
        SECURITY_LOGGER.info("USER_LOGOUT | UserId: {} | SessionId: {} | Timestamp: {}", 
                userId, sessionId, getCurrentTimestamp());
    }
    
    /**
     * Log attendance marking
     */
    public static void logAttendanceMarking(String teacherId, Long studentId, String status, String className) {
        SECURITY_LOGGER.info("ATTENDANCE_MARKED | TeacherId: {} | StudentId: {} | Status: {} | Class: {} | Timestamp: {}", 
                teacherId, studentId, status, className, getCurrentTimestamp());
    }
    
    /**
     * Log report generation
     */
    public static void logReportGeneration(String userId, String reportType, String parameters) {
        SECURITY_LOGGER.info("REPORT_GENERATED | UserId: {} | ReportType: {} | Parameters: {} | Timestamp: {}", 
                userId, reportType, parameters, getCurrentTimestamp());
    }
    
    /**
     * Log notification sending
     */
    public static void logNotificationSent(String type, String recipient, String subject, boolean success) {
        String status = success ? "SUCCESS" : "FAILED";
        NOTIFICATION_LOGGER.info("NOTIFICATION_SENT | Type: {} | Recipient: {} | Subject: {} | Status: {} | Timestamp: {}", 
                type, recipient, subject, status, getCurrentTimestamp());
    }
    
    /**
     * Log performance metrics
     */
    public static void logPerformance(String operation, long executionTimeMs, String details) {
        PERFORMANCE_LOGGER.info("PERFORMANCE | Operation: {} | ExecutionTime: {}ms | Details: {} | Timestamp: {}", 
                operation, executionTimeMs, details, getCurrentTimestamp());
    }
    
    /**
     * Log method entry with parameters
     */
    public static void logMethodEntry(Logger logger, String methodName, Object... parameters) {
        if (logger.isDebugEnabled()) {
            logger.debug("ENTERING {} with parameters: {}", methodName, parameters);
        }
    }
    
    /**
     * Log method exit with result
     */
    public static void logMethodExit(Logger logger, String methodName, Object result) {
        if (logger.isDebugEnabled()) {
            logger.debug("EXITING {} with result: {}", methodName, result);
        }
    }
    
    /**
     * Log method exit with execution time
     */
    public static void logMethodExit(Logger logger, String methodName, long executionTimeMs) {
        if (logger.isDebugEnabled()) {
            logger.debug("EXITING {} - execution time: {}ms", methodName, executionTimeMs);
        }
    }
    
    /**
     * Generate unique request ID
     */
    private static String generateRequestId() {
        return "REQ-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
    }
    
    /**
     * Get client IP address from request
     */
    public static String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    /**
     * Get current timestamp formatted
     */
    private static String getCurrentTimestamp() {
        return LocalDateTime.now().format(TIMESTAMP_FORMATTER);
    }
}
