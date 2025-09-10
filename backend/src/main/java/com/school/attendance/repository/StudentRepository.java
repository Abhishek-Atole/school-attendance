package com.school.attendance.repository;

import com.school.attendance.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    /**
     * Find student by GR Number and School
     */
    Optional<Student> findByGrNoAndSchoolId(String grNo, Long schoolId);

    /**
     * Find student by Roll Number, Standard, Section, and School
     */
    Optional<Student> findByRollNoAndStandardAndSectionAndSchoolId(
            String rollNo, String standard, String section, Long schoolId);

    /**
     * Find all students by school with pagination
     */
    Page<Student> findBySchoolIdAndIsActiveTrue(Long schoolId, Pageable pageable);

    /**
     * Find students by class (standard and section)
     */
    List<Student> findBySchoolIdAndStandardAndSectionAndIsActiveTrueOrderByRollNo(
            Long schoolId, String standard, String section);

    /**
     * Find students by standard only
     */
    List<Student> findBySchoolIdAndStandardAndIsActiveTrueOrderByRollNo(
            Long schoolId, String standard);

    /**
     * Find all active students in a school
     */
    List<Student> findBySchoolIdAndIsActiveTrueOrderByStandardAscSectionAscRollNoAsc(Long schoolId);

    /**
     * Search students by name (first name or last name)
     */
    @Query("SELECT s FROM Student s WHERE s.schoolId = :schoolId AND s.isActive = true AND " +
           "(LOWER(s.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(CONCAT(s.firstName, ' ', s.lastName)) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Student> searchByName(@Param("schoolId") Long schoolId, @Param("searchTerm") String searchTerm);

    /**
     * Search students by GR Number or Roll Number
     */
    @Query("SELECT s FROM Student s WHERE s.schoolId = :schoolId AND s.isActive = true AND " +
           "(LOWER(s.grNo) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.rollNo) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Student> searchByNumber(@Param("schoolId") Long schoolId, @Param("searchTerm") String searchTerm);

    /**
     * Count students by class
     */
    long countBySchoolIdAndStandardAndSectionAndIsActiveTrue(
            Long schoolId, String standard, String section);

    /**
     * Count total active students in school
     */
    long countBySchoolIdAndIsActiveTrue(Long schoolId);

    /**
     * Get all standards in a school
     */
    @Query("SELECT DISTINCT s.standard FROM Student s WHERE s.schoolId = :schoolId AND s.isActive = true ORDER BY s.standard")
    List<String> findDistinctStandardsBySchoolId(@Param("schoolId") Long schoolId);

    /**
     * Get all sections for a standard in a school
     */
    @Query("SELECT DISTINCT s.section FROM Student s WHERE s.schoolId = :schoolId AND s.standard = :standard AND s.isActive = true ORDER BY s.section")
    List<String> findDistinctSectionsBySchoolIdAndStandard(@Param("schoolId") Long schoolId, @Param("standard") String standard);

    /**
     * Check if GR Number exists in school
     */
    boolean existsByGrNoAndSchoolId(String grNo, Long schoolId);

    /**
     * Check if Roll Number exists in class
     */
    boolean existsByRollNoAndStandardAndSectionAndSchoolId(
            String rollNo, String standard, String section, Long schoolId);

    /**
     * Find students with low attendance (custom query for reporting)
     */
    @Query("SELECT s FROM Student s WHERE s.schoolId = :schoolId AND s.isActive = true AND " +
           "s.id IN (SELECT ar.student.id FROM AttendanceRecord ar WHERE " +
           "ar.date BETWEEN :startDate AND :endDate AND ar.isHoliday = false " +
           "GROUP BY ar.student.id " +
           "HAVING (SUM(CASE WHEN ar.status = 'PRESENT' OR ar.status = 'LATE' THEN 1 ELSE 0 END) * 100.0 / COUNT(*)) < :threshold)")
    List<Student> findStudentsWithLowAttendance(@Param("schoolId") Long schoolId, 
                                               @Param("startDate") java.time.LocalDate startDate,
                                               @Param("endDate") java.time.LocalDate endDate,
                                               @Param("threshold") double threshold);
}
