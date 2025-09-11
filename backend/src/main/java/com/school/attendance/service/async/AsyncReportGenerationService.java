package com.school.attendance.service.async;

import com.school.attendance.entity.AttendanceRecord;
import com.school.attendance.entity.Student;
import com.school.attendance.event.AttendanceEvents;
import com.school.attendance.repository.AttendanceRecordRepository;
import com.school.attendance.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Async Report Generation Service
 * Handles background report processing for attendance data
 */
//@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncReportGenerationService {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final StudentRepository studentRepository;

    /**
     * Handle report request event asynchronously
     */
    @Async("reportTaskExecutor")
    @EventListener
    public void handleReportRequest(AttendanceEvents.AttendanceReportRequestedEvent event) {
        log.info("Processing report request - Type: {}, School: {}, Class: {}, Date Range: {} to {}", 
                event.getReportType(), event.getSchoolId(), event.getClassId(), 
                event.getFromDate(), event.getToDate());
        
        try {
            String reportContent = switch (event.getReportType()) {
                case "DAILY_SUMMARY" -> generateDailySummaryReport(event);
                case "STUDENT_ATTENDANCE" -> generateStudentAttendanceReport(event);
                case "CLASS_ATTENDANCE" -> generateClassAttendanceReport(event);
                case "LOW_ATTENDANCE_STUDENTS" -> generateLowAttendanceReport(event);
                case "MONTHLY_SUMMARY" -> generateMonthlySummaryReport(event);
                case "DETAILED_ATTENDANCE" -> generateDetailedAttendanceReport(event);
                default -> generateGenericReport(event);
            };
            
            // In production, save report to file system/cloud storage
            saveReportToStorage(event, reportContent);
            
            // Notify user that report is ready
            notifyReportCompletion(event, reportContent.length());
            
            log.info("Report generation completed successfully - Type: {}, Size: {} chars", 
                    event.getReportType(), reportContent.length());
            
        } catch (Exception e) {
            log.error("Failed to generate report - Type: {}, Error: {}", 
                    event.getReportType(), e.getMessage(), e);
            notifyReportFailure(event, e.getMessage());
        }
    }

    // ========== REPORT GENERATION METHODS ==========

    /**
     * Generate daily summary report
     */
    private String generateDailySummaryReport(AttendanceEvents.AttendanceReportRequestedEvent event) {
        log.info("Generating daily summary report for date: {}", event.getFromDate());
        
        StringBuilder report = new StringBuilder();
        report.append("DAILY ATTENDANCE SUMMARY REPORT\n");
        report.append("=" .repeat(50)).append("\n");
        report.append("Date: ").append(event.getFromDate()).append("\n");
        report.append("School ID: ").append(event.getSchoolId()).append("\n");
        report.append("Generated: ").append(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)).append("\n\n");

        // Get attendance data for the date - using a simpler approach for now
        LocalDate reportDate = LocalDate.parse(event.getFromDate().toString());
        List<AttendanceRecord> attendanceList = attendanceRecordRepository.findByDateBetweenOrderByDateDesc(
                reportDate, reportDate, PageRequest.of(0, 1000)).getContent();
        
        // Calculate statistics
        Map<String, Long> statusCounts = attendanceList.stream()
                .collect(Collectors.groupingBy(record -> record.getStatus().name(), Collectors.counting()));
        
        long totalStudents = attendanceList.size();
        long presentCount = statusCounts.getOrDefault("PRESENT", 0L);
        long absentCount = statusCounts.getOrDefault("ABSENT", 0L);
        long lateCount = statusCounts.getOrDefault("LATE", 0L);
        
        double attendancePercentage = totalStudents > 0 ? 
                (double) (presentCount + lateCount) / totalStudents * 100 : 0.0;
        
        report.append("ATTENDANCE STATISTICS:\n");
        report.append("-".repeat(25)).append("\n");
        report.append(String.format("Total Students: %d\n", totalStudents));
        report.append(String.format("Present: %d (%.1f%%)\n", presentCount, 
                totalStudents > 0 ? (double) presentCount / totalStudents * 100 : 0));
        report.append(String.format("Absent: %d (%.1f%%)\n", absentCount, 
                totalStudents > 0 ? (double) absentCount / totalStudents * 100 : 0));
        report.append(String.format("Late: %d (%.1f%%)\n", lateCount, 
                totalStudents > 0 ? (double) lateCount / totalStudents * 100 : 0));
        report.append(String.format("Overall Attendance Rate: %.2f%%\n\n", attendancePercentage));
        
        // Add detailed breakdown if needed
        if (event.getClassId() == null) {
            report.append("CLASS-WISE BREAKDOWN:\n");
            report.append("-".repeat(25)).append("\n");
            Map<String, List<AttendanceRecord>> classGroups = attendanceList.stream()
                    .collect(Collectors.groupingBy(a -> a.getStudent().getStandard() + "-" + 
                            (a.getStudent().getSection() != null ? a.getStudent().getSection() : "")));
            
            classGroups.forEach((classKey, classAttendance) -> {
                long classTotal = classAttendance.size();
                long classPresent = classAttendance.stream()
                        .mapToLong(a -> "PRESENT".equals(a.getStatus().name()) || "LATE".equals(a.getStatus().name()) ? 1 : 0)
                        .sum();
                double classPercentage = classTotal > 0 ? (double) classPresent / classTotal * 100 : 0.0;
                
                report.append(String.format("Class %s: %d/%d students present (%.1f%%)\n", 
                        classKey, classPresent, classTotal, classPercentage));
            });
        }
        
        return report.toString();
    }

    /**
     * Generate student attendance report
     */
    private String generateStudentAttendanceReport(AttendanceEvents.AttendanceReportRequestedEvent event) {
        log.info("Generating student attendance report from {} to {}", 
                event.getFromDate(), event.getToDate());
        
        StringBuilder report = new StringBuilder();
        report.append("STUDENT ATTENDANCE REPORT\n");
        report.append("=" .repeat(40)).append("\n");
        
        report.append(String.format("Period: %s to %s\n", event.getFromDate(), event.getToDate()));
        report.append(String.format("Generated: %s\n\n", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)));
        
        // Get attendance records for the period
        List<AttendanceRecord> attendanceRecords = attendanceRecordRepository.findByDateBetweenOrderByDateDesc(
                event.getFromDate(), event.getToDate(), PageRequest.of(0, 1000)).getContent();
        
        // Calculate statistics
        Map<String, Long> statusCounts = attendanceRecords.stream()
                .collect(Collectors.groupingBy(record -> record.getStatus().name(), Collectors.counting()));
        
        long totalDays = attendanceRecords.size();
        long presentDays = statusCounts.getOrDefault("PRESENT", 0L);
        long absentDays = statusCounts.getOrDefault("ABSENT", 0L);
        long lateDays = statusCounts.getOrDefault("LATE", 0L);
        
        double attendancePercentage = totalDays > 0 ? 
                (double) (presentDays + lateDays) / totalDays * 100 : 0.0;
        
        report.append("ATTENDANCE SUMMARY:\n");
        report.append("-".repeat(20)).append("\n");
        report.append(String.format("Total Records: %d\n", totalDays));
        report.append(String.format("Present: %d records\n", presentDays));
        report.append(String.format("Absent: %d records\n", absentDays));
        report.append(String.format("Late: %d records\n", lateDays));
        report.append(String.format("Attendance Percentage: %.2f%%\n\n", attendancePercentage));
        
        return report.toString();
    }

    /**
     * Generate class attendance report
     */
    private String generateClassAttendanceReport(AttendanceEvents.AttendanceReportRequestedEvent event) {
        log.info("Generating class attendance report for school: {} from {} to {}", 
                event.getSchoolId(), event.getFromDate(), event.getToDate());
        
        StringBuilder report = new StringBuilder();
        report.append("CLASS ATTENDANCE REPORT\n");
        report.append("=" .repeat(35)).append("\n");
        report.append(String.format("School ID: %d\n", event.getSchoolId()));
        report.append(String.format("Period: %s to %s\n", event.getFromDate(), event.getToDate()));
        report.append(String.format("Generated: %s\n\n", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)));
        
        // Get all students in the school
        List<Student> allStudents = studentRepository.findBySchoolIdAndIsActiveTrueOrderByStandardAscSectionAscRollNoAsc(event.getSchoolId());
        
        report.append("STUDENT-WISE ATTENDANCE SUMMARY:\n");
        report.append("-".repeat(40)).append("\n");
        report.append(String.format("%-15s %-20s %-15s %-10s\n", "GR No", "Student Name", "Class", "Percentage"));
        report.append("-".repeat(70)).append("\n");
        
        for (Student student : allStudents.subList(0, Math.min(100, allStudents.size()))) { // Limit to avoid memory issues
            List<AttendanceRecord> studentAttendance = attendanceRecordRepository
                    .findByStudentIdAndDateBetweenOrderByDateDesc(student.getId(), event.getFromDate(), event.getToDate());
            
            long totalDays = studentAttendance.size();
            long presentDays = studentAttendance.stream()
                    .mapToLong(a -> "PRESENT".equals(a.getStatus().name()) || "LATE".equals(a.getStatus().name()) ? 1 : 0)
                    .sum();
            
            double percentage = totalDays > 0 ? (double) presentDays / totalDays * 100 : 0.0;
            
            report.append(String.format("%-15s %-20s %-15s %-10.1f%%\n",
                    student.getGrNo(),
                    student.getFirstName() + " " + student.getLastName(),
                    student.getStandard() + "-" + (student.getSection() != null ? student.getSection() : ""),
                    percentage));
        }
        
        return report.toString();
    }

    /**
     * Generate low attendance students report
     */
    private String generateLowAttendanceReport(AttendanceEvents.AttendanceReportRequestedEvent event) {
        log.info("Generating low attendance report");
        
        StringBuilder report = new StringBuilder();
        report.append("LOW ATTENDANCE STUDENTS REPORT\n");
        report.append("=" .repeat(40)).append("\n");
        report.append(String.format("Period: %s to %s\n", event.getFromDate(), event.getToDate()));
        report.append(String.format("Generated: %s\n\n", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)));
        
        report.append("STUDENTS WITH LOW ATTENDANCE:\n");
        report.append("-".repeat(30)).append("\n");
        report.append("(This would contain actual low attendance students in production)\n");
        
        return report.toString();
    }

    /**
     * Generate monthly summary report
     */
    private String generateMonthlySummaryReport(AttendanceEvents.AttendanceReportRequestedEvent event) {
        StringBuilder report = new StringBuilder();
        report.append("MONTHLY ATTENDANCE SUMMARY\n");
        report.append("=" .repeat(35)).append("\n");
        // Implementation for monthly summary
        return report.toString();
    }

    /**
     * Generate detailed attendance report
     */
    private String generateDetailedAttendanceReport(AttendanceEvents.AttendanceReportRequestedEvent event) {
        StringBuilder report = new StringBuilder();
        report.append("DETAILED ATTENDANCE REPORT\n");
        report.append("=" .repeat(35)).append("\n");
        // Implementation for detailed report
        return report.toString();
    }

    /**
     * Generate generic report
     */
    private String generateGenericReport(AttendanceEvents.AttendanceReportRequestedEvent event) {
        return "Generic report content for type: " + event.getReportType();
    }

    // ========== HELPER METHODS ==========

    /**
     * Save report to storage (file system/cloud)
     */
    private void saveReportToStorage(AttendanceEvents.AttendanceReportRequestedEvent event, String reportContent) {
        String fileName = String.format("report_%s_%s_%s.txt", 
                event.getReportType(), 
                event.getFromDate().toString().replace("-", ""),
                System.currentTimeMillis());
        
        log.info("Saving report to storage: {}, Size: {} chars", fileName, reportContent.length());
        
        // In production, this would:
        // 1. Save to local file system or cloud storage (AWS S3, etc.)
        // 2. Store metadata in database
        // 3. Generate download links
        // 4. Set expiration policies
    }

    /**
     * Notify user that report is ready
     */
    private void notifyReportCompletion(AttendanceEvents.AttendanceReportRequestedEvent event, int reportSize) {
        log.info("Notifying user {} that report {} is ready (Size: {} chars)", 
                event.getRequestedBy(), event.getReportType(), reportSize);
        
        // In production, this would:
        // 1. Send email notification with download link
        // 2. Update UI with completion status
        // 3. Add to user's report history
    }

    /**
     * Notify user of report generation failure
     */
    private void notifyReportFailure(AttendanceEvents.AttendanceReportRequestedEvent event, String errorMessage) {
        log.error("Notifying user {} of report generation failure: {}", 
                event.getRequestedBy(), errorMessage);
        
        // In production, this would:
        // 1. Send error notification to user
        // 2. Log error for admin review
        // 3. Provide retry options
    }
}
