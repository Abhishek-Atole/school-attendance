package com.school.attendance.repository;

import com.school.attendance.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {

    /**
     * Find student by GR Number and School
     */
    @Query("SELECT s FROM Student s WHERE s.grNo = :grNo AND s.school.id = :schoolId")
    Optional<Student> findByGrNoAndSchoolId(@Param("grNo") String grNo, @Param("schoolId") Long schoolId);

    /**
     * Find student by Roll Number, Standard, Section, and School
     */
    @Query("SELECT s FROM Student s WHERE s.rollNo = :rollNo AND s.standard = :standard AND s.section = :section AND s.school.id = :schoolId")
    Optional<Student> findByRollNoAndStandardAndSectionAndSchoolId(
            @Param("rollNo") String rollNo, @Param("standard") String standard, 
            @Param("section") String section, @Param("schoolId") Long schoolId);

    /**
     * Find all students by school with pagination
     */
    @Query("SELECT s FROM Student s WHERE s.school.id = :schoolId AND s.isActive = true")
    Page<Student> findBySchoolIdAndIsActiveTrue(@Param("schoolId") Long schoolId, Pageable pageable);

    /**
     * Find students by class (standard and section) with pagination
     */
    @Query("SELECT s FROM Student s WHERE s.school.id = :schoolId AND s.standard = :standard AND s.section = :section AND s.isActive = true ORDER BY s.rollNo")
    Page<Student> findBySchoolIdAndStandardAndSectionAndIsActiveTrue(
            @Param("schoolId") Long schoolId, @Param("standard") String standard, 
            @Param("section") String section, Pageable pageable);

    /**
     * Find students by class (standard and section) - legacy method
     */
    @Query("SELECT s FROM Student s WHERE s.school.id = :schoolId AND s.standard = :standard AND s.section = :section AND s.isActive = true ORDER BY s.rollNo")
    List<Student> findBySchoolIdAndStandardAndSectionAndIsActiveTrueOrderByRollNo(
            @Param("schoolId") Long schoolId, @Param("standard") String standard, @Param("section") String section);

    /**
     * Find students by standard only with pagination
     */
    @Query("SELECT s FROM Student s WHERE s.school.id = :schoolId AND s.standard = :standard AND s.isActive = true ORDER BY s.rollNo")
    Page<Student> findBySchoolIdAndStandardAndIsActiveTrue(
            @Param("schoolId") Long schoolId, @Param("standard") String standard, Pageable pageable);

    /**
     * Find students by standard only - legacy method
     */
    @Query("SELECT s FROM Student s WHERE s.school.id = :schoolId AND s.standard = :standard AND s.isActive = true ORDER BY s.rollNo")
    List<Student> findBySchoolIdAndStandardAndIsActiveTrueOrderByRollNo(
            @Param("schoolId") Long schoolId, @Param("standard") String standard);

    /**
     * Find all active students in a school
     */
    @Query("SELECT s FROM Student s WHERE s.school.id = :schoolId AND s.isActive = true ORDER BY s.standard ASC, s.section ASC, s.rollNo ASC")
    List<Student> findBySchoolIdAndIsActiveTrueOrderByStandardAscSectionAscRollNoAsc(@Param("schoolId") Long schoolId);

    /**
     * Search students by name (first name or last name) with pagination
     */
    @Query("SELECT s FROM Student s WHERE s.school.id = :schoolId AND s.isActive = true AND " +
           "(LOWER(s.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(CONCAT(s.firstName, ' ', s.lastName)) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Student> searchByName(@Param("schoolId") Long schoolId, @Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Search students by name (first name or last name) - legacy method
     */
    @Query("SELECT s FROM Student s WHERE s.school.id = :schoolId AND s.isActive = true AND " +
           "(LOWER(s.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(CONCAT(s.firstName, ' ', s.lastName)) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Student> searchByNameList(@Param("schoolId") Long schoolId, @Param("searchTerm") String searchTerm);

    /**
     * Search students by GR Number or Roll Number with pagination
     */
    @Query("SELECT s FROM Student s WHERE s.school.id = :schoolId AND s.isActive = true AND " +
           "(LOWER(s.grNo) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.rollNo) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Student> searchByNumber(@Param("schoolId") Long schoolId, @Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Search students by GR Number or Roll Number - legacy method
     */
    @Query("SELECT s FROM Student s WHERE s.school.id = :schoolId AND s.isActive = true AND " +
           "(LOWER(s.grNo) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.rollNo) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Student> searchByNumberList(@Param("schoolId") Long schoolId, @Param("searchTerm") String searchTerm);

    /**
     * Count students by class
     */
    @Query("SELECT COUNT(s) FROM Student s WHERE s.school.id = :schoolId AND s.standard = :standard AND s.section = :section AND s.isActive = true")
    long countBySchoolIdAndStandardAndSectionAndIsActiveTrue(
            @Param("schoolId") Long schoolId, @Param("standard") String standard, @Param("section") String section);

    /**
     * Count total active students in school
     */
    @Query("SELECT COUNT(s) FROM Student s WHERE s.school.id = :schoolId AND s.isActive = true")
    long countBySchoolIdAndIsActiveTrue(@Param("schoolId") Long schoolId);

    /**
     * Get all standards in a school
     */
    @Query("SELECT DISTINCT s.standard FROM Student s WHERE s.school.id = :schoolId AND s.isActive = true ORDER BY s.standard")
    List<String> findDistinctStandardsBySchoolId(@Param("schoolId") Long schoolId);

    /**
     * Get all sections for a standard in a school
     */
    @Query("SELECT DISTINCT s.section FROM Student s WHERE s.school.id = :schoolId AND s.standard = :standard AND s.isActive = true ORDER BY s.section")
    List<String> findDistinctSectionsBySchoolIdAndStandard(@Param("schoolId") Long schoolId, @Param("standard") String standard);

    /**
     * Check if GR Number exists in school
     */
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Student s WHERE s.grNo = :grNo AND s.school.id = :schoolId")
    boolean existsByGrNoAndSchoolId(@Param("grNo") String grNo, @Param("schoolId") Long schoolId);

    /**
     * Check if Roll Number exists in class
     */
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Student s WHERE s.rollNo = :rollNo AND s.standard = :standard AND s.section = :section AND s.school.id = :schoolId")
    boolean existsByRollNoAndStandardAndSectionAndSchoolId(
            @Param("rollNo") String rollNo, @Param("standard") String standard, 
            @Param("section") String section, @Param("schoolId") Long schoolId);

    /**
     * Find students with low attendance (custom query for reporting)
     */
    @Query("SELECT s FROM Student s WHERE s.school.id = :schoolId AND s.isActive = true AND " +
           "s.id IN (SELECT ar.student.id FROM AttendanceRecord ar WHERE " +
           "ar.date BETWEEN :startDate AND :endDate AND ar.isHoliday = false " +
           "GROUP BY ar.student.id " +
           "HAVING (SUM(CASE WHEN ar.status = 'PRESENT' OR ar.status = 'LATE' THEN 1 ELSE 0 END) * 100.0 / COUNT(*)) < :threshold)")
    List<Student> findStudentsWithLowAttendance(@Param("schoolId") Long schoolId, 
                                               @Param("startDate") java.time.LocalDate startDate,
                                               @Param("endDate") java.time.LocalDate endDate,
                                               @Param("threshold") double threshold);

    /**
     * Find all active students
     */
    List<Student> findByIsActiveTrue();
}
