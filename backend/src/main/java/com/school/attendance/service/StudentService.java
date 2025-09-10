package com.school.attendance.service;

import com.school.attendance.entity.Student;
import com.school.attendance.repository.StudentRepository;
import com.school.attendance.repository.SchoolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final SchoolRepository schoolRepository;

    /**
     * Create a new student
     */
    public Student createStudent(Student student) {
        log.info("Creating new student with GR No: {} for school ID: {}", 
                student.getGrNo(), student.getSchool().getId());
        
        // Validate school exists
        if (!schoolRepository.existsById(student.getSchool().getId())) {
            throw new IllegalArgumentException("School with ID " + student.getSchool().getId() + " does not exist");
        }
        
        // Check if GR Number already exists
        if (studentRepository.existsByGrNoAndSchoolId(student.getGrNo(), student.getSchool().getId())) {
            throw new IllegalArgumentException("Student with GR Number " + student.getGrNo() + " already exists in this school");
        }
        
        // Check if Roll Number already exists in the same class
        if (student.getSection() != null && 
            studentRepository.existsByRollNoAndStandardAndSectionAndSchoolId(
                student.getRollNo(), student.getStandard(), student.getSection(), student.getSchool().getId())) {
            throw new IllegalArgumentException("Student with Roll Number " + student.getRollNo() + 
                                             " already exists in class " + student.getStandard() + "-" + student.getSection());
        }
        
        Student savedStudent = studentRepository.save(student);
        log.info("Successfully created student with ID: {}", savedStudent.getId());
        return savedStudent;
    }

    /**
     * Update an existing student
     */
    public Student updateStudent(Long studentId, Student updatedStudent) {
        log.info("Updating student with ID: {}", studentId);
        
        Student existingStudent = getStudentById(studentId);
        
        // Check if GR Number is being changed and if it already exists
        if (!existingStudent.getGrNo().equals(updatedStudent.getGrNo()) &&
            studentRepository.existsByGrNoAndSchoolId(updatedStudent.getGrNo(), existingStudent.getSchool().getId())) {
            throw new IllegalArgumentException("Student with GR Number " + updatedStudent.getGrNo() + " already exists");
        }
        
        // Update fields
        existingStudent.setGrNo(updatedStudent.getGrNo());
        existingStudent.setRollNo(updatedStudent.getRollNo());
        existingStudent.setFirstName(updatedStudent.getFirstName());
        existingStudent.setLastName(updatedStudent.getLastName());
        existingStudent.setDateOfBirth(updatedStudent.getDateOfBirth());
        existingStudent.setGender(updatedStudent.getGender());
        existingStudent.setCaste(updatedStudent.getCaste());
        existingStudent.setStandard(updatedStudent.getStandard());
        existingStudent.setSection(updatedStudent.getSection());
        existingStudent.setMobileNumber(updatedStudent.getMobileNumber());
        existingStudent.setParentName(updatedStudent.getParentName());
        existingStudent.setParentMobile(updatedStudent.getParentMobile());
        
        Student savedStudent = studentRepository.save(existingStudent);
        log.info("Successfully updated student with ID: {}", savedStudent.getId());
        return savedStudent;
    }

    /**
     * Get student by ID
     */
    @Transactional(readOnly = true)
    public Student getStudentById(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student with ID " + studentId + " not found"));
    }

    /**
     * Get student by GR Number and School ID
     */
    @Transactional(readOnly = true)
    public Optional<Student> getStudentByGrNo(String grNo, Long schoolId) {
        return studentRepository.findByGrNoAndSchoolId(grNo, schoolId);
    }

    /**
     * Get all students by school with pagination
     */
    @Transactional(readOnly = true)
    public Page<Student> getStudentsBySchool(Long schoolId, Pageable pageable) {
        return studentRepository.findBySchoolIdAndIsActiveTrue(schoolId, pageable);
    }

    /**
     * Get students by class (standard and section)
     */
    @Transactional(readOnly = true)
    public List<Student> getStudentsByClass(Long schoolId, String standard, String section) {
        if (section != null) {
            return studentRepository.findBySchoolIdAndStandardAndSectionAndIsActiveTrueOrderByRollNo(
                    schoolId, standard, section);
        } else {
            return studentRepository.findBySchoolIdAndStandardAndIsActiveTrueOrderByRollNo(
                    schoolId, standard);
        }
    }

    /**
     * Search students by name
     */
    @Transactional(readOnly = true)
    public List<Student> searchStudents(String searchTerm, Long schoolId) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return List.of();
        }
        
        // Search by name first
        List<Student> nameResults = studentRepository.searchByName(schoolId, searchTerm.trim());
        
        // If no results by name, try searching by numbers (GR No, Roll No)
        if (nameResults.isEmpty()) {
            return studentRepository.searchByNumber(schoolId, searchTerm.trim());
        }
        
        return nameResults;
    }

    /**
     * Get all standards in a school
     */
    @Transactional(readOnly = true)
    public List<String> getStandardsBySchool(Long schoolId) {
        return studentRepository.findDistinctStandardsBySchoolId(schoolId);
    }

    /**
     * Get all sections for a standard in a school
     */
    @Transactional(readOnly = true)
    public List<String> getSectionsByStandard(Long schoolId, String standard) {
        return studentRepository.findDistinctSectionsBySchoolIdAndStandard(schoolId, standard);
    }

    /**
     * Count students by class
     */
    @Transactional(readOnly = true)
    public long countStudentsByClass(Long schoolId, String standard, String section) {
        return studentRepository.countBySchoolIdAndStandardAndSectionAndIsActiveTrue(
                schoolId, standard, section);
    }

    /**
     * Count total students in school
     */
    @Transactional(readOnly = true)
    public long countTotalStudents(Long schoolId) {
        return studentRepository.countBySchoolIdAndIsActiveTrue(schoolId);
    }

    /**
     * Deactivate a student (soft delete)
     */
    public void deactivateStudent(Long studentId) {
        log.info("Deactivating student with ID: {}", studentId);
        
        Student student = getStudentById(studentId);
        student.setIsActive(false);
        studentRepository.save(student);
        
        log.info("Successfully deactivated student with ID: {}", studentId);
    }

    /**
     * Activate a student
     */
    public void activateStudent(Long studentId) {
        log.info("Activating student with ID: {}", studentId);
        
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student with ID " + studentId + " not found"));
        student.setIsActive(true);
        studentRepository.save(student);
        
        log.info("Successfully activated student with ID: {}", studentId);
    }

    /**
     * Delete a student permanently
     */
    public void deleteStudent(Long studentId) {
        log.info("Permanently deleting student with ID: {}", studentId);
        
        if (!studentRepository.existsById(studentId)) {
            throw new IllegalArgumentException("Student with ID " + studentId + " not found");
        }
        
        studentRepository.deleteById(studentId);
        log.info("Successfully deleted student with ID: {}", studentId);
    }

    /**
     * Bulk import students (placeholder for future implementation)
     */
    public void bulkImportStudents(List<Student> students) {
        log.info("Bulk importing {} students", students.size());
        
        // Validate all students before saving
        for (Student student : students) {
            if (studentRepository.existsByGrNoAndSchoolId(student.getGrNo(), student.getSchool().getId())) {
                throw new IllegalArgumentException("Student with GR Number " + student.getGrNo() + " already exists");
            }
        }
        
        List<Student> savedStudents = studentRepository.saveAll(students);
        log.info("Successfully imported {} students", savedStudents.size());
    }
}
