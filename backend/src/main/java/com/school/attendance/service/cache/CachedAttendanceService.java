package com.school.attendance.service.cache;

import com.school.attendance.entity.AttendanceRecord;
import com.school.attendance.entity.AttendanceRecord.AttendanceStatus;
import com.school.attendance.repository.AttendanceRecordRepository;
import com.school.attendance.repository.specification.AttendanceSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Cached Attendance Service for High-Performance Analytics
 * Implements intelligent caching for attendance summaries and patterns
 */
//@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CachedAttendanceService {

    private final AttendanceRecordRepository attendanceRecordRepository;

    /**
     * Get daily attendance summary by class with caching
     */
    @Cacheable(value = "dashboardAnalytics", 
               key = "'dailySummary:' + #date + ':school:' + #schoolId")
    public List<Map<String, Object>> getDailyAttendanceSummaryByClass(LocalDate date, Long schoolId) {
        log.debug("Fetching daily attendance summary for date: {} school: {} (cache miss)", date, schoolId);
        
        List<Object[]> results = attendanceRecordRepository.getDailyAttendanceSummaryByClass(date);
        
        return results.stream().map(row -> {
            Map<String, Object> summary = new HashMap<>();
            summary.put("className", row[0]);
            summary.put("totalRecords", row[1]);
            summary.put("presentCount", row[2]);
            summary.put("absentCount", row[3]);
            summary.put("lateCount", row[4]);
            
            // Calculate percentages
            long total = (Long) row[1];
            if (total > 0) {
                summary.put("presentPercentage", ((Long) row[2] * 100.0) / total);
                summary.put("absentPercentage", ((Long) row[3] * 100.0) / total);
                summary.put("latePercentage", ((Long) row[4] * 100.0) / total);
            } else {
                summary.put("presentPercentage", 0.0);
                summary.put("absentPercentage", 0.0);
                summary.put("latePercentage", 0.0);
            }
            
            return summary;
        }).collect(Collectors.toList());
    }

    /**
     * Get student attendance trend with caching
     */
    @Cacheable(value = "attendancePatterns", 
               key = "'trend:student:' + #studentId + ':from:' + #startDate + ':to:' + #endDate")
    public List<Map<String, Object>> getStudentAttendanceTrend(Long studentId, LocalDate startDate, LocalDate endDate) {
        log.debug("Fetching attendance trend for student: {} from {} to {} (cache miss)", 
                studentId, startDate, endDate);
        
        List<Object[]> results = attendanceRecordRepository.getStudentAttendanceTrend(studentId, startDate, endDate);
        
        return results.stream().map(row -> {
            Map<String, Object> trend = new HashMap<>();
            trend.put("date", row[0]);
            trend.put("status", row[1]);
            return trend;
        }).collect(Collectors.toList());
    }

    /**
     * Get attendance summary for a student with caching
     */
    @Cacheable(value = "attendanceSummaries", 
               key = "'summary:student:' + #studentId + ':from:' + #startDate + ':to:' + #endDate")
    public Map<String, Object> getStudentAttendanceSummary(Long studentId, LocalDate startDate, LocalDate endDate) {
        log.debug("Fetching attendance summary for student: {} from {} to {} (cache miss)", 
                studentId, startDate, endDate);
        
        Object[] result = attendanceRecordRepository.getStudentAttendancePercentage(studentId, startDate, endDate);
        
        Map<String, Object> summary = new HashMap<>();
        if (result != null && result.length >= 2) {
            long totalDays = (Long) result[0];
            long presentDays = (Long) result[1];
            long absentDays = totalDays - presentDays;
            
            summary.put("totalDays", totalDays);
            summary.put("presentDays", presentDays);
            summary.put("absentDays", absentDays);
            summary.put("attendancePercentage", totalDays > 0 ? (presentDays * 100.0) / totalDays : 0.0);
        } else {
            summary.put("totalDays", 0);
            summary.put("presentDays", 0);
            summary.put("absentDays", 0);
            summary.put("attendancePercentage", 0.0);
        }
        
        return summary;
    }

    /**
     * Get attendance for a specific date with optimized query and caching
     */
    @Cacheable(value = "attendanceSummaries", 
               key = "'date:' + #date + ':school:' + #schoolId")
    public List<AttendanceRecord> getAttendanceByDate(LocalDate date, Long schoolId) {
        log.debug("Fetching attendance for date: {} school: {} (cache miss)", date, schoolId);
        return attendanceRecordRepository.findByDateWithStudentAndTeacher(date);
    }

    /**
     * Get monthly attendance overview with caching
     */
    @Cacheable(value = "attendancePatterns", 
               key = "'monthly:school:' + #schoolId + ':year:' + #year + ':month:' + #month")
    public Map<String, Object> getMonthlyAttendanceOverview(Long schoolId, int year, int month) {
        log.debug("Fetching monthly attendance overview for school: {} year: {} month: {} (cache miss)", 
                schoolId, year, month);
        
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        
        // Get attendance data using specifications
        Specification<AttendanceRecord> spec = AttendanceSpecifications.hasDateBetween(startDate, endDate)
                .and(AttendanceSpecifications.withStudentAndTeacher());
        
        List<AttendanceRecord> records = attendanceRecordRepository.findAll(spec);
        
        Map<String, Object> overview = new HashMap<>();
        
        // Calculate overall stats
        long totalRecords = records.size();
        long presentCount = records.stream().mapToLong(r -> 
            r.getStatus() == AttendanceStatus.PRESENT ? 1 : 0).sum();
        long absentCount = records.stream().mapToLong(r -> 
            r.getStatus() == AttendanceStatus.ABSENT ? 1 : 0).sum();
        long lateCount = records.stream().mapToLong(r -> 
            r.getStatus() == AttendanceStatus.LATE ? 1 : 0).sum();
        
        overview.put("totalRecords", totalRecords);
        overview.put("presentCount", presentCount);
        overview.put("absentCount", absentCount);
        overview.put("lateCount", lateCount);
        overview.put("presentPercentage", totalRecords > 0 ? (presentCount * 100.0) / totalRecords : 0.0);
        overview.put("absentPercentage", totalRecords > 0 ? (absentCount * 100.0) / totalRecords : 0.0);
        overview.put("latePercentage", totalRecords > 0 ? (lateCount * 100.0) / totalRecords : 0.0);
        
        return overview;
    }

    /**
     * Get attendance statistics by class with caching
     */
    @Cacheable(value = "attendancePatterns", 
               key = "'classStats:school:' + #schoolId + ':from:' + #startDate + ':to:' + #endDate")
    public List<Map<String, Object>> getClassAttendanceStatistics(Long schoolId, LocalDate startDate, LocalDate endDate) {
        log.debug("Fetching class attendance statistics for school: {} from {} to {} (cache miss)", 
                schoolId, startDate, endDate);
        
        // This would ideally use a native query for better performance
        // For now, using specification to group by class
        Specification<AttendanceRecord> spec = AttendanceSpecifications.hasDateBetween(startDate, endDate)
                .and(AttendanceSpecifications.withStudentAndTeacher());
        
        List<AttendanceRecord> records = attendanceRecordRepository.findAll(spec);
        
        // Group by class and calculate statistics
        Map<String, Map<String, Object>> classStats = new HashMap<>();
        
        for (AttendanceRecord record : records) {
            String className = record.getStudent().getStandard() + "-" + record.getStudent().getSection();
            
            classStats.computeIfAbsent(className, k -> {
                Map<String, Object> stats = new HashMap<>();
                stats.put("className", className);
                stats.put("totalRecords", 0L);
                stats.put("presentCount", 0L);
                stats.put("absentCount", 0L);
                stats.put("lateCount", 0L);
                return stats;
            });
            
            Map<String, Object> stats = classStats.get(className);
            stats.put("totalRecords", (Long) stats.get("totalRecords") + 1);
            
            switch (record.getStatus()) {
                case PRESENT:
                    stats.put("presentCount", (Long) stats.get("presentCount") + 1);
                    break;
                case ABSENT:
                    stats.put("absentCount", (Long) stats.get("absentCount") + 1);
                    break;
                case LATE:
                    stats.put("lateCount", (Long) stats.get("lateCount") + 1);
                    break;
                case HALF_DAY:
                    stats.put("presentCount", (Long) stats.get("presentCount") + 1);
                    break;
                case SICK_LEAVE:
                    stats.put("absentCount", (Long) stats.get("absentCount") + 1);
                    break;
                case HOLIDAY:
                    // Don't count holidays in attendance statistics
                    break;
            }
        }
        
        // Calculate percentages for each class
        return classStats.values().stream().map(stats -> {
            long total = (Long) stats.get("totalRecords");
            if (total > 0) {
                long present = (Long) stats.get("presentCount");
                long absent = (Long) stats.get("absentCount");
                long late = (Long) stats.get("lateCount");
                
                stats.put("presentPercentage", (present * 100.0) / total);
                stats.put("absentPercentage", (absent * 100.0) / total);
                stats.put("latePercentage", (late * 100.0) / total);
            }
            return stats;
        }).collect(Collectors.toList());
    }

    // ========== CACHE INVALIDATION METHODS ==========

    /**
     * Clear attendance caches when attendance is marked/updated
     */
    @CacheEvict(value = {"attendanceSummaries", "dashboardAnalytics", "attendancePatterns"}, allEntries = true)
    public void invalidateAttendanceCaches() {
        log.info("Invalidating all attendance caches due to data changes");
    }

    /**
     * Clear caches for a specific date
     */
    @CacheEvict(value = {"attendanceSummaries", "dashboardAnalytics"}, allEntries = true)
    public void invalidateDateCache(LocalDate date) {
        log.info("Invalidating attendance caches for date: {}", date);
    }

    /**
     * Clear caches for a specific student
     */
    @CacheEvict(value = {"attendanceSummaries", "attendancePatterns"}, allEntries = true)
    public void invalidateStudentCache(Long studentId) {
        log.info("Invalidating attendance caches for student: {}", studentId);
    }

    /**
     * Warm up frequently accessed caches
     */
    public void warmUpAttendanceCaches(Long schoolId) {
        log.info("Warming up attendance caches for school: {}", schoolId);
        
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        
        // Warm up today's attendance
        this.getDailyAttendanceSummaryByClass(today, schoolId);
        
        // Warm up current month overview
        this.getMonthlyAttendanceOverview(schoolId, today.getYear(), today.getMonthValue());
        
        // Warm up class statistics for current month
        this.getClassAttendanceStatistics(schoolId, startOfMonth, today);
        
        log.info("Attendance cache warm-up completed for school: {}", schoolId);
    }
}
