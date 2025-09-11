package com.school.attendance.service.cache;

import com.school.attendance.entity.Student;
import com.school.attendance.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Cached Student Service for High-Performance Data Access
 * Implements intelligent caching strategies for frequently accessed student data
 */
//@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CachedStudentService {

    private final StudentRepository studentRepository;

    /**
     * Get student by ID with caching
     */
    @Cacheable(value = "studentProfiles", key = "#studentId", unless = "#result == null")
    public Optional<Student> getStudentById(Long studentId) {
        log.debug("Fetching student by ID: {} (cache miss)", studentId);
        return studentRepository.findById(studentId);
    }

    /**
     * Get student by GR number with caching
     */
    @Cacheable(value = "studentProfiles", key = "'grNo:' + #grNo + ':school:' + #schoolId", unless = "#result.isEmpty()")
    public Optional<Student> getStudentByGrNo(String grNo, Long schoolId) {
        log.debug("Fetching student by GR No: {} for school: {} (cache miss)", grNo, schoolId);
        return studentRepository.findByGrNoAndSchoolId(grNo, schoolId);
    }

    /**
     * Get students by class with caching and pagination
     */
    @Cacheable(value = "classInformation", 
               key = "'students:school:' + #schoolId + ':standard:' + #standard + ':section:' + #section + ':page:' + #pageable.pageNumber + ':size:' + #pageable.pageSize")
    public Page<Student> getStudentsByClass(Long schoolId, String standard, String section, Pageable pageable) {
        log.debug("Fetching students for class {} {} with pagination (cache miss)", standard, section);
        return studentRepository.findBySchoolIdAndStandardAndSectionAndIsActiveTrue(schoolId, standard, section, pageable);
    }

    /**
     * Get all active students by school with caching
     */
    @Cacheable(value = "schoolConfiguration", 
               key = "'activeStudents:school:' + #schoolId")
    public List<Student> getActiveStudentsBySchool(Long schoolId) {
        log.debug("Fetching all active students for school: {} (cache miss)", schoolId);
        return studentRepository.findBySchoolIdAndIsActiveTrueOrderByStandardAscSectionAscRollNoAsc(schoolId);
    }

    /**
     * Search students by name with caching
     */
    @Cacheable(value = "studentProfiles", 
               key = "'search:school:' + #schoolId + ':name:' + #searchTerm + ':page:' + #pageable.pageNumber")
    public Page<Student> searchStudentsByName(Long schoolId, String searchTerm, Pageable pageable) {
        log.debug("Searching students by name: {} (cache miss)", searchTerm);
        return studentRepository.searchByName(schoolId, searchTerm, pageable);
    }

    /**
     * Get student count by class with caching
     */
    @Cacheable(value = "classInformation", 
               key = "'count:school:' + #schoolId + ':standard:' + #standard + ':section:' + #section")
    public long getStudentCountByClass(Long schoolId, String standard, String section) {
        log.debug("Getting student count for class {} {} (cache miss)", standard, section);
        return studentRepository.countBySchoolIdAndStandardAndSectionAndIsActiveTrue(schoolId, standard, section);
    }

    /**
     * Get all standards in school with caching
     */
    @Cacheable(value = "schoolConfiguration", 
               key = "'standards:school:' + #schoolId")
    public List<String> getStandardsBySchool(Long schoolId) {
        log.debug("Fetching standards for school: {} (cache miss)", schoolId);
        return studentRepository.findDistinctStandardsBySchoolId(schoolId);
    }

    /**
     * Get sections for a standard with caching
     */
    @Cacheable(value = "schoolConfiguration", 
               key = "'sections:school:' + #schoolId + ':standard:' + #standard")
    public List<String> getSectionsByStandard(Long schoolId, String standard) {
        log.debug("Fetching sections for standard: {} in school: {} (cache miss)", standard, schoolId);
        return studentRepository.findDistinctSectionsBySchoolIdAndStandard(schoolId, standard);
    }

    // ========== CACHE INVALIDATION METHODS ==========

    /**
     * Update student and refresh cache
     */
    @Transactional
    @CachePut(value = "studentProfiles", key = "#result.id")
    @CacheEvict(value = {"classInformation", "schoolConfiguration"}, allEntries = true)
    public Student updateStudent(Student student) {
        log.info("Updating student: {} and refreshing cache", student.getId());
        return studentRepository.save(student);
    }

    /**
     * Create new student and invalidate related caches
     */
    @Transactional
    @CacheEvict(value = {"classInformation", "schoolConfiguration"}, allEntries = true)
    public Student createStudent(Student student) {
        log.info("Creating new student and invalidating class/school caches");
        Student savedStudent = studentRepository.save(student);
        
        // Cache the new student
        this.cacheStudent(savedStudent);
        
        return savedStudent;
    }

    /**
     * Delete student and clear all related caches
     */
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "studentProfiles", key = "#studentId"),
        @CacheEvict(value = {"classInformation", "schoolConfiguration"}, allEntries = true)
    })
    public void deleteStudent(Long studentId) {
        log.info("Deleting student: {} and clearing caches", studentId);
        studentRepository.deleteById(studentId);
    }

    /**
     * Manually cache a student (useful after creation)
     */
    @CachePut(value = "studentProfiles", key = "#student.id")
    public Student cacheStudent(Student student) {
        log.debug("Manually caching student: {}", student.getId());
        return student;
    }

    /**
     * Clear all student-related caches
     */
    @CacheEvict(value = {"studentProfiles", "classInformation", "schoolConfiguration"}, allEntries = true)
    public void clearAllStudentCaches() {
        log.info("Clearing all student-related caches");
    }

    /**
     * Clear caches for a specific school
     */
    @CacheEvict(value = {"classInformation", "schoolConfiguration"}, allEntries = true)
    public void clearSchoolCaches(Long schoolId) {
        log.info("Clearing caches for school: {}", schoolId);
    }

    /**
     * Warm up frequently used caches
     */
    public void warmUpCaches(Long schoolId) {
        log.info("Warming up student caches for school: {}", schoolId);
        
        // Warm up standards and sections
        this.getStandardsBySchool(schoolId);
        
        // Warm up each class
        List<String> standards = this.getStandardsBySchool(schoolId);
        for (String standard : standards) {
            List<String> sections = this.getSectionsByStandard(schoolId, standard);
            for (String section : sections) {
                // Get first page of students to warm cache
                this.getStudentCountByClass(schoolId, standard, section);
            }
        }
        
        log.info("Cache warm-up completed for school: {}", schoolId);
    }

    // ========== PERFORMANCE MONITORING METHODS ==========

    /**
     * Get cache statistics for monitoring
     */
    public void logCacheStats() {
        log.info("Student cache performance stats logged - implement with cache metrics if needed");
    }
}
