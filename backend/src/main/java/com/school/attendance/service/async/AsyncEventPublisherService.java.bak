package com.school.attendance.service.async;

import com.school.attendance.event.AttendanceEvents;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Event Publisher Service
 * Publishes attendance events to trigger async processing
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncEventPublisherService {

    private final ApplicationEventPublisher eventPublisher;

    /**
     * Publish attendance marked event
     */
    public void publishAttendanceMarked(Long studentId, Long teacherId, LocalDate attendanceDate, 
                                      String attendanceStatus, String note) {
        log.debug("Publishing attendance marked event for student: {}", studentId);
        
        AttendanceEvents.AttendanceMarkedEvent event = new AttendanceEvents.AttendanceMarkedEvent(
                this, studentId, teacherId, attendanceDate, attendanceStatus, note);
        
        eventPublisher.publishEvent(event);
    }

    /**
     * Publish bulk attendance marked event
     */
    public void publishBulkAttendanceMarked(Long classId, Long teacherId, LocalDate attendanceDate, 
                                          int totalStudents, int presentCount, int absentCount) {
        log.debug("Publishing bulk attendance marked event for class: {}", classId);
        
        AttendanceEvents.BulkAttendanceMarkedEvent event = new AttendanceEvents.BulkAttendanceMarkedEvent(
                this, classId, teacherId, attendanceDate, totalStudents, presentCount, absentCount);
        
        eventPublisher.publishEvent(event);
    }

    /**
     * Publish low attendance alert
     */
    public void publishLowAttendanceAlert(Long studentId, String studentName, String grNo,
                                        double attendancePercentage, double threshold,
                                        LocalDate fromDate, LocalDate toDate) {
        log.warn("Publishing low attendance alert for student: {} - {}%", studentName, attendancePercentage);
        
        AttendanceEvents.LowAttendanceAlert event = new AttendanceEvents.LowAttendanceAlert(
                this, studentId, studentName, grNo, attendancePercentage, threshold, fromDate, toDate);
        
        eventPublisher.publishEvent(event);
    }

    /**
     * Publish attendance report request
     */
    public void publishReportRequest(String reportType, String format, Long requestedBy, String email,
                                   LocalDate fromDate, LocalDate toDate, Long schoolId, Long classId) {
        log.info("Publishing report request - Type: {}, RequestedBy: {}", reportType, requestedBy);
        
        AttendanceEvents.AttendanceReportRequestedEvent event = new AttendanceEvents.AttendanceReportRequestedEvent(
                this, reportType, format, requestedBy, email, fromDate, toDate, schoolId, classId);
        
        eventPublisher.publishEvent(event);
    }

    /**
     * Publish daily attendance summary
     */
    public void publishDailyAttendanceSummary(Long schoolId, LocalDate date, int totalStudents,
                                            int presentStudents, int absentStudents, int lateStudents,
                                            double attendancePercentage) {
        log.debug("Publishing daily attendance summary for school: {} on {}", schoolId, date);
        
        AttendanceEvents.DailyAttendanceSummaryEvent event = new AttendanceEvents.DailyAttendanceSummaryEvent(
                this, schoolId, date, totalStudents, presentStudents, absentStudents, lateStudents, attendancePercentage);
        
        eventPublisher.publishEvent(event);
    }

    // ========== CONVENIENCE METHODS ==========

    /**
     * Trigger async notification for single attendance marking
     */
    public void notifyAttendanceMarked(Long studentId, String status, LocalDate date, String note) {
        publishAttendanceMarked(studentId, null, date, status, note);
    }

    /**
     * Trigger async notification for bulk attendance
     */
    public void notifyBulkAttendance(Long classId, LocalDate date, int total, int present, int absent) {
        publishBulkAttendanceMarked(classId, null, date, total, present, absent);
    }

    /**
     * Generate daily report asynchronously
     */
    public void generateDailyReport(Long schoolId, LocalDate date, Long requestedBy, String email) {
        publishReportRequest("DAILY_SUMMARY", "PDF", requestedBy, email, date, date, schoolId, null);
    }

    /**
     * Generate student report asynchronously
     */
    public void generateStudentReport(Long studentId, LocalDate fromDate, LocalDate toDate, 
                                    Long requestedBy, String email, Long schoolId) {
        publishReportRequest("STUDENT_ATTENDANCE", "PDF", requestedBy, email, fromDate, toDate, schoolId, null);
    }

    /**
     * Generate class report asynchronously
     */
    public void generateClassReport(Long classId, LocalDate fromDate, LocalDate toDate, 
                                  Long requestedBy, String email, Long schoolId) {
        publishReportRequest("CLASS_ATTENDANCE", "PDF", requestedBy, email, fromDate, toDate, schoolId, classId);
    }

    /**
     * Check and alert for low attendance students
     */
    public void checkLowAttendanceStudents(Long schoolId, LocalDate fromDate, LocalDate toDate, double threshold) {
        log.info("Initiating low attendance check for school: {} with threshold: {}%", schoolId, threshold);
        
        // In a real implementation, this would:
        // 1. Query database for students with low attendance
        // 2. For each student below threshold, publish LowAttendanceAlert event
        // 3. Async notification service would handle sending alerts
        
        // For demonstration, let's simulate finding one low attendance student
        publishLowAttendanceAlert(1L, "John Doe", "GR001", 65.5, threshold, fromDate, toDate);
    }

    /**
     * Publish end-of-day summary
     */
    public void publishEndOfDaySummary(Long schoolId, LocalDate date) {
        log.info("Publishing end-of-day summary for school: {} on {}", schoolId, date);
        
        // In a real implementation, this would:
        // 1. Calculate actual attendance statistics for the day
        // 2. Publish the summary event
        // 3. Async services would handle notifications and reporting
        
        // Simulated statistics for demonstration
        publishDailyAttendanceSummary(schoolId, date, 500, 450, 40, 10, 92.0);
    }

    // ========== BATCH OPERATIONS ==========

    /**
     * Process all pending notifications for a school
     */
    public void processPendingNotifications(Long schoolId, LocalDate date) {
        log.info("Processing pending notifications for school: {} on {}", schoolId, date);
        
        // This would typically:
        // 1. Check for unmarked attendance
        // 2. Send reminders to teachers
        // 3. Generate parent notifications for absences
        // 4. Create administrative alerts
        
        // For now, just trigger the daily summary
        publishEndOfDaySummary(schoolId, date);
    }

    /**
     * Generate weekly reports for all classes
     */
    public void generateWeeklyReports(Long schoolId, LocalDate weekStartDate, Long requestedBy, String email) {
        log.info("Generating weekly reports for school: {} starting from {}", schoolId, weekStartDate);
        
        LocalDate weekEndDate = weekStartDate.plusDays(6);
        
        // Generate school-wide weekly report
        publishReportRequest("WEEKLY_SUMMARY", "PDF", requestedBy, email, 
                           weekStartDate, weekEndDate, schoolId, null);
        
        // In production, you might also generate class-specific reports
        // for each class in the school
    }

    /**
     * Generate monthly reports
     */
    public void generateMonthlyReports(Long schoolId, LocalDate monthStartDate, Long requestedBy, String email) {
        log.info("Generating monthly reports for school: {} for month starting {}", schoolId, monthStartDate);
        
        LocalDate monthEndDate = monthStartDate.plusMonths(1).minusDays(1);
        
        publishReportRequest("MONTHLY_SUMMARY", "PDF", requestedBy, email, 
                           monthStartDate, monthEndDate, schoolId, null);
    }
}
