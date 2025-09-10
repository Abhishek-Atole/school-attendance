package com.school.attendance.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LoggingUtil
 */
class LoggingUtilTest {

    private ListAppender<ILoggingEvent> listAppender;
    private ListAppender<ILoggingEvent> securityAppender;
    private ListAppender<ILoggingEvent> performanceAppender;
    private ListAppender<ILoggingEvent> notificationAppender;

    @BeforeEach
    void setUp() {
        // Clear MDC before each test
        MDC.clear();
        
        // Set up list appenders to capture log events
        Logger logger = (Logger) LoggerFactory.getLogger(LoggingUtil.class);
        Logger securityLogger = (Logger) LoggerFactory.getLogger("SECURITY");
        Logger performanceLogger = (Logger) LoggerFactory.getLogger("PERFORMANCE");
        Logger notificationLogger = (Logger) LoggerFactory.getLogger("NOTIFICATION");
        
        listAppender = new ListAppender<>();
        securityAppender = new ListAppender<>();
        performanceAppender = new ListAppender<>();
        notificationAppender = new ListAppender<>();
        
        listAppender.start();
        securityAppender.start();
        performanceAppender.start();
        notificationAppender.start();
        
        logger.addAppender(listAppender);
        securityLogger.addAppender(securityAppender);
        performanceLogger.addAppender(performanceAppender);
        notificationLogger.addAppender(notificationAppender);
    }

    @Test
    void testSetRequestContext() {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI("/api/students");
        request.setRemoteAddr("127.0.0.1");
        request.addHeader("User-Agent", "Test Agent");

        // When
        LoggingUtil.setRequestContext(request, "user123");

        // Then
        assertEquals("127.0.0.1", MDC.get("ipAddress"));
        assertEquals("user123", MDC.get("userId"));
        assertNotNull(MDC.get("requestId"));
        assertNotNull(MDC.get("sessionId"));
    }

    @Test
    void testClearContext() {
        // Given
        MDC.put("testKey", "testValue");
        
        // When
        LoggingUtil.clearContext();
        
        // Then
        assertNull(MDC.get("testKey"));
    }

    @Test
    void testLogSecurityEvent() {
        // When
        LoggingUtil.logSecurityEvent("Authentication failed", "user123", "Invalid credentials");

        // Then
        assertEquals(1, securityAppender.list.size());
        ILoggingEvent event = securityAppender.list.get(0);
        assertEquals(Level.INFO, event.getLevel());
        String message = event.getFormattedMessage();
        assertTrue(message.contains("SECURITY_EVENT"));
        assertTrue(message.contains("Event: Authentication failed"));
        assertTrue(message.contains("UserId: user123"));
        assertTrue(message.contains("Details: Invalid credentials"));
    }

    @Test
    void testLogUserLogin() {
        // When
        LoggingUtil.logUserLogin("user123", "127.0.0.1", true);

        // Then
        assertEquals(1, securityAppender.list.size());
        ILoggingEvent event = securityAppender.list.get(0);
        assertEquals(Level.INFO, event.getLevel());
        assertTrue(event.getFormattedMessage().contains("USER_LOGIN"));
        assertTrue(event.getFormattedMessage().contains("SUCCESS"));
    }

    @Test
    void testLogUserLoginFailure() {
        // When
        LoggingUtil.logUserLogin("user123", "127.0.0.1", false);

        // Then
        assertEquals(1, securityAppender.list.size());
        ILoggingEvent event = securityAppender.list.get(0);
        assertEquals(Level.INFO, event.getLevel());
        assertTrue(event.getFormattedMessage().contains("FAILED"));
    }

    @Test
    void testLogUserLogout() {
        // When
        LoggingUtil.logUserLogout("user123", "session123");

        // Then
        assertEquals(1, securityAppender.list.size());
        ILoggingEvent event = securityAppender.list.get(0);
        assertEquals(Level.INFO, event.getLevel());
        assertTrue(event.getFormattedMessage().contains("USER_LOGOUT"));
        assertTrue(event.getFormattedMessage().contains("user123"));
    }

    @Test
    void testLogAttendanceMarking() {
        // When
        LoggingUtil.logAttendanceMarking("teacher123", 456L, "PRESENT", "Math-101");

        // Then
        assertEquals(1, securityAppender.list.size());
        ILoggingEvent event = securityAppender.list.get(0);
        assertEquals(Level.INFO, event.getLevel());
        assertTrue(event.getFormattedMessage().contains("ATTENDANCE_MARKED"));
        assertTrue(event.getFormattedMessage().contains("PRESENT"));
    }

    @Test
    void testLogReportGeneration() {
        // When
        LoggingUtil.logReportGeneration("admin123", "ATTENDANCE_REPORT", "class=Math-101,date=2023-12-01");

        // Then
        assertEquals(1, securityAppender.list.size());
        ILoggingEvent event = securityAppender.list.get(0);
        assertEquals(Level.INFO, event.getLevel());
        assertTrue(event.getFormattedMessage().contains("REPORT_GENERATED"));
        assertTrue(event.getFormattedMessage().contains("ATTENDANCE_REPORT"));
    }

    @Test
    void testLogNotificationSentSuccess() {
        // When
        LoggingUtil.logNotificationSent("EMAIL", "user@test.com", "Welcome message", true);

        // Then
        assertEquals(1, notificationAppender.list.size());
        ILoggingEvent event = notificationAppender.list.get(0);
        assertEquals(Level.INFO, event.getLevel());
        assertTrue(event.getFormattedMessage().contains("NOTIFICATION_SENT"));
        assertTrue(event.getFormattedMessage().contains("SUCCESS"));
    }

    @Test
    void testLogNotificationSentFailure() {
        // When
        LoggingUtil.logNotificationSent("SMS", "1234567890", "Alert message", false);

        // Then
        assertEquals(1, notificationAppender.list.size());
        ILoggingEvent event = notificationAppender.list.get(0);
        assertEquals(Level.INFO, event.getLevel());
        assertTrue(event.getFormattedMessage().contains("FAILED"));
    }

    @Test
    void testLogPerformance() {
        // When
        LoggingUtil.logPerformance("StudentService.findAll", 150L, "Retrieved 25 students");

        // Then
        assertEquals(1, performanceAppender.list.size());
        ILoggingEvent event = performanceAppender.list.get(0);
        assertEquals(Level.INFO, event.getLevel());
        assertTrue(event.getFormattedMessage().contains("PERFORMANCE"));
        assertTrue(event.getFormattedMessage().contains("150ms"));
    }

    @Test
    void testLogMethodEntry() {
        // Given
        Logger testLogger = (Logger) LoggerFactory.getLogger("TestLogger");
        testLogger.setLevel(Level.DEBUG);
        ListAppender<ILoggingEvent> testAppender = new ListAppender<>();
        testAppender.start();
        testLogger.addAppender(testAppender);

        // When
        LoggingUtil.logMethodEntry(testLogger, "testMethod", "param1", "param2");

        // Then
        assertEquals(1, testAppender.list.size());
        ILoggingEvent event = testAppender.list.get(0);
        assertEquals(Level.DEBUG, event.getLevel());
        assertTrue(event.getFormattedMessage().contains("ENTERING testMethod"));
    }

    @Test
    void testLogMethodExit() {
        // Given
        Logger testLogger = (Logger) LoggerFactory.getLogger("TestLogger");
        testLogger.setLevel(Level.DEBUG);
        ListAppender<ILoggingEvent> testAppender = new ListAppender<>();
        testAppender.start();
        testLogger.addAppender(testAppender);

        // When
        LoggingUtil.logMethodExit(testLogger, "testMethod", "result");

        // Then
        assertEquals(1, testAppender.list.size());
        ILoggingEvent event = testAppender.list.get(0);
        assertEquals(Level.DEBUG, event.getLevel());
        assertTrue(event.getFormattedMessage().contains("EXITING testMethod"));
    }

    @Test
    void testLogMethodExitWithExecutionTime() {
        // Given
        Logger testLogger = (Logger) LoggerFactory.getLogger("TestLogger");
        testLogger.setLevel(Level.DEBUG);
        ListAppender<ILoggingEvent> testAppender = new ListAppender<>();
        testAppender.start();
        testLogger.addAppender(testAppender);

        // When
        LoggingUtil.logMethodExit(testLogger, "testMethod", 250L);

        // Then
        assertEquals(1, testAppender.list.size());
        ILoggingEvent event = testAppender.list.get(0);
        assertEquals(Level.DEBUG, event.getLevel());
        assertTrue(event.getFormattedMessage().contains("250ms"));
    }

    @Test
    void testGetClientIpAddress() {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "203.0.113.195, 70.41.3.18");

        // When
        String ipAddress = LoggingUtil.getClientIpAddress(request);

        // Then
        assertEquals("203.0.113.195", ipAddress);
    }

    @Test
    void testGetClientIpAddressFromXRealIp() {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Real-IP", "203.0.113.195");

        // When
        String ipAddress = LoggingUtil.getClientIpAddress(request);

        // Then
        assertEquals("203.0.113.195", ipAddress);
    }

    @Test
    void testGetClientIpAddressFallback() {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");

        // When
        String ipAddress = LoggingUtil.getClientIpAddress(request);

        // Then
        assertEquals("127.0.0.1", ipAddress);
    }
}
