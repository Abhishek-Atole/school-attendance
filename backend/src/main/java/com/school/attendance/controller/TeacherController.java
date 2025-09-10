package com.school.attendance.controller;

import com.school.attendance.entity.Teacher;
import com.school.attendance.service.TeacherService;
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
import java.util.Set;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Teacher Management", description = "APIs for managing teachers")
public class TeacherController {

    private final TeacherService teacherService;

    @PostMapping
    @Operation(summary = "Create a new teacher", description = "Creates a new teacher record in the system")
    public ResponseEntity<Teacher> createTeacher(@Valid @RequestBody Teacher teacher) {
        try {
            Teacher createdTeacher = teacherService.createTeacher(teacher);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTeacher);
        } catch (IllegalArgumentException e) {
            log.error("Error creating teacher: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{teacherId}")
    @Operation(summary = "Get teacher by ID", description = "Retrieves a teacher by their unique ID")
    public ResponseEntity<Teacher> getTeacherById(
            @Parameter(description = "Teacher ID") @PathVariable Long teacherId) {
        try {
            Teacher teacher = teacherService.getTeacherById(teacherId);
            return ResponseEntity.ok(teacher);
        } catch (IllegalArgumentException e) {
            log.error("Teacher not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    @Operation(summary = "Get all teachers", description = "Retrieves all teachers with pagination and filtering")
    public ResponseEntity<Page<Teacher>> getAllTeachers(
            @Parameter(description = "School ID") @RequestParam Long schoolId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "firstName") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Teacher> teachers = teacherService.getTeachersBySchool(schoolId, pageable);
        return ResponseEntity.ok(teachers);
    }

    @GetMapping("/all")
    @Operation(summary = "Get all active teachers", description = "Retrieves all active teachers without pagination")
    public ResponseEntity<List<Teacher>> getAllActiveTeachers(
            @Parameter(description = "School ID") @RequestParam Long schoolId) {
        
        List<Teacher> teachers = teacherService.getAllActiveTeachers(schoolId);
        return ResponseEntity.ok(teachers);
    }

    @GetMapping("/search")
    @Operation(summary = "Search teachers", description = "Search teachers by name")
    public ResponseEntity<List<Teacher>> searchTeachers(
            @Parameter(description = "School ID") @RequestParam Long schoolId,
            @Parameter(description = "Search term") @RequestParam String query) {
        
        List<Teacher> teachers = teacherService.searchTeachers(query, schoolId);
        return ResponseEntity.ok(teachers);
    }

    @GetMapping("/emp/{empNo}")
    @Operation(summary = "Get teacher by employee number", description = "Retrieves a teacher by their employee number")
    public ResponseEntity<Teacher> getTeacherByEmpNo(
            @Parameter(description = "Employee Number") @PathVariable String empNo,
            @Parameter(description = "School ID") @RequestParam Long schoolId) {
        
        Optional<Teacher> teacher = teacherService.getTeacherByEmpNo(empNo, schoolId);
        return teacher.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get teacher by email", description = "Retrieves a teacher by their email address")
    public ResponseEntity<Teacher> getTeacherByEmail(
            @Parameter(description = "Email address") @PathVariable String email) {
        
        Optional<Teacher> teacher = teacherService.getTeacherByEmail(email);
        return teacher.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/subject/{subject}")
    @Operation(summary = "Get teachers by subject", description = "Retrieves all teachers who teach a specific subject")
    public ResponseEntity<List<Teacher>> getTeachersBySubject(
            @Parameter(description = "School ID") @RequestParam Long schoolId,
            @Parameter(description = "Subject name") @PathVariable String subject,
            @Parameter(description = "Primary subject only") @RequestParam(defaultValue = "false") boolean primaryOnly) {
        
        List<Teacher> teachers;
        if (primaryOnly) {
            teachers = teacherService.getTeachersByPrimarySubject(schoolId, subject);
        } else {
            teachers = teacherService.getTeachersBySubject(schoolId, subject);
        }
        return ResponseEntity.ok(teachers);
    }

    @GetMapping("/class/{className}")
    @Operation(summary = "Get teachers by class", description = "Retrieves all teachers assigned to a specific class")
    public ResponseEntity<List<Teacher>> getTeachersByClass(
            @Parameter(description = "School ID") @RequestParam Long schoolId,
            @Parameter(description = "Class name (e.g., 10-A)") @PathVariable String className) {
        
        List<Teacher> teachers = teacherService.getTeachersByClass(schoolId, className);
        return ResponseEntity.ok(teachers);
    }

    @PutMapping("/{teacherId}")
    @Operation(summary = "Update teacher", description = "Updates an existing teacher record")
    public ResponseEntity<Teacher> updateTeacher(
            @Parameter(description = "Teacher ID") @PathVariable Long teacherId,
            @Valid @RequestBody Teacher updatedTeacher) {
        try {
            Teacher teacher = teacherService.updateTeacher(teacherId, updatedTeacher);
            return ResponseEntity.ok(teacher);
        } catch (IllegalArgumentException e) {
            log.error("Error updating teacher: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{teacherId}/subjects")
    @Operation(summary = "Assign subjects to teacher", description = "Assigns subjects to a teacher")
    public ResponseEntity<Teacher> assignSubjects(
            @Parameter(description = "Teacher ID") @PathVariable Long teacherId,
            @RequestBody Set<String> subjects) {
        try {
            Teacher teacher = teacherService.assignSubjects(teacherId, subjects);
            return ResponseEntity.ok(teacher);
        } catch (IllegalArgumentException e) {
            log.error("Error assigning subjects: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{teacherId}/classes")
    @Operation(summary = "Assign classes to teacher", description = "Assigns classes to a teacher")
    public ResponseEntity<Teacher> assignClasses(
            @Parameter(description = "Teacher ID") @PathVariable Long teacherId,
            @RequestBody Set<String> classes) {
        try {
            Teacher teacher = teacherService.assignClasses(teacherId, classes);
            return ResponseEntity.ok(teacher);
        } catch (IllegalArgumentException e) {
            log.error("Error assigning classes: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{teacherId}/deactivate")
    @Operation(summary = "Deactivate teacher", description = "Deactivates a teacher (soft delete)")
    public ResponseEntity<Void> deactivateTeacher(
            @Parameter(description = "Teacher ID") @PathVariable Long teacherId) {
        try {
            teacherService.deactivateTeacher(teacherId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("Error deactivating teacher: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{teacherId}/activate")
    @Operation(summary = "Activate teacher", description = "Activates a previously deactivated teacher")
    public ResponseEntity<Void> activateTeacher(
            @Parameter(description = "Teacher ID") @PathVariable Long teacherId) {
        try {
            teacherService.activateTeacher(teacherId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("Error activating teacher: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{teacherId}")
    @Operation(summary = "Delete teacher", description = "Permanently deletes a teacher record")
    public ResponseEntity<Void> deleteTeacher(
            @Parameter(description = "Teacher ID") @PathVariable Long teacherId) {
        try {
            teacherService.deleteTeacher(teacherId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("Error deleting teacher: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/subjects")
    @Operation(summary = "Get all subjects", description = "Retrieves all subjects taught in a school")
    public ResponseEntity<List<String>> getAllSubjects(
            @Parameter(description = "School ID") @RequestParam Long schoolId) {
        
        List<String> subjects = teacherService.getAllSubjects(schoolId);
        return ResponseEntity.ok(subjects);
    }

    @GetMapping("/classes")
    @Operation(summary = "Get all assigned classes", description = "Retrieves all classes with assigned teachers")
    public ResponseEntity<List<String>> getAllAssignedClasses(
            @Parameter(description = "School ID") @RequestParam Long schoolId) {
        
        List<String> classes = teacherService.getAllAssignedClasses(schoolId);
        return ResponseEntity.ok(classes);
    }

    @GetMapping("/count")
    @Operation(summary = "Get teacher count", description = "Gets total count of active teachers in a school")
    public ResponseEntity<TeacherCountResponse> getTeacherCount(
            @Parameter(description = "School ID") @RequestParam Long schoolId) {
        
        long count = teacherService.countTotalTeachers(schoolId);
        TeacherCountResponse response = new TeacherCountResponse(count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{teacherId}/can-mark-attendance")
    @Operation(summary = "Check attendance marking permission", 
               description = "Checks if teacher can mark attendance for a specific class")
    public ResponseEntity<AttendancePermissionResponse> canMarkAttendanceForClass(
            @Parameter(description = "Teacher ID") @PathVariable Long teacherId,
            @Parameter(description = "Class name") @RequestParam String className) {
        
        boolean canMark = teacherService.canMarkAttendanceForClass(teacherId, className);
        AttendancePermissionResponse response = new AttendancePermissionResponse(canMark, className);
        return ResponseEntity.ok(response);
    }

    // Response DTOs
    public static class TeacherCountResponse {
        private long count;

        public TeacherCountResponse(long count) {
            this.count = count;
        }

        public long getCount() { return count; }
    }

    public static class AttendancePermissionResponse {
        private boolean canMarkAttendance;
        private String className;

        public AttendancePermissionResponse(boolean canMarkAttendance, String className) {
            this.canMarkAttendance = canMarkAttendance;
            this.className = className;
        }

        public boolean isCanMarkAttendance() { return canMarkAttendance; }
        public String getClassName() { return className; }
    }
}
