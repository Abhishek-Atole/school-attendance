package com.school.attendance.repository;

import com.school.attendance.entity.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    /**
     * Find teacher by Employee Number and School
     */
    Optional<Teacher> findByEmpNoAndSchoolId(String empNo, Long schoolId);

    /**
     * Find teacher by email
     */
    Optional<Teacher> findByEmail(String email);

    /**
     * Find all teachers by school with pagination
     */
    Page<Teacher> findBySchoolIdAndIsActiveTrue(Long schoolId, Pageable pageable);

    /**
     * Find all active teachers by school
     */
    List<Teacher> findBySchoolIdAndIsActiveTrueOrderByFirstName(Long schoolId);

    /**
     * Search teachers by name
     */
    @Query("SELECT t FROM Teacher t WHERE t.schoolId = :schoolId AND t.isActive = true AND " +
           "(LOWER(t.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(t.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(CONCAT(t.firstName, ' ', t.lastName)) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Teacher> searchByName(@Param("schoolId") Long schoolId, @Param("searchTerm") String searchTerm);

    /**
     * Find teachers by primary subject
     */
    List<Teacher> findBySchoolIdAndPrimarySubjectAndIsActiveTrueOrderByFirstName(
            Long schoolId, String primarySubject);

    /**
     * Find teachers by subject (including secondary subjects)
     */
    @Query("SELECT DISTINCT t FROM Teacher t JOIN t.subjects s WHERE t.schoolId = :schoolId AND t.isActive = true AND s = :subject")
    List<Teacher> findBySubject(@Param("schoolId") Long schoolId, @Param("subject") String subject);

    /**
     * Find teachers assigned to a specific class
     */
    @Query("SELECT DISTINCT t FROM Teacher t JOIN t.assignedClasses c WHERE t.schoolId = :schoolId AND t.isActive = true AND c = :className")
    List<Teacher> findByAssignedClass(@Param("schoolId") Long schoolId, @Param("className") String className);

    /**
     * Count total active teachers in school
     */
    long countBySchoolIdAndIsActiveTrue(Long schoolId);

    /**
     * Check if Employee Number exists in school
     */
    boolean existsByEmpNoAndSchoolId(String empNo, Long schoolId);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Get all subjects taught in school
     */
    @Query("SELECT DISTINCT s FROM Teacher t JOIN t.subjects s WHERE t.schoolId = :schoolId AND t.isActive = true ORDER BY s")
    List<String> findAllSubjectsBySchoolId(@Param("schoolId") Long schoolId);

    /**
     * Get all classes assigned in school
     */
    @Query("SELECT DISTINCT c FROM Teacher t JOIN t.assignedClasses c WHERE t.schoolId = :schoolId AND t.isActive = true ORDER BY c")
    List<String> findAllAssignedClassesBySchoolId(@Param("schoolId") Long schoolId);

    /**
     * Find teachers who haven't marked attendance for a specific date and class
     */
    @Query("SELECT t FROM Teacher t WHERE t.schoolId = :schoolId AND t.isActive = true AND " +
           ":className MEMBER OF t.assignedClasses AND " +
           "NOT EXISTS (SELECT ar FROM AttendanceRecord ar WHERE ar.teacher = t AND ar.date = :date)")
    List<Teacher> findTeachersNotMarkedAttendance(@Param("schoolId") Long schoolId, 
                                                 @Param("className") String className,
                                                 @Param("date") java.time.LocalDate date);
}
