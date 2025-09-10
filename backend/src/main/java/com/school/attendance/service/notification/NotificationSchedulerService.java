package com.school.attendance.service.notification;

import com.school.attendance.entity.AttendanceRecord;
import com.school.attendance.entity.Student;
import com.school.attendance.repository.AttendanceRecordRepository;
import com.school.attendance.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class NotificationSchedulerService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationSchedulerService.class);
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired(required = false)
    private StudentRepository studentRepository;
    
    @Autowired(required = false)
    private AttendanceRecordRepository attendanceRecordRepository;

    /**
     * Daily scheduler to check absentees and send notifications
     * Runs every day at 8 PM (20:00)
     */
    @Scheduled(cron = "0 0 20 * * *")
    public void sendDailyAbsenteeAlerts() {
        logger.info("Starting daily absentee alert job");
        
        try {
            if (studentRepository == null || attendanceRecordRepository == null) {
                logger.info("Repository dependencies not available, using mock notifications");
                sendMockDailyAlerts();
                return;
            }
            
            LocalDate today = LocalDate.now();
            
            // Get all absent students for today
            List<AttendanceRecord> todayAbsences = attendanceRecordRepository
                .findByDateAndStatus(today, AttendanceRecord.AttendanceStatus.ABSENT);
            
            logger.info("Found {} absent students for today ({})", todayAbsences.size(), today);
            
            for (AttendanceRecord record : todayAbsences) {
                Student student = record.getStudent();
                if (student != null && student.getIsActive()) {
                    notificationService.sendDailyAbsenteeAlert(
                        student, 
                        student.getParentEmail(), 
                        student.getParentMobile()
                    );
                }
            }
            
            logger.info("Completed daily absentee alert job");
            
        } catch (Exception e) {
            logger.error("Error in daily absentee alert job: {}", e.getMessage(), e);
        }
    }

    /**
     * Weekly scheduler to check low attendance and send alerts
     * Runs every Sunday at 7 PM (19:00)
     */
    @Scheduled(cron = "0 0 19 * * SUN")
    public void sendLowAttendanceAlerts() {
        logger.info("Starting low attendance alert job");
        
        try {
            if (studentRepository == null || attendanceRecordRepository == null) {
                logger.info("Repository dependencies not available, using mock notifications");
                sendMockLowAttendanceAlerts();
                return;
            }
            
            List<Student> allStudents = studentRepository.findByIsActiveTrue();
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(30); // Last 30 days
            
            for (Student student : allStudents) {
                List<AttendanceRecord> records = attendanceRecordRepository
                    .findByStudentAndDateBetween(student, startDate, endDate);
                
                if (!records.isEmpty()) {
                    long presentDays = records.stream()
                        .mapToLong(r -> r.getStatus() == AttendanceRecord.AttendanceStatus.PRESENT ? 1 : 0)
                        .sum();
                    
                    double attendancePercentage = (double) presentDays / records.size() * 100;
                    
                    notificationService.sendLowAttendanceAlert(
                        student, 
                        attendancePercentage, 
                        student.getParentEmail(), 
                        student.getParentMobile()
                    );
                }
            }
            
            logger.info("Completed low attendance alert job");
            
        } catch (Exception e) {
            logger.error("Error in low attendance alert job: {}", e.getMessage(), e);
        }
    }

    /**
     * Manual trigger for testing purposes
     */
    public void triggerDailyAlerts() {
        logger.info("Manual trigger for daily alerts");
        sendDailyAbsenteeAlerts();
    }

    /**
     * Manual trigger for low attendance alerts
     */
    public void triggerLowAttendanceAlerts() {
        logger.info("Manual trigger for low attendance alerts");
        sendLowAttendanceAlerts();
    }

    /**
     * Send mock daily alerts for testing
     */
    private void sendMockDailyAlerts() {
        logger.info("Sending mock daily absentee alerts");
        
        // Mock absent students
        String[] mockAbsentees = {
            "John Doe", "Jane Smith", "Mike Johnson"
        };
        
        String[] mockEmails = {
            "john.parent@email.com", "jane.parent@email.com", "mike.parent@email.com"
        };
        
        String[] mockPhones = {
            "+91-9876543210", "+91-9876543211", "+91-9876543212"
        };
        
        for (int i = 0; i < mockAbsentees.length; i++) {
            String subject = "Daily Attendance Alert - " + mockAbsentees[i];
            String message = String.format(
                "Dear Parent,\n\n" +
                "This is to inform you that your child %s was marked absent today (%s).\n\n" +
                "If this is an error, please contact the school immediately.\n\n" +
                "Thank you,\n" +
                "School Administration",
                mockAbsentees[i],
                LocalDate.now().toString()
            );
            
            notificationService.sendEmail(mockEmails[i], subject, message);
            
            String smsMessage = String.format(
                "ATTENDANCE ALERT: %s was absent today. Contact school if this is an error. - School",
                mockAbsentees[i].split(" ")[0]
            );
            notificationService.sendSms(mockPhones[i], smsMessage);
        }
        
        logger.info("Completed mock daily alerts");
    }

    /**
     * Send mock low attendance alerts for testing
     */
    private void sendMockLowAttendanceAlerts() {
        logger.info("Sending mock low attendance alerts");
        
        String[] mockStudents = {
            "Alice Brown", "Bob Wilson"
        };
        
        String[] mockEmails = {
            "alice.parent@email.com", "bob.parent@email.com"
        };
        
        String[] mockPhones = {
            "+91-9876543213", "+91-9876543214"
        };
        
        double[] mockAttendance = {65.5, 72.3};
        
        for (int i = 0; i < mockStudents.length; i++) {
            String subject = "Low Attendance Alert - " + mockStudents[i];
            String message = String.format(
                "Dear Parent,\n\n" +
                "This is to inform you that your child %s has low attendance.\n\n" +
                "Current Attendance: %.1f%%\n" +
                "Required Minimum: 75.0%%\n\n" +
                "Please ensure regular attendance to avoid academic issues.\n\n" +
                "Thank you,\n" +
                "School Administration",
                mockStudents[i],
                mockAttendance[i]
            );
            
            notificationService.sendEmail(mockEmails[i], subject, message);
            
            String smsMessage = String.format(
                "LOW ATTENDANCE: %s has %.1f%% attendance (Required: 75%%). Please ensure regular attendance.",
                mockStudents[i].split(" ")[0],
                mockAttendance[i]
            );
            notificationService.sendSms(mockPhones[i], smsMessage);
        }
        
        logger.info("Completed mock low attendance alerts");
    }
}
