package com.school.attendance.repository;

import com.school.attendance.entity.AttendanceRecord;
import com.school.attendance.entity.AttendanceRecord.AttendanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {

    /**
     * Find attendance record by student and date
     */
    Optional<AttendanceRecord> findByStudentIdAndDate(Long studentId, LocalDate date);

    /**
     * Find all attendance records for a student between dates
     */
    List<AttendanceRecord> findByStudentIdAndDateBetweenOrderByDateDesc(
            Long studentId, LocalDate startDate, LocalDate endDate);

    /**
     * Find attendance records for a class on a specific date
     */
    @Query("SELECT ar FROM AttendanceRecord ar JOIN ar.student s WHERE " +
           "s.schoolId = :schoolId AND s.standard = :standard AND s.section = :section AND " +
           "ar.date = :date ORDER BY s.rollNo")
    List<AttendanceRecord> findClassAttendanceByDate(@Param("schoolId") Long schoolId,
                                                    @Param("standard") String standard,
                                                    @Param("section") String section,
                                                    @Param("date") LocalDate date);

    /**
     * Find attendance records by teacher and date
     */
    List<AttendanceRecord> findByTeacherIdAndDateOrderByCreatedAt(Long teacherId, LocalDate date);

    /**
     * Find attendance records by status and date range
     */
    List<AttendanceRecord> findByStatusAndDateBetween(AttendanceStatus status, 
                                                     LocalDate startDate, LocalDate endDate);

    /**
     * Count attendance by student and status in date range
     */
    @Query("SELECT COUNT(ar) FROM AttendanceRecord ar WHERE ar.student.id = :studentId AND " +
           "ar.status = :status AND ar.date BETWEEN :startDate AND :endDate AND ar.isHoliday = false")
    long countByStudentAndStatus(@Param("studentId") Long studentId,
                               @Param("status") AttendanceStatus status,
                               @Param("startDate") LocalDate startDate,
                               @Param("endDate") LocalDate endDate);

    /**
     * Get attendance statistics for a student
     */
    @Query("SELECT ar.status, COUNT(ar) FROM AttendanceRecord ar WHERE ar.student.id = :studentId AND " +
           "ar.date BETWEEN :startDate AND :endDate AND ar.isHoliday = false " +
           "GROUP BY ar.status")
    List<Object[]> getStudentAttendanceStatistics(@Param("studentId") Long studentId,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);

    /**
     * Get daily attendance summary for a school
     */
    @Query("SELECT s.standard, s.section, ar.status, COUNT(ar) " +
           "FROM AttendanceRecord ar JOIN ar.student s " +
           "WHERE s.schoolId = :schoolId AND ar.date = :date AND ar.isHoliday = false " +
           "GROUP BY s.standard, s.section, ar.status " +
           "ORDER BY s.standard, s.section")
    List<Object[]> getDailyAttendanceSummary(@Param("schoolId") Long schoolId, @Param("date") LocalDate date);

    /**
     * Find students not marked for attendance on a specific date
     */
    @Query("SELECT s FROM Student s WHERE s.schoolId = :schoolId AND s.standard = :standard AND " +
           "s.section = :section AND s.isActive = true AND " +
           "NOT EXISTS (SELECT ar FROM AttendanceRecord ar WHERE ar.student = s AND ar.date = :date)")
    List<Object> findStudentsNotMarkedAttendance(@Param("schoolId") Long schoolId,
                                                @Param("standard") String standard,
                                                @Param("section") String section,
                                                @Param("date") LocalDate date);

    /**
     * Check if attendance exists for student on date
     */
    boolean existsByStudentIdAndDate(Long studentId, LocalDate date);

    /**
     * Find attendance records with pagination
     */
    Page<AttendanceRecord> findByDateBetweenOrderByDateDesc(LocalDate startDate, LocalDate endDate, Pageable pageable);

    /**
     * Get monthly attendance summary for a class
     */
    @Query("SELECT ar.date, ar.status, COUNT(ar) " +
           "FROM AttendanceRecord ar JOIN ar.student s " +
           "WHERE s.schoolId = :schoolId AND s.standard = :standard AND s.section = :section AND " +
           "ar.date BETWEEN :startDate AND :endDate AND ar.isHoliday = false " +
           "GROUP BY ar.date, ar.status " +
           "ORDER BY ar.date")
    List<Object[]> getMonthlyClassAttendanceSummary(@Param("schoolId") Long schoolId,
                                                   @Param("standard") String standard,
                                                   @Param("section") String section,
                                                   @Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);

    /**
     * Get attendance percentage for a student
     */
    @Query("SELECT " +
           "COUNT(*) as totalDays, " +
           "SUM(CASE WHEN ar.status = 'PRESENT' OR ar.status = 'LATE' THEN 1 ELSE 0 END) as presentDays " +
           "FROM AttendanceRecord ar " +
           "WHERE ar.student.id = :studentId AND ar.date BETWEEN :startDate AND :endDate AND ar.isHoliday = false")
    Object[] getStudentAttendancePercentage(@Param("studentId") Long studentId,
                                          @Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);

    /**
     * Find holidays in date range
     */
    List<AttendanceRecord> findByIsHolidayTrueAndDateBetweenOrderByDate(LocalDate startDate, LocalDate endDate);

    /**
     * Delete attendance records for a specific date (for corrections)
     */
    void deleteByStudentIdAndDate(Long studentId, LocalDate date);
}
