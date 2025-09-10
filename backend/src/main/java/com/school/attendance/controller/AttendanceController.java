package com.school.attendance.controller;

import com.school.attendance.entity.AttendanceRecord;
import com.school.attendance.entity.AttendanceRecord.AttendanceStatus;
import com.school.attendance.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Attendance Management", description = "APIs for managing student attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/mark")
    @Operation(summary = "Mark attendance for a student", description = "Marks attendance for a single student")
    public ResponseEntity<AttendanceRecord> markAttendance(
            @Valid @RequestBody AttendanceMarkRequest request) {
        try {
            AttendanceRecord record = attendanceService.markAttendance(
                request.getStudentId(),
                request.getDate(),
                request.getStatus(),
                request.getNote(),
                request.getTeacherId()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(record);
        } catch (IllegalArgumentException e) {
            log.error("Error marking attendance: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/bulk-mark")
    @Operation(summary = "Mark attendance for multiple students", description = "Marks attendance for multiple students in one operation")
    public ResponseEntity<BulkAttendanceResponse> bulkMarkAttendance(
            @Valid @RequestBody BulkAttendanceMarkRequest request) {
        try {
            List<AttendanceRecord> records = attendanceService.bulkMarkAttendance(
                request.getAttendanceRequests(),
                request.getTeacherId()
            );
            
            BulkAttendanceResponse response = new BulkAttendanceResponse(
                request.getAttendanceRequests().size(),
                records.size(),
                request.getAttendanceRequests().size() - records.size(),
                "Attendance marked successfully"
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error in bulk attendance marking: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get student attendance", description = "Retrieves attendance records for a specific student")
    public ResponseEntity<List<AttendanceRecord>> getStudentAttendance(
            @Parameter(description = "Student ID") @PathVariable Long studentId,
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<AttendanceRecord> records = attendanceService.getStudentAttendanceHistory(studentId, startDate, endDate);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/student/{studentId}/date/{date}")
    @Operation(summary = "Get attendance for specific date", description = "Gets attendance record for a student on a specific date")
    public ResponseEntity<AttendanceRecord> getAttendanceByDate(
            @Parameter(description = "Student ID") @PathVariable Long studentId,
            @Parameter(description = "Date") @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        Optional<AttendanceRecord> record = attendanceService.getAttendanceRecord(studentId, date);
        return record.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/class")
    @Operation(summary = "Get class attendance", description = "Retrieves attendance for all students in a class on a specific date")
    public ResponseEntity<List<AttendanceRecord>> getClassAttendance(
            @Parameter(description = "School ID") @RequestParam Long schoolId,
            @Parameter(description = "Standard/Grade") @RequestParam String standard,
            @Parameter(description = "Section") @RequestParam(required = false) String section,
            @Parameter(description = "Date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        List<AttendanceRecord> records = attendanceService.getClassAttendance(schoolId, standard, section, date);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/statistics/student/{studentId}")
    @Operation(summary = "Get student attendance statistics", description = "Gets attendance statistics for a student")
    public ResponseEntity<AttendanceService.AttendanceStatistics> getStudentStatistics(
            @Parameter(description = "Student ID") @PathVariable Long studentId,
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        AttendanceService.AttendanceStatistics stats = attendanceService.getStudentAttendanceStatistics(studentId, startDate, endDate);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/summary/daily")
    @Operation(summary = "Get daily attendance summary", description = "Gets attendance summary for all classes on a specific date")
    public ResponseEntity<List<AttendanceService.DailyAttendanceSummary>> getDailyAttendanceSummary(
            @Parameter(description = "School ID") @RequestParam Long schoolId,
            @Parameter(description = "Date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        List<AttendanceService.DailyAttendanceSummary> summary = attendanceService.getDailyAttendanceSummary(schoolId, date);
        return ResponseEntity.ok(summary);
    }

    @PutMapping("/{recordId}")
    @Operation(summary = "Update attendance record", description = "Updates an existing attendance record")
    public ResponseEntity<AttendanceRecord> updateAttendanceRecord(
            @Parameter(description = "Record ID") @PathVariable Long recordId,
            @Valid @RequestBody AttendanceUpdateRequest request) {
        try {
            // For simplicity, we'll delete and recreate the record
            // In a real implementation, you might want a proper update method
            attendanceService.deleteAttendanceRecord(recordId);
            
            AttendanceRecord newRecord = attendanceService.markAttendance(
                request.getStudentId(),
                request.getDate(),
                request.getStatus(),
                request.getNote(),
                request.getTeacherId()
            );
            
            return ResponseEntity.ok(newRecord);
        } catch (IllegalArgumentException e) {
            log.error("Error updating attendance: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{recordId}")
    @Operation(summary = "Delete attendance record", description = "Deletes an attendance record")
    public ResponseEntity<Void> deleteAttendanceRecord(
            @Parameter(description = "Record ID") @PathVariable Long recordId) {
        try {
            attendanceService.deleteAttendanceRecord(recordId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("Error deleting attendance: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/holiday")
    @Operation(summary = "Mark holiday", description = "Marks a holiday for all students in a school")
    public ResponseEntity<HolidayResponse> markHoliday(
            @Valid @RequestBody HolidayMarkRequest request) {
        try {
            attendanceService.markHoliday(request.getSchoolId(), request.getDate(), request.getReason());
            HolidayResponse response = new HolidayResponse("Holiday marked successfully for " + request.getDate());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error marking holiday: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/records")
    @Operation(summary = "Get attendance records", description = "Gets attendance records with pagination")
    public ResponseEntity<Page<AttendanceRecord>> getAttendanceRecords(
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        Page<AttendanceRecord> records = attendanceService.getAttendanceRecords(startDate, endDate, pageable);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/exists")
    @Operation(summary = "Check if attendance exists", description = "Checks if attendance record exists for a student on a date")
    public ResponseEntity<AttendanceExistsResponse> checkAttendanceExists(
            @Parameter(description = "Student ID") @RequestParam Long studentId,
            @Parameter(description = "Date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        boolean exists = attendanceService.attendanceExistsForStudent(studentId, date);
        AttendanceExistsResponse response = new AttendanceExistsResponse(exists, studentId, date);
        return ResponseEntity.ok(response);
    }

    // Request DTOs
    public static class AttendanceMarkRequest {
        private Long studentId;
        private LocalDate date;
        private AttendanceStatus status;
        private String note;
        private Long teacherId;

        // Constructors, getters, and setters
        public AttendanceMarkRequest() {}

        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }
        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        public AttendanceStatus getStatus() { return status; }
        public void setStatus(AttendanceStatus status) { this.status = status; }
        public String getNote() { return note; }
        public void setNote(String note) { this.note = note; }
        public Long getTeacherId() { return teacherId; }
        public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
    }

    public static class BulkAttendanceMarkRequest {
        private List<AttendanceService.BulkAttendanceRequest> attendanceRequests;
        private Long teacherId;

        public List<AttendanceService.BulkAttendanceRequest> getAttendanceRequests() { return attendanceRequests; }
        public void setAttendanceRequests(List<AttendanceService.BulkAttendanceRequest> attendanceRequests) { 
            this.attendanceRequests = attendanceRequests; 
        }
        public Long getTeacherId() { return teacherId; }
        public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
    }

    public static class AttendanceUpdateRequest {
        private Long studentId;
        private LocalDate date;
        private AttendanceStatus status;
        private String note;
        private Long teacherId;

        // Getters and setters
        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }
        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        public AttendanceStatus getStatus() { return status; }
        public void setStatus(AttendanceStatus status) { this.status = status; }
        public String getNote() { return note; }
        public void setNote(String note) { this.note = note; }
        public Long getTeacherId() { return teacherId; }
        public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
    }

    public static class HolidayMarkRequest {
        private Long schoolId;
        private LocalDate date;
        private String reason;

        // Getters and setters
        public Long getSchoolId() { return schoolId; }
        public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }
        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }

    // Response DTOs
    public static class BulkAttendanceResponse {
        private int totalRecords;
        private int successCount;
        private int failureCount;
        private String message;

        public BulkAttendanceResponse(int totalRecords, int successCount, int failureCount, String message) {
            this.totalRecords = totalRecords;
            this.successCount = successCount;
            this.failureCount = failureCount;
            this.message = message;
        }

        // Getters
        public int getTotalRecords() { return totalRecords; }
        public int getSuccessCount() { return successCount; }
        public int getFailureCount() { return failureCount; }
        public String getMessage() { return message; }
    }

    public static class HolidayResponse {
        private String message;

        public HolidayResponse(String message) {
            this.message = message;
        }

        public String getMessage() { return message; }
    }

    public static class AttendanceExistsResponse {
        private boolean exists;
        private Long studentId;
        private LocalDate date;

        public AttendanceExistsResponse(boolean exists, Long studentId, LocalDate date) {
            this.exists = exists;
            this.studentId = studentId;
            this.date = date;
        }

        public boolean isExists() { return exists; }
        public Long getStudentId() { return studentId; }
        public LocalDate getDate() { return date; }
    }
}
