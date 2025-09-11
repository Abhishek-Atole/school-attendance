package com.school.attendance.demo;

import com.school.attendance.service.async.AsyncEventPublisherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Demo class to showcase async processing functionality
 * Only runs in 'demo' profile to avoid interfering with normal operations
 */
@Component
@Profile("demo")
@RequiredArgsConstructor
@Slf4j
public class AsyncProcessingDemo implements CommandLineRunner {

    private final AsyncEventPublisherService eventPublisherService;

    @Override
    public void run(String... args) throws Exception {
        log.info("🚀 Starting Async Processing Demo...");
        
        // Demo 1: Single attendance marking with notification
        demonstrateAttendanceNotification();
        
        // Demo 2: Bulk attendance processing
        demonstrateBulkAttendanceProcessing();
        
        // Demo 3: Report generation
        demonstrateReportGeneration();
        
        // Demo 4: Low attendance alerts
        demonstrateLowAttendanceAlerts();
        
        // Demo 5: Daily summary processing
        demonstrateDailySummaryProcessing();
        
        log.info("✅ Async Processing Demo completed! Check logs for async processing results.");
        log.info("📊 All operations are running in background threads - notifications and reports are being processed asynchronously.");
    }

    private void demonstrateAttendanceNotification() throws InterruptedException {
        log.info("\n🔔 Demo 1: Async Attendance Notifications");
        log.info("================================================");
        
        // Simulate marking attendance for different students
        eventPublisherService.notifyAttendanceMarked(1L, "ABSENT", LocalDate.now(), "Student was sick");
        eventPublisherService.notifyAttendanceMarked(2L, "LATE", LocalDate.now(), "Traffic delay");
        eventPublisherService.notifyAttendanceMarked(3L, "PRESENT", LocalDate.now(), null);
        
        log.info("✅ Attendance notifications queued for async processing");
        
        // Small delay to show async processing
        Thread.sleep(1000);
    }

    private void demonstrateBulkAttendanceProcessing() throws InterruptedException {
        log.info("\n📊 Demo 2: Bulk Attendance Processing");
        log.info("=====================================");
        
        // Simulate bulk attendance for a class
        eventPublisherService.notifyBulkAttendance(101L, LocalDate.now(), 30, 25, 5);
        eventPublisherService.notifyBulkAttendance(102L, LocalDate.now(), 28, 26, 2);
        
        log.info("✅ Bulk attendance processing queued for async handling");
        
        Thread.sleep(1500);
    }

    private void demonstrateReportGeneration() throws InterruptedException {
        log.info("\n📈 Demo 3: Async Report Generation");
        log.info("===================================");
        
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(7);
        
        // Generate different types of reports asynchronously
        eventPublisherService.generateDailyReport(1L, today, 100L, "admin@school.com");
        eventPublisherService.generateStudentReport(1L, weekAgo, today, 100L, "teacher@school.com", 1L);
        eventPublisherService.generateClassReport(101L, weekAgo, today, 100L, "principal@school.com", 1L);
        
        log.info("✅ Report generation requests queued for async processing");
        
        Thread.sleep(2000);
    }

    private void demonstrateLowAttendanceAlerts() throws InterruptedException {
        log.info("\n⚠️  Demo 4: Low Attendance Alerts");
        log.info("=================================");
        
        LocalDate monthAgo = LocalDate.now().minusDays(30);
        LocalDate today = LocalDate.now();
        
        // Check for low attendance students
        eventPublisherService.checkLowAttendanceStudents(1L, monthAgo, today, 75.0);
        
        log.info("✅ Low attendance check initiated - alerts will be sent asynchronously");
        
        Thread.sleep(1200);
    }

    private void demonstrateDailySummaryProcessing() throws InterruptedException {
        log.info("\n📋 Demo 5: Daily Summary Processing");
        log.info("====================================");
        
        // Process end-of-day summaries
        eventPublisherService.publishEndOfDaySummary(1L, LocalDate.now());
        eventPublisherService.processPendingNotifications(1L, LocalDate.now());
        
        log.info("✅ Daily summary and pending notifications processing initiated");
        
        Thread.sleep(2500);
    }
}
