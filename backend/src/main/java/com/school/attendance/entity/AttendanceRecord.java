package com.school.attendance.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "attendance_records", 
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"date", "student_id"})
       })
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Date is required")
    @PastOrPresent(message = "Date cannot be in the future")
    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Attendance status is required")
    @Column(nullable = false)
    private AttendanceStatus status;

    @Column(length = 500)
    private String note;

    @Column(name = "marked_time")
    private LocalTime markedTime;

    @Column(name = "is_holiday", nullable = false)
    private Boolean isHoliday = false;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Many-to-One relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher; // Optional: who marked the attendance

    // Constructor without ID for creation
    public AttendanceRecord(LocalDate date, AttendanceStatus status, String note,
                           LocalTime markedTime, Student student, Teacher teacher) {
        this.date = date;
        this.status = status;
        this.note = note;
        this.markedTime = markedTime;
        this.student = student;
        this.teacher = teacher;
        this.isHoliday = false;
    }

    // Constructor for holiday marking
    public AttendanceRecord(LocalDate date, Student student, String note) {
        this.date = date;
        this.status = AttendanceStatus.HOLIDAY;
        this.note = note;
        this.student = student;
        this.isHoliday = true;
        this.markedTime = LocalTime.now();
    }

    public enum AttendanceStatus {
        PRESENT("Present"),
        ABSENT("Absent"),
        LATE("Late"),
        HALF_DAY("Half Day"),
        HOLIDAY("Holiday"),
        SICK_LEAVE("Sick Leave");

        private final String displayName;

        AttendanceStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
