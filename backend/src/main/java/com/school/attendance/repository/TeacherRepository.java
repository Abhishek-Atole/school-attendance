package com.school.attendance.repository;

import com.school.attendance.entity.Teacher;
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
public interface TeacherRepository extends JpaRepository<Teacher, Long>, JpaSpecificationExecutor<Teacher> {

    /**
     * Find teacher by Employee Number and School
     */
    @Query("SELECT t FROM Teacher t WHERE t.empNo = :empNo AND t.school.id = :schoolId")
    Optional<Teacher> findByEmpNoAndSchoolId(@Param("empNo") String empNo, @Param("schoolId") Long schoolId);

    /**
     * Find teacher by email
     */
    Optional<Teacher> findByEmail(String email);

    /**
     * Find all teachers by school with pagination
     */
    @Query("SELECT t FROM Teacher t WHERE t.school.id = :schoolId AND t.isActive = true")
    Page<Teacher> findBySchoolIdAndIsActiveTrue(@Param("schoolId") Long schoolId, Pageable pageable);

    /**
     * Find all active teachers by school
     */
    @Query("SELECT t FROM Teacher t WHERE t.school.id = :schoolId AND t.isActive = true ORDER BY t.firstName")
    List<Teacher> findBySchoolIdAndIsActiveTrueOrderByFirstName(@Param("schoolId") Long schoolId);

    /**
     * Search teachers by name with pagination
     */
    @Query("SELECT t FROM Teacher t WHERE t.school.id = :schoolId AND t.isActive = true AND " +
           "(LOWER(t.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(t.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(CONCAT(t.firstName, ' ', t.lastName)) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Teacher> searchByName(@Param("schoolId") Long schoolId, @Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Search teachers by name - legacy method
     */
    @Query("SELECT t FROM Teacher t WHERE t.school.id = :schoolId AND t.isActive = true AND " +
           "(LOWER(t.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(t.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(CONCAT(t.firstName, ' ', t.lastName)) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Teacher> searchByNameList(@Param("schoolId") Long schoolId, @Param("searchTerm") String searchTerm);

    /**
     * Find teachers by primary subject with pagination
     */
    @Query("SELECT t FROM Teacher t WHERE t.school.id = :schoolId AND t.primarySubject = :primarySubject AND t.isActive = true ORDER BY t.firstName")
    Page<Teacher> findBySchoolIdAndPrimarySubjectAndIsActiveTrue(
            @Param("schoolId") Long schoolId, @Param("primarySubject") String primarySubject, Pageable pageable);

    /**
     * Find teachers by primary subject - legacy method
     */
    @Query("SELECT t FROM Teacher t WHERE t.school.id = :schoolId AND t.primarySubject = :primarySubject AND t.isActive = true ORDER BY t.firstName")
    List<Teacher> findBySchoolIdAndPrimarySubjectAndIsActiveTrueOrderByFirstName(
            @Param("schoolId") Long schoolId, @Param("primarySubject") String primarySubject);

    /**
     * Find teachers by subject (including secondary subjects) with pagination
     */
    @Query("SELECT DISTINCT t FROM Teacher t JOIN t.subjects s WHERE t.school.id = :schoolId AND t.isActive = true AND s = :subject")
    Page<Teacher> findBySubject(@Param("schoolId") Long schoolId, @Param("subject") String subject, Pageable pageable);

    /**
     * Find teachers by subject (including secondary subjects) - legacy method
     */
    @Query("SELECT DISTINCT t FROM Teacher t JOIN t.subjects s WHERE t.school.id = :schoolId AND t.isActive = true AND s = :subject")
    List<Teacher> findBySubjectList(@Param("schoolId") Long schoolId, @Param("subject") String subject);

    /**
     * Find teachers assigned to a specific class
     */
    @Query("SELECT DISTINCT t FROM Teacher t JOIN t.assignedClasses c WHERE t.school.id = :schoolId AND t.isActive = true AND c = :className")
    List<Teacher> findByAssignedClass(@Param("schoolId") Long schoolId, @Param("className") String className);

    /**
     * Count total active teachers in school
     */
    @Query("SELECT COUNT(t) FROM Teacher t WHERE t.school.id = :schoolId AND t.isActive = true")
    long countBySchoolIdAndIsActiveTrue(@Param("schoolId") Long schoolId);

    /**
     * Check if Employee Number exists in school
     */
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Teacher t WHERE t.empNo = :empNo AND t.school.id = :schoolId")
    boolean existsByEmpNoAndSchoolId(@Param("empNo") String empNo, @Param("schoolId") Long schoolId);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Get all subjects taught in school
     */
    @Query("SELECT DISTINCT s FROM Teacher t JOIN t.subjects s WHERE t.school.id = :schoolId AND t.isActive = true ORDER BY s")
    List<String> findAllSubjectsBySchoolId(@Param("schoolId") Long schoolId);

    /**
     * Get all classes assigned in school
     */
    @Query("SELECT DISTINCT c FROM Teacher t JOIN t.assignedClasses c WHERE t.school.id = :schoolId AND t.isActive = true ORDER BY c")
    List<String> findAllAssignedClassesBySchoolId(@Param("schoolId") Long schoolId);

    /**
     * Find teachers who haven't marked attendance for a specific date and class
     */
    @Query("SELECT t FROM Teacher t WHERE t.school.id = :schoolId AND t.isActive = true AND " +
           ":className MEMBER OF t.assignedClasses AND " +
           "NOT EXISTS (SELECT ar FROM AttendanceRecord ar WHERE ar.teacher = t AND ar.date = :date)")
    List<Teacher> findTeachersNotMarkedAttendance(@Param("schoolId") Long schoolId, 
                                                 @Param("className") String className,
                                                 @Param("date") java.time.LocalDate date);
}
