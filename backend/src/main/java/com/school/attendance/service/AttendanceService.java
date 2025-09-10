package com.school.attendance.service;

import com.school.attendance.entity.AttendanceRecord;
import com.school.attendance.entity.AttendanceRecord.AttendanceStatus;
import com.school.attendance.entity.Student;
import com.school.attendance.entity.Teacher;
import com.school.attendance.repository.AttendanceRecordRepository;
import com.school.attendance.repository.StudentRepository;
import com.school.attendance.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AttendanceService {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    /**
     * Mark attendance for a single student
     */
    public AttendanceRecord markAttendance(Long studentId, LocalDate date, AttendanceStatus status, 
                                         String note, Long teacherId) {
        log.info("Marking attendance for student ID: {} on date: {} with status: {}", 
                studentId, date, status);
        
        // Validate student exists
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student with ID " + studentId + " not found"));
        
        // Validate teacher if provided
        Teacher teacher = null;
        if (teacherId != null) {
            teacher = teacherRepository.findById(teacherId)
                    .orElseThrow(() -> new IllegalArgumentException("Teacher with ID " + teacherId + " not found"));
        }
        
        // Check if attendance already exists for this student on this date
        Optional<AttendanceRecord> existingRecord = attendanceRecordRepository.findByStudentIdAndDate(studentId, date);
        
        if (existingRecord.isPresent()) {
            // Update existing record
            AttendanceRecord record = existingRecord.get();
            record.setStatus(status);
            record.setNote(note);
            record.setMarkedTime(LocalTime.now());
            record.setTeacher(teacher);
            
            AttendanceRecord savedRecord = attendanceRecordRepository.save(record);
            log.info("Updated existing attendance record with ID: {}", savedRecord.getId());
            return savedRecord;
        } else {
            // Create new record
            AttendanceRecord newRecord = new AttendanceRecord(date, status, note, LocalTime.now(), student, teacher);
            AttendanceRecord savedRecord = attendanceRecordRepository.save(newRecord);
            log.info("Created new attendance record with ID: {}", savedRecord.getId());
            return savedRecord;
        }
    }

    /**
     * Mark attendance for multiple students (bulk operation)
     */
    public List<AttendanceRecord> bulkMarkAttendance(List<BulkAttendanceRequest> requests, Long teacherId) {
        log.info("Bulk marking attendance for {} students", requests.size());
        
        // Validate teacher if provided
        Teacher teacher = null;
        if (teacherId != null) {
            teacher = teacherRepository.findById(teacherId)
                    .orElseThrow(() -> new IllegalArgumentException("Teacher with ID " + teacherId + " not found"));
        }
        
        List<AttendanceRecord> records = new java.util.ArrayList<>();
        
        for (BulkAttendanceRequest request : requests) {
            try {
                AttendanceRecord record = markAttendance(
                    request.getStudentId(), 
                    request.getDate(), 
                    request.getStatus(), 
                    request.getNote(), 
                    teacherId
                );
                records.add(record);
            } catch (Exception e) {
                log.error("Failed to mark attendance for student ID: {} - {}", request.getStudentId(), e.getMessage());
                // Continue with other students, don't fail the entire operation
            }
        }
        
        log.info("Successfully marked attendance for {} out of {} students", records.size(), requests.size());
        return records;
    }

    /**
     * Get attendance record by student and date
     */
    @Transactional(readOnly = true)
    public Optional<AttendanceRecord> getAttendanceRecord(Long studentId, LocalDate date) {
        return attendanceRecordRepository.findByStudentIdAndDate(studentId, date);
    }

    /**
     * Get student attendance history
     */
    @Transactional(readOnly = true)
    public List<AttendanceRecord> getStudentAttendanceHistory(Long studentId, LocalDate startDate, LocalDate endDate) {
        return attendanceRecordRepository.findByStudentIdAndDateBetweenOrderByDateDesc(studentId, startDate, endDate);
    }

    /**
     * Get class attendance for a specific date
     */
    @Transactional(readOnly = true)
    public List<AttendanceRecord> getClassAttendance(Long schoolId, String standard, String section, LocalDate date) {
        return attendanceRecordRepository.findClassAttendanceByDate(schoolId, standard, section, date);
    }

    /**
     * Get attendance statistics for a student
     */
    @Transactional(readOnly = true)
    public AttendanceStatistics getStudentAttendanceStatistics(Long studentId, LocalDate startDate, LocalDate endDate) {
        List<Object[]> rawStats = attendanceRecordRepository.getStudentAttendanceStatistics(studentId, startDate, endDate);
        
        Map<AttendanceStatus, Long> statusCounts = new HashMap<>();
        long totalDays = 0;
        
        for (Object[] row : rawStats) {
            AttendanceStatus status = (AttendanceStatus) row[0];
            Long count = (Long) row[1];
            statusCounts.put(status, count);
            totalDays += count;
        }
        
        long presentDays = statusCounts.getOrDefault(AttendanceStatus.PRESENT, 0L) + 
                          statusCounts.getOrDefault(AttendanceStatus.LATE, 0L);
        long absentDays = statusCounts.getOrDefault(AttendanceStatus.ABSENT, 0L);
        long halfDays = statusCounts.getOrDefault(AttendanceStatus.HALF_DAY, 0L);
        long sickLeaveDays = statusCounts.getOrDefault(AttendanceStatus.SICK_LEAVE, 0L);
        
        double attendancePercentage = totalDays > 0 ? (presentDays * 100.0 / totalDays) : 0.0;
        
        return new AttendanceStatistics(totalDays, presentDays, absentDays, halfDays, sickLeaveDays, attendancePercentage);
    }

    /**
     * Get daily attendance summary for a school
     */
    @Transactional(readOnly = true)
    public List<DailyAttendanceSummary> getDailyAttendanceSummary(Long schoolId, LocalDate date) {
        List<Object[]> rawData = attendanceRecordRepository.getDailyAttendanceSummary(schoolId, date);
        
        Map<String, DailyAttendanceSummary> summaryMap = new HashMap<>();
        
        for (Object[] row : rawData) {
            String standard = (String) row[0];
            String section = (String) row[1];
            AttendanceStatus status = (AttendanceStatus) row[2];
            Long count = (Long) row[3];
            
            String classKey = standard + "-" + (section != null ? section : "");
            DailyAttendanceSummary summary = summaryMap.computeIfAbsent(classKey, 
                k -> new DailyAttendanceSummary(standard, section, 0L, 0L, 0L, 0L, 0L, 0.0));
            
            switch (status) {
                case PRESENT -> summary.setPresentCount(summary.getPresentCount() + count);
                case ABSENT -> summary.setAbsentCount(summary.getAbsentCount() + count);
                case LATE -> summary.setLateCount(summary.getLateCount() + count);
                case HALF_DAY -> summary.setHalfDayCount(summary.getHalfDayCount() + count);
                case SICK_LEAVE -> summary.setSickLeaveCount(summary.getSickLeaveCount() + count);
            }
            
            summary.setTotalStudents(summary.getTotalStudents() + count);
        }
        
        // Calculate attendance percentages
        summaryMap.values().forEach(summary -> {
            long presentCount = summary.getPresentCount() + summary.getLateCount();
            double percentage = summary.getTotalStudents() > 0 ? 
                (presentCount * 100.0 / summary.getTotalStudents()) : 0.0;
            summary.setAttendancePercentage(percentage);
        });
        
        return summaryMap.values().stream().toList();
    }

    /**
     * Mark holiday for all students in a school
     */
    public void markHoliday(Long schoolId, LocalDate date, String reason) {
        log.info("Marking holiday for school ID: {} on date: {}", schoolId, date);
        
        List<Student> students = studentRepository.findBySchoolIdAndIsActiveTrue(schoolId, 
                org.springframework.data.domain.Pageable.unpaged()).getContent();
        
        for (Student student : students) {
            // Check if attendance already exists
            if (!attendanceRecordRepository.existsByStudentIdAndDate(student.getId(), date)) {
                AttendanceRecord holidayRecord = new AttendanceRecord(date, student, reason);
                attendanceRecordRepository.save(holidayRecord);
            }
        }
        
        log.info("Successfully marked holiday for {} students", students.size());
    }

    /**
     * Delete attendance record
     */
    public void deleteAttendanceRecord(Long recordId) {
        log.info("Deleting attendance record with ID: {}", recordId);
        
        if (!attendanceRecordRepository.existsById(recordId)) {
            throw new IllegalArgumentException("Attendance record with ID " + recordId + " not found");
        }
        
        attendanceRecordRepository.deleteById(recordId);
        log.info("Successfully deleted attendance record with ID: {}", recordId);
    }

    /**
     * Get attendance records with pagination
     */
    @Transactional(readOnly = true)
    public Page<AttendanceRecord> getAttendanceRecords(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return attendanceRecordRepository.findByDateBetweenOrderByDateDesc(startDate, endDate, pageable);
    }

    /**
     * Check if student attendance exists for a date
     */
    @Transactional(readOnly = true)
    public boolean attendanceExistsForStudent(Long studentId, LocalDate date) {
        return attendanceRecordRepository.existsByStudentIdAndDate(studentId, date);
    }

    /**
     * Mark daily attendance for all students in a school
     * By default all students are present, only absent ones are specified
     */
    public DailyAttendanceResult markDailyAttendance(Long schoolId, LocalDate date, 
                                                   List<Long> absentStudentIds, 
                                                   List<Long> absentTeacherIds, 
                                                   boolean isHoliday, Long teacherId) {
        log.info("Marking daily attendance for school ID: {} on date: {}", schoolId, date);
        
        // Skip Sundays automatically
        if (date.getDayOfWeek() == java.time.DayOfWeek.SUNDAY) {
            throw new IllegalArgumentException("Cannot mark attendance on Sunday");
        }
        
        // Get all active students in the school
        List<Student> allStudents = studentRepository.findBySchoolIdAndIsActiveTrue(schoolId, 
                org.springframework.data.domain.Pageable.unpaged()).getContent();
        
        // Validate teacher if provided
        Teacher teacher = null;
        if (teacherId != null) {
            teacher = teacherRepository.findById(teacherId)
                    .orElseThrow(() -> new IllegalArgumentException("Teacher with ID " + teacherId + " not found"));
        }
        
        List<AttendanceRecord> createdRecords = new java.util.ArrayList<>();
        int successCount = 0;
        int failureCount = 0;
        
        for (Student student : allStudents) {
            try {
                AttendanceStatus status;
                String note = null;
                
                if (isHoliday) {
                    status = AttendanceStatus.HOLIDAY;
                    note = "Holiday";
                } else if (absentStudentIds != null && absentStudentIds.contains(student.getId())) {
                    status = AttendanceStatus.ABSENT;
                } else {
                    status = AttendanceStatus.PRESENT;
                }
                
                // Check if attendance already exists
                Optional<AttendanceRecord> existingRecord = attendanceRecordRepository
                        .findByStudentIdAndDate(student.getId(), date);
                
                AttendanceRecord record;
                if (existingRecord.isPresent()) {
                    // Update existing record
                    record = existingRecord.get();
                    record.setStatus(status);
                    record.setNote(note);
                    record.setMarkedTime(LocalTime.now());
                    record.setTeacher(teacher);
                    record.setIsHoliday(isHoliday);
                } else {
                    // Create new record
                    record = new AttendanceRecord(date, status, note, LocalTime.now(), student, teacher);
                    record.setIsHoliday(isHoliday);
                }
                
                AttendanceRecord savedRecord = attendanceRecordRepository.save(record);
                createdRecords.add(savedRecord);
                successCount++;
                
            } catch (Exception e) {
                log.error("Failed to mark attendance for student ID: {} - {}", student.getId(), e.getMessage());
                failureCount++;
            }
        }
        
        log.info("Daily attendance marked: {} successful, {} failed for {} students", 
                successCount, failureCount, allStudents.size());
        
        return new DailyAttendanceResult(allStudents.size(), successCount, failureCount, 
                                       createdRecords, date, isHoliday);
    }

    /**
     * Get attendance summary for a student
     */
    @Transactional(readOnly = true)
    public AttendanceSummary getAttendanceSummaryForStudent(Long studentId, LocalDate startDate, LocalDate endDate) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student with ID " + studentId + " not found"));
        
        List<AttendanceRecord> records = attendanceRecordRepository
                .findByStudentIdAndDateBetweenOrderByDateDesc(studentId, startDate, endDate);
        
        return calculateAttendanceSummary(student, records, startDate, endDate);
    }

    /**
     * Get attendance summary for a teacher (if teacher attendance is tracked)
     */
    @Transactional(readOnly = true)
    public AttendanceSummary getAttendanceSummaryForTeacher(Long teacherId, LocalDate startDate, LocalDate endDate) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher with ID " + teacherId + " not found"));
        
        // For teachers, we'll count the days they marked attendance as "present"
        List<AttendanceRecord> markedRecords = attendanceRecordRepository
                .findByTeacherIdAndDateOrderByCreatedAt(teacherId, startDate);
        
        // Calculate working days between dates (excluding Sundays and holidays)
        long totalWorkingDays = calculateWorkingDays(startDate, endDate);
        
        // Count unique dates when teacher marked attendance
        long presentDays = markedRecords.stream()
                .filter(record -> !record.getDate().getDayOfWeek().equals(java.time.DayOfWeek.SUNDAY))
                .map(AttendanceRecord::getDate)
                .distinct()
                .count();
        
        long absentDays = totalWorkingDays - presentDays;
        double attendancePercentage = totalWorkingDays > 0 ? (presentDays * 100.0 / totalWorkingDays) : 0.0;
        
        return new AttendanceSummary(
                teacher.getFullName(),
                teacher.getEmpNo(),
                null, // GR No (not applicable for teachers)
                null, // Roll No (not applicable for teachers)
                teacher.getGender().toString(),
                teacher.getDateOfBirth(),
                teacher.getMobileNumber(),
                null, // Standard (not applicable for teachers)
                totalWorkingDays,
                presentDays,
                absentDays,
                0L, // Half days
                0L, // Sick leave
                0L, // Holidays (handled separately)
                attendancePercentage
        );
    }

    /**
     * Calculate attendance summary from records
     */
    private AttendanceSummary calculateAttendanceSummary(Student student, List<AttendanceRecord> records, 
                                                       LocalDate startDate, LocalDate endDate) {
        long totalDays = 0;
        long presentDays = 0;
        long absentDays = 0;
        long halfDays = 0;
        long sickLeaveDays = 0;
        long holidayDays = 0;
        
        Map<AttendanceStatus, Long> statusCounts = records.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        AttendanceRecord::getStatus,
                        java.util.stream.Collectors.counting()));
        
        presentDays = statusCounts.getOrDefault(AttendanceStatus.PRESENT, 0L) + 
                     statusCounts.getOrDefault(AttendanceStatus.LATE, 0L);
        absentDays = statusCounts.getOrDefault(AttendanceStatus.ABSENT, 0L);
        halfDays = statusCounts.getOrDefault(AttendanceStatus.HALF_DAY, 0L);
        sickLeaveDays = statusCounts.getOrDefault(AttendanceStatus.SICK_LEAVE, 0L);
        holidayDays = statusCounts.getOrDefault(AttendanceStatus.HOLIDAY, 0L);
        
        totalDays = presentDays + absentDays + halfDays + sickLeaveDays;
        double attendancePercentage = totalDays > 0 ? (presentDays * 100.0 / totalDays) : 0.0;
        
        return new AttendanceSummary(
                student.getFullName(),
                student.getGrNo(),
                student.getRollNo(),
                student.getGender().toString(),
                student.getDateOfBirth(),
                student.getMobileNumber(),
                student.getStandard() + (student.getSection() != null ? "-" + student.getSection() : ""),
                totalDays,
                presentDays,
                absentDays,
                halfDays,
                sickLeaveDays,
                holidayDays,
                attendancePercentage
        );
    }

    /**
     * Calculate working days between two dates (excluding Sundays)
     */
    private long calculateWorkingDays(LocalDate startDate, LocalDate endDate) {
        long totalDays = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        long weekends = 0;
        
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            if (current.getDayOfWeek() == java.time.DayOfWeek.SUNDAY) {
                weekends++;
            }
            current = current.plusDays(1);
        }
        
        return totalDays - weekends;
    }

    // Inner classes for request/response objects
    public static class BulkAttendanceRequest {
        private Long studentId;
        private LocalDate date;
        private AttendanceStatus status;
        private String note;

        // Constructors, getters, and setters
        public BulkAttendanceRequest() {}

        public BulkAttendanceRequest(Long studentId, LocalDate date, AttendanceStatus status, String note) {
            this.studentId = studentId;
            this.date = date;
            this.status = status;
            this.note = note;
        }

        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }
        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        public AttendanceStatus getStatus() { return status; }
        public void setStatus(AttendanceStatus status) { this.status = status; }
        public String getNote() { return note; }
        public void setNote(String note) { this.note = note; }
    }

    public static class AttendanceStatistics {
        private long totalDays;
        private long presentDays;
        private long absentDays;
        private long halfDays;
        private long sickLeaveDays;
        private double attendancePercentage;

        public AttendanceStatistics(long totalDays, long presentDays, long absentDays, 
                                  long halfDays, long sickLeaveDays, double attendancePercentage) {
            this.totalDays = totalDays;
            this.presentDays = presentDays;
            this.absentDays = absentDays;
            this.halfDays = halfDays;
            this.sickLeaveDays = sickLeaveDays;
            this.attendancePercentage = attendancePercentage;
        }

        // Getters and setters
        public long getTotalDays() { return totalDays; }
        public long getPresentDays() { return presentDays; }
        public long getAbsentDays() { return absentDays; }
        public long getHalfDays() { return halfDays; }
        public long getSickLeaveDays() { return sickLeaveDays; }
        public double getAttendancePercentage() { return attendancePercentage; }
    }

    public static class DailyAttendanceSummary {
        private String standard;
        private String section;
        private Long totalStudents;
        private Long presentCount;
        private Long absentCount;
        private Long lateCount;
        private Long halfDayCount;
        private Long sickLeaveCount;
        private Double attendancePercentage;

        public DailyAttendanceSummary(String standard, String section, Long totalStudents, 
                                    Long presentCount, Long absentCount, Long lateCount, 
                                    Long halfDayCount, Double attendancePercentage) {
            this.standard = standard;
            this.section = section;
            this.totalStudents = totalStudents;
            this.presentCount = presentCount;
            this.absentCount = absentCount;
            this.lateCount = lateCount;
            this.halfDayCount = halfDayCount;
            this.sickLeaveCount = 0L;
            this.attendancePercentage = attendancePercentage;
        }

        // Getters and setters
        public String getStandard() { return standard; }
        public String getSection() { return section; }
        public Long getTotalStudents() { return totalStudents; }
        public void setTotalStudents(Long totalStudents) { this.totalStudents = totalStudents; }
        public Long getPresentCount() { return presentCount; }
        public void setPresentCount(Long presentCount) { this.presentCount = presentCount; }
        public Long getAbsentCount() { return absentCount; }
        public void setAbsentCount(Long absentCount) { this.absentCount = absentCount; }
        public Long getLateCount() { return lateCount; }
        public void setLateCount(Long lateCount) { this.lateCount = lateCount; }
        public Long getHalfDayCount() { return halfDayCount; }
        public void setHalfDayCount(Long halfDayCount) { this.halfDayCount = halfDayCount; }
        public Long getSickLeaveCount() { return sickLeaveCount; }
        public void setSickLeaveCount(Long sickLeaveCount) { this.sickLeaveCount = sickLeaveCount; }
        public Double getAttendancePercentage() { return attendancePercentage; }
        public void setAttendancePercentage(Double attendancePercentage) { this.attendancePercentage = attendancePercentage; }
    }

    public static class DailyAttendanceResult {
        private int totalStudents;
        private int successCount;
        private int failureCount;
        private List<AttendanceRecord> records;
        private LocalDate date;
        private boolean isHoliday;

        public DailyAttendanceResult(int totalStudents, int successCount, int failureCount,
                                   List<AttendanceRecord> records, LocalDate date, boolean isHoliday) {
            this.totalStudents = totalStudents;
            this.successCount = successCount;
            this.failureCount = failureCount;
            this.records = records;
            this.date = date;
            this.isHoliday = isHoliday;
        }

        // Getters
        public int getTotalStudents() { return totalStudents; }
        public int getSuccessCount() { return successCount; }
        public int getFailureCount() { return failureCount; }
        public List<AttendanceRecord> getRecords() { return records; }
        public LocalDate getDate() { return date; }
        public boolean isHoliday() { return isHoliday; }
    }

    public static class AttendanceSummary {
        private String studentName;
        private String grNo;
        private String rollNo;
        private String gender;
        private LocalDate dateOfBirth;
        private String mobileNumber;
        private String standard;
        private long totalDays;
        private long presentDays;
        private long absentDays;
        private long halfDays;
        private long sickLeaveDays;
        private long holidayDays;
        private double attendancePercentage;

        public AttendanceSummary(String studentName, String grNo, String rollNo, String gender,
                               LocalDate dateOfBirth, String mobileNumber, String standard,
                               long totalDays, long presentDays, long absentDays, long halfDays,
                               long sickLeaveDays, long holidayDays, double attendancePercentage) {
            this.studentName = studentName;
            this.grNo = grNo;
            this.rollNo = rollNo;
            this.gender = gender;
            this.dateOfBirth = dateOfBirth;
            this.mobileNumber = mobileNumber;
            this.standard = standard;
            this.totalDays = totalDays;
            this.presentDays = presentDays;
            this.absentDays = absentDays;
            this.halfDays = halfDays;
            this.sickLeaveDays = sickLeaveDays;
            this.holidayDays = holidayDays;
            this.attendancePercentage = attendancePercentage;
        }

        // Getters
        public String getStudentName() { return studentName; }
        public String getGrNo() { return grNo; }
        public String getRollNo() { return rollNo; }
        public String getGender() { return gender; }
        public LocalDate getDateOfBirth() { return dateOfBirth; }
        public String getMobileNumber() { return mobileNumber; }
        public String getStandard() { return standard; }
        public long getTotalDays() { return totalDays; }
        public long getPresentDays() { return presentDays; }
        public long getAbsentDays() { return absentDays; }
        public long getHalfDays() { return halfDays; }
        public long getSickLeaveDays() { return sickLeaveDays; }
        public long getHolidayDays() { return holidayDays; }
        public double getAttendancePercentage() { return attendancePercentage; }
        
        public int getAge() {
            return java.time.Period.between(dateOfBirth, LocalDate.now()).getYears();
        }
    }
}
