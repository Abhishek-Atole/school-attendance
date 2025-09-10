package com.school.attendance.repository;

import com.school.attendance.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchoolRepository extends JpaRepository<School, Long> {

    /**
     * Find school by name (case-insensitive)
     */
    Optional<School> findByNameIgnoreCase(String name);

    /**
     * Find schools containing name (case-insensitive)
     */
    List<School> findByNameContainingIgnoreCase(String name);

    /**
     * Find schools by contact email
     */
    Optional<School> findByContactEmail(String contactEmail);

    /**
     * Check if school exists by name
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Get all active schools (having active students or teachers)
     */
    @Query("SELECT DISTINCT s FROM School s WHERE " +
           "EXISTS (SELECT 1 FROM Student st WHERE st.school = s AND st.isActive = true) OR " +
           "EXISTS (SELECT 1 FROM Teacher t WHERE t.school = s AND t.isActive = true)")
    List<School> findActiveSchools();

    /**
     * Get school statistics
     */
    @Query("SELECT s.id as schoolId, s.name as schoolName, " +
           "COUNT(DISTINCT st.id) as totalStudents, " +
           "COUNT(DISTINCT t.id) as totalTeachers " +
           "FROM School s " +
           "LEFT JOIN s.students st ON st.isActive = true " +
           "LEFT JOIN s.teachers t ON t.isActive = true " +
           "WHERE s.id = :schoolId " +
           "GROUP BY s.id, s.name")
    Object getSchoolStatistics(@Param("schoolId") Long schoolId);
}
