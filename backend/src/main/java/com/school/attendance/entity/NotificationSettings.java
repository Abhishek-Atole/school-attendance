package com.school.attendance.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_settings")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email_enabled", nullable = false)
    private Boolean emailEnabled = true;

    @Column(name = "sms_enabled", nullable = false)
    private Boolean smsEnabled = true;

    @Column(name = "daily_absentee_alerts", nullable = false)
    private Boolean dailyAbsenteeAlerts = true;

    @Column(name = "low_attendance_alerts", nullable = false)
    private Boolean lowAttendanceAlerts = true;

    @Column(name = "holiday_notifications", nullable = false)
    private Boolean holidayNotifications = true;

    @Column(name = "attendance_threshold", nullable = false)
    private Double attendanceThreshold = 75.0;

    @Column(name = "notification_time")
    private String notificationTime = "20:00"; // 8 PM default

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    // Constructor with defaults
    public NotificationSettings(School school) {
        this.school = school;
        this.emailEnabled = true;
        this.smsEnabled = true;
        this.dailyAbsenteeAlerts = true;
        this.lowAttendanceAlerts = true;
        this.holidayNotifications = true;
        this.attendanceThreshold = 75.0;
        this.notificationTime = "20:00";
    }
}
