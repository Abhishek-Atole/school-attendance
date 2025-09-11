package com.school.attendance.controller;

import com.school.attendance.entity.Student;
import com.school.attendance.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Student Management", description = "APIs for managing students")
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    @Operation(summary = "Create a new student", description = "Creates a new student record in the system")
    public ResponseEntity<Student> createStudent(@Valid @RequestBody Student student) {
        try {
            Student createdStudent = studentService.createStudent(student);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdStudent);
        } catch (IllegalArgumentException e) {
            log.error("Error creating student: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{studentId}")
    @Operation(summary = "Get student by ID", description = "Retrieves a student by their unique ID")
    public ResponseEntity<Student> getStudentById(
            @Parameter(description = "Student ID") @PathVariable Long studentId) {
        try {
            Student student = studentService.getStudentById(studentId);
            return ResponseEntity.ok(student);
        } catch (IllegalArgumentException e) {
            log.error("Student not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    @Operation(summary = "Get all students", description = "Retrieves all students with pagination and filtering")
    public ResponseEntity<Page<Student>> getAllStudents(
            @Parameter(description = "School ID") @RequestParam Long schoolId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "firstName") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String sortDir,
            @Parameter(description = "Filter by standard") @RequestParam(required = false) String standard,
            @Parameter(description = "Filter by section") @RequestParam(required = false) String section) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Student> students = studentService.getStudentsBySchool(schoolId, pageable);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/class/{standard}")
    @Operation(summary = "Get students by class", description = "Retrieves all students in a specific class")
    public ResponseEntity<List<Student>> getStudentsByClass(
            @Parameter(description = "School ID") @RequestParam Long schoolId,
            @Parameter(description = "Standard/Grade") @PathVariable String standard,
            @Parameter(description = "Section") @RequestParam(required = false) String section) {
        
        List<Student> students = studentService.getStudentsByClass(schoolId, standard, section);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/search")
    @Operation(summary = "Search students", description = "Search students by name, GR number, or roll number")
    public ResponseEntity<List<Student>> searchStudents(
            @Parameter(description = "School ID") @RequestParam Long schoolId,
            @Parameter(description = "Search term") @RequestParam String query) {
        
        List<Student> students = studentService.searchStudents(query, schoolId);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/gr/{grNo}")
    @Operation(summary = "Get student by GR number", description = "Retrieves a student by their GR number")
    public ResponseEntity<Student> getStudentByGrNo(
            @Parameter(description = "GR Number") @PathVariable String grNo,
            @Parameter(description = "School ID") @RequestParam Long schoolId) {
        
        Optional<Student> student = studentService.getStudentByGrNo(grNo, schoolId);
        return student.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{studentId}")
    @Operation(summary = "Update student", description = "Updates an existing student record")
    public ResponseEntity<Student> updateStudent(
            @Parameter(description = "Student ID") @PathVariable Long studentId,
            @Valid @RequestBody Student updatedStudent) {
        try {
            Student student = studentService.updateStudent(studentId, updatedStudent);
            return ResponseEntity.ok(student);
        } catch (IllegalArgumentException e) {
            log.error("Error updating student: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{studentId}/deactivate")
    @Operation(summary = "Deactivate student", description = "Deactivates a student (soft delete)")
    public ResponseEntity<Void> deactivateStudent(
            @Parameter(description = "Student ID") @PathVariable Long studentId) {
        try {
            studentService.deactivateStudent(studentId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("Error deactivating student: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{studentId}/activate")
    @Operation(summary = "Activate student", description = "Activates a previously deactivated student")
    public ResponseEntity<Void> activateStudent(
            @Parameter(description = "Student ID") @PathVariable Long studentId) {
        try {
            studentService.activateStudent(studentId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("Error activating student: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{studentId}")
    @Operation(summary = "Delete student", description = "Permanently deletes a student record")
    public ResponseEntity<Void> deleteStudent(
            @Parameter(description = "Student ID") @PathVariable Long studentId) {
        try {
            studentService.deleteStudent(studentId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("Error deleting student: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/standards")
    @Operation(summary = "Get all standards", description = "Retrieves all standards/grades in a school")
    public ResponseEntity<List<String>> getStandards(
            @Parameter(description = "School ID") @RequestParam Long schoolId) {
        
        List<String> standards = studentService.getStandardsBySchool(schoolId);
        return ResponseEntity.ok(standards);
    }

    @GetMapping("/sections")
    @Operation(summary = "Get sections by standard", description = "Retrieves all sections for a specific standard")
    public ResponseEntity<List<String>> getSectionsByStandard(
            @Parameter(description = "School ID") @RequestParam Long schoolId,
            @Parameter(description = "Standard") @RequestParam String standard) {
        
        List<String> sections = studentService.getSectionsByStandard(schoolId, standard);
        return ResponseEntity.ok(sections);
    }

    @GetMapping("/count")
    @Operation(summary = "Get student counts", description = "Gets count of students by various criteria")
    public ResponseEntity<StudentCountResponse> getStudentCounts(
            @Parameter(description = "School ID") @RequestParam Long schoolId,
            @Parameter(description = "Standard") @RequestParam(required = false) String standard,
            @Parameter(description = "Section") @RequestParam(required = false) String section) {
        
        long count;
        if (standard != null && section != null) {
            count = studentService.countStudentsByClass(schoolId, standard, section);
        } else {
            count = studentService.countTotalStudents(schoolId);
        }
        
        StudentCountResponse response = new StudentCountResponse(count, standard, section);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/bulk-import")
    @Operation(summary = "Bulk import students", description = "Imports multiple students at once")
    public ResponseEntity<BulkImportResponse> bulkImportStudents(
            @Valid @RequestBody List<Student> students) {
        try {
            studentService.bulkImportStudents(students);
            BulkImportResponse response = new BulkImportResponse(
                students.size(), students.size(), 0, "All students imported successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error in bulk import: {}", e.getMessage());
            BulkImportResponse response = new BulkImportResponse(
                students.size(), 0, students.size(), "Import failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Response DTOs
    public static class StudentCountResponse {
        private long count;
        private String standard;
        private String section;

        public StudentCountResponse(long count, String standard, String section) {
            this.count = count;
            this.standard = standard;
            this.section = section;
        }

        // Getters and setters
        public long getCount() { return count; }
        public String getStandard() { return standard; }
        public String getSection() { return section; }
    }

    public static class BulkImportResponse {
        private int totalRecords;
        private int successCount;
        private int failureCount;
        private String message;

        public BulkImportResponse(int totalRecords, int successCount, int failureCount, String message) {
            this.totalRecords = totalRecords;
            this.successCount = successCount;
            this.failureCount = failureCount;
            this.message = message;
        }

        // Getters and setters
        public int getTotalRecords() { return totalRecords; }
        public int getSuccessCount() { return successCount; }
        public int getFailureCount() { return failureCount; }
        public String getMessage() { return message; }
    }
}
