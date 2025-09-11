package com.school.attendance.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Events for attendance-related operations
 * Used to trigger async processing for notifications and analytics
 */
public class AttendanceEvents {

    /**
     * Event fired when attendance is marked for a student
     */
    @Getter
    @EqualsAndHashCode(callSuper = false)
    public static class AttendanceMarkedEvent extends ApplicationEvent {
        private final Long studentId;
        private final Long teacherId;
        private final LocalDate attendanceDate;
        private final String attendanceStatus;
        private final String note;
        private final LocalDateTime eventTimestamp;

        public AttendanceMarkedEvent(Object source, Long studentId, Long teacherId, 
                                   LocalDate attendanceDate, String attendanceStatus, String note) {
            super(source);
            this.studentId = studentId;
            this.teacherId = teacherId;
            this.attendanceDate = attendanceDate;
            this.attendanceStatus = attendanceStatus;
            this.note = note;
            this.eventTimestamp = LocalDateTime.now();
        }
    }

    /**
     * Event fired when bulk attendance is marked for a class
     */
    @Getter
    @EqualsAndHashCode(callSuper = false)
    public static class BulkAttendanceMarkedEvent extends ApplicationEvent {
        private final Long classId;
        private final Long teacherId;
        private final LocalDate attendanceDate;
        private final int totalStudents;
        private final int presentCount;
        private final int absentCount;
        private final LocalDateTime eventTimestamp;

        public BulkAttendanceMarkedEvent(Object source, Long classId, Long teacherId, 
                                       LocalDate attendanceDate, int totalStudents, 
                                       int presentCount, int absentCount) {
            super(source);
            this.classId = classId;
            this.teacherId = teacherId;
            this.attendanceDate = attendanceDate;
            this.totalStudents = totalStudents;
            this.presentCount = presentCount;
            this.absentCount = absentCount;
            this.eventTimestamp = LocalDateTime.now();
        }
    }

    /**
     * Event fired when a student's attendance drops below threshold
     */
    @Getter
    @EqualsAndHashCode(callSuper = false)
    public static class LowAttendanceAlert extends ApplicationEvent {
        private final Long studentId;
        private final String studentName;
        private final String grNo;
        private final double attendancePercentage;
        private final double threshold;
        private final LocalDate fromDate;
        private final LocalDate toDate;
        private final LocalDateTime eventTimestamp;

        public LowAttendanceAlert(Object source, Long studentId, String studentName, String grNo,
                                double attendancePercentage, double threshold, 
                                LocalDate fromDate, LocalDate toDate) {
            super(source);
            this.studentId = studentId;
            this.studentName = studentName;
            this.grNo = grNo;
            this.attendancePercentage = attendancePercentage;
            this.threshold = threshold;
            this.fromDate = fromDate;
            this.toDate = toDate;
            this.eventTimestamp = LocalDateTime.now();
        }
    }

    /**
     * Event fired when attendance report is requested
     */
    @Getter
    @EqualsAndHashCode(callSuper = false)
    public static class AttendanceReportRequestedEvent extends ApplicationEvent {
        private final String reportType; // DAILY, WEEKLY, MONTHLY, CUSTOM
        private final String format; // CSV, EXCEL, PDF
        private final Long requestedBy; // User ID
        private final String email; // Email to send report
        private final LocalDate fromDate;
        private final LocalDate toDate;
        private final Long schoolId;
        private final Long classId; // Optional - for class-specific reports
        private final LocalDateTime eventTimestamp;

        public AttendanceReportRequestedEvent(Object source, String reportType, String format,
                                            Long requestedBy, String email, LocalDate fromDate, 
                                            LocalDate toDate, Long schoolId, Long classId) {
            super(source);
            this.reportType = reportType;
            this.format = format;
            this.requestedBy = requestedBy;
            this.email = email;
            this.fromDate = fromDate;
            this.toDate = toDate;
            this.schoolId = schoolId;
            this.classId = classId;
            this.eventTimestamp = LocalDateTime.now();
        }
    }

    /**
     * Event fired when daily attendance summary is completed
     */
    @Getter
    @EqualsAndHashCode(callSuper = false)
    public static class DailyAttendanceSummaryEvent extends ApplicationEvent {
        private final Long schoolId;
        private final LocalDate date;
        private final int totalStudents;
        private final int presentStudents;
        private final int absentStudents;
        private final int lateStudents;
        private final double attendancePercentage;
        private final LocalDateTime eventTimestamp;

        public DailyAttendanceSummaryEvent(Object source, Long schoolId, LocalDate date,
                                         int totalStudents, int presentStudents, int absentStudents,
                                         int lateStudents, double attendancePercentage) {
            super(source);
            this.schoolId = schoolId;
            this.date = date;
            this.totalStudents = totalStudents;
            this.presentStudents = presentStudents;
            this.absentStudents = absentStudents;
            this.lateStudents = lateStudents;
            this.attendancePercentage = attendancePercentage;
            this.eventTimestamp = LocalDateTime.now();
        }
    }
}
