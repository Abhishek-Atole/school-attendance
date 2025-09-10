package com.school.attendance.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_logs")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private String recipient;

    @Column(nullable = false)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status;

    @Column(name = "error_message")
    private String errorMessage;

    @CreatedDate
    @Column(name = "sent_at", updatable = false)
    private LocalDateTime sentAt;

    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "school_id")
    private Long schoolId;

    public enum NotificationType {
        EMAIL, SMS
    }

    public enum NotificationStatus {
        SUCCESS, FAILED, PENDING
    }

    // Constructor for successful notifications
    public NotificationLog(NotificationType type, String recipient, String subject, 
                          String message, Long studentId, Long schoolId) {
        this.type = type;
        this.recipient = recipient;
        this.subject = subject;
        this.message = message;
        this.status = NotificationStatus.SUCCESS;
        this.studentId = studentId;
        this.schoolId = schoolId;
    }

    // Constructor for failed notifications
    public NotificationLog(NotificationType type, String recipient, String subject, 
                          String message, String errorMessage, Long studentId, Long schoolId) {
        this.type = type;
        this.recipient = recipient;
        this.subject = subject;
        this.message = message;
        this.status = NotificationStatus.FAILED;
        this.errorMessage = errorMessage;
        this.studentId = studentId;
        this.schoolId = schoolId;
    }
}
