# Low-Level Design (LLD) - School Attendance Management System

## 🏗️ System Components Overview

This document provides detailed technical specifications for implementing the School Attendance Management System, including entity models, service layers, controllers, and utility classes.

## 📊 Entity Models & Relationships

### 1. School Entity
```java
@Entity
@Table(name = "schools")
public class School {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String address;
    
    private String logoPath;
    
    private String contactEmail;
    
    private String contactPhone;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // One-to-Many relationships
    @OneToMany(mappedBy = "school", cascade = CascadeType.ALL)
    private List<Student> students;
    
    @OneToMany(mappedBy = "school", cascade = CascadeType.ALL)
    private List<Teacher> teachers;
}
```

### 2. Student Entity
```java
@Entity
@Table(name = "students", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"grNo", "schoolId"}),
    @UniqueConstraint(columnNames = {"rollNo", "standard", "schoolId"})
})
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 20)
    private String grNo; // General Register Number
    
    @Column(nullable = false, length = 10)
    private String rollNo;
    
    @Column(nullable = false, length = 100)
    private String firstName;
    
    @Column(nullable = false, length = 100)
    private String lastName;
    
    @Column(nullable = false)
    private LocalDate dateOfBirth;
    
    @Enumerated(EnumType.STRING)
    private Gender gender;
    
    @Column(length = 50)
    private String caste;
    
    @Column(nullable = false, length = 10)
    private String standard; // Class/Grade
    
    @Column(length = 10)
    private String section;
    
    @Column(length = 15)
    private String mobileNumber;
    
    @Column(length = 100)
    private String parentName;
    
    @Column(length = 15)
    private String parentMobile;
    
    private String profilePhotoPath;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // Many-to-One relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;
    
    // One-to-Many relationship
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<AttendanceRecord> attendanceRecords;
}

enum Gender {
    MALE, FEMALE, OTHER
}
```

### 3. Teacher Entity
```java
@Entity
@Table(name = "teachers", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"empNo", "schoolId"})
})
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 20)
    private String empNo; // Employee Number
    
    @Column(nullable = false, length = 100)
    private String firstName;
    
    @Column(nullable = false, length = 100)
    private String lastName;
    
    @Column(nullable = false)
    private LocalDate dateOfBirth;
    
    @Enumerated(EnumType.STRING)
    private Gender gender;
    
    @Column(length = 100)
    private String primarySubject;
    
    @ElementCollection
    @CollectionTable(name = "teacher_subjects")
    private Set<String> subjects;
    
    @ElementCollection
    @CollectionTable(name = "teacher_classes")
    private Set<String> assignedClasses;
    
    @Column(length = 15)
    private String mobileNumber;
    
    @Column(length = 100)
    private String email;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // Many-to-One relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;
    
    // One-to-Many relationship
    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL)
    private List<AttendanceRecord> attendanceRecords;
}
```

### 4. AttendanceRecord Entity
```java
@Entity
@Table(name = "attendance_records", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"date", "studentId"})
})
public class AttendanceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private LocalDate date;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status;
    
    @Column(length = 500)
    private String note;
    
    private LocalTime markedTime;
    
    @Column(nullable = false)
    private Boolean isHoliday = false;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // Many-to-One relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher; // Optional: who marked the attendance
}

enum AttendanceStatus {
    PRESENT, ABSENT, LATE, HALF_DAY, HOLIDAY, SICK_LEAVE
}
```

### 5. User Entity (for Authentication)
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Enumerated(EnumType.STRING)
    private Role role;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    // Optional foreign keys based on role
    private Long teacherId;
    private Long studentId;
    private Long schoolId;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
}

enum Role {
    ADMIN, TEACHER, STUDENT
}
```

## 🔄 Entity Relationships

```
School (1) ←→ (Many) Student
School (1) ←→ (Many) Teacher
Student (1) ←→ (Many) AttendanceRecord
Teacher (1) ←→ (Many) AttendanceRecord [Optional]
User (1) ←→ (0..1) Teacher
User (1) ←→ (0..1) Student
```

## 🏢 Service Layer Design

### 1. AttendanceService
```java
@Service
@Transactional
public class AttendanceService {
    
    // Core Methods
    public AttendanceRecord markAttendance(Long studentId, LocalDate date, 
                                         AttendanceStatus status, String note);
    
    public List<AttendanceRecord> bulkMarkAttendance(List<AttendanceRequest> requests);
    
    public AttendanceRecord updateAttendance(Long recordId, AttendanceStatus status, String note);
    
    public void deleteAttendance(Long recordId);
    
    // Query Methods
    public List<AttendanceRecord> getStudentAttendance(Long studentId, 
                                                     LocalDate startDate, LocalDate endDate);
    
    public List<AttendanceRecord> getClassAttendance(String standard, String section, 
                                                   LocalDate date);
    
    public AttendanceStatistics getAttendanceStatistics(Long studentId, 
                                                       LocalDate startDate, LocalDate endDate);
    
    public List<Student> getAbsentStudents(LocalDate date, String standard);
    
    // Holiday Management
    public void markHoliday(LocalDate date, String reason);
    
    public List<Holiday> getHolidays(LocalDate startDate, LocalDate endDate);
}
```

### 2. StudentService
```java
@Service
@Transactional
public class StudentService {
    
    public Student createStudent(StudentCreateRequest request);
    
    public Student updateStudent(Long studentId, StudentUpdateRequest request);
    
    public void deleteStudent(Long studentId);
    
    public Student getStudentById(Long studentId);
    
    public Student getStudentByGrNo(String grNo, Long schoolId);
    
    public Page<Student> getStudents(Long schoolId, String standard, 
                                   String section, Pageable pageable);
    
    public List<Student> searchStudents(String searchTerm, Long schoolId);
    
    public void bulkImportStudents(MultipartFile file, Long schoolId);
    
    public void uploadStudentPhoto(Long studentId, MultipartFile photo);
}
```

### 3. TeacherService
```java
@Service
@Transactional
public class TeacherService {
    
    public Teacher createTeacher(TeacherCreateRequest request);
    
    public Teacher updateTeacher(Long teacherId, TeacherUpdateRequest request);
    
    public void deleteTeacher(Long teacherId);
    
    public Teacher getTeacherById(Long teacherId);
    
    public Teacher getTeacherByEmpNo(String empNo, Long schoolId);
    
    public Page<Teacher> getTeachers(Long schoolId, Pageable pageable);
    
    public List<Teacher> getTeachersBySubject(String subject, Long schoolId);
    
    public void assignClassesToTeacher(Long teacherId, Set<String> classes);
}
```

### 4. ReportService
```java
@Service
public class ReportService {
    
    public AttendanceReport generateDailyReport(LocalDate date, Long schoolId);
    
    public AttendanceReport generateMonthlyReport(int year, int month, 
                                                Long schoolId, String standard);
    
    public StudentAttendanceReport generateStudentReport(Long studentId, 
                                                       LocalDate startDate, LocalDate endDate);
    
    public byte[] exportAttendanceToExcel(AttendanceReport report);
    
    public byte[] exportAttendanceToPdf(AttendanceReport report);
    
    public byte[] exportAttendanceToCsv(AttendanceReport report);
    
    public List<Student> getLowAttendanceStudents(double threshold, 
                                                LocalDate startDate, LocalDate endDate);
}
```

## 🎮 Controller Layer Design

### 1. AttendanceController
```java
@RestController
@RequestMapping("/api/v1/attendance")
@PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
public class AttendanceController {
    
    @PostMapping("/mark")
    public ResponseEntity<AttendanceResponse> markAttendance(
            @Valid @RequestBody AttendanceRequest request) {
        // Implementation
    }
    
    @PostMapping("/bulk-mark")
    public ResponseEntity<List<AttendanceResponse>> bulkMarkAttendance(
            @Valid @RequestBody List<AttendanceRequest> requests) {
        // Implementation
    }
    
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<AttendanceResponse>> getStudentAttendance(
            @PathVariable Long studentId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        // Implementation
    }
    
    @GetMapping("/class/{standard}")
    public ResponseEntity<List<AttendanceResponse>> getClassAttendance(
            @PathVariable String standard,
            @RequestParam String section,
            @RequestParam LocalDate date) {
        // Implementation
    }
    
    @PutMapping("/{recordId}")
    public ResponseEntity<AttendanceResponse> updateAttendance(
            @PathVariable Long recordId,
            @Valid @RequestBody AttendanceUpdateRequest request) {
        // Implementation
    }
}
```

### 2. StudentController
```java
@RestController
@RequestMapping("/api/v1/students")
public class StudentController {
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentResponse> createStudent(
            @Valid @RequestBody StudentCreateRequest request) {
        // Implementation
    }
    
    @GetMapping
    public ResponseEntity<Page<StudentResponse>> getStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String standard,
            @RequestParam(required = false) String section) {
        // Implementation
    }
    
    @GetMapping("/{studentId}")
    public ResponseEntity<StudentResponse> getStudent(@PathVariable Long studentId) {
        // Implementation
    }
    
    @PostMapping("/bulk-import")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ImportResponse> bulkImportStudents(
            @RequestParam("file") MultipartFile file) {
        // Implementation
    }
}
```

## 🛠️ Utility Classes

### 1. Export Utilities
```java
@Component
public class CsvExporter {
    public byte[] exportAttendanceToCsv(List<AttendanceRecord> records) {
        // CSV generation logic
    }
}

@Component
public class ExcelExporter {
    public byte[] exportAttendanceToExcel(List<AttendanceRecord> records) {
        // Excel generation using Apache POI
    }
}

@Component
public class PdfExporter {
    public byte[] exportAttendanceToPdf(AttendanceReport report) {
        // PDF generation using iText
    }
}
```

### 2. Validation Utilities
```java
@Component
public class AttendanceValidator {
    public void validateAttendanceRequest(AttendanceRequest request) {
        // Business validation logic
    }
    
    public boolean isValidGrNo(String grNo) {
        // GR Number format validation
    }
}
```

## 🔄 Sequence Diagram: Mark Attendance Flow

```
Teacher → Frontend → Backend → Database

1. Teacher selects students and marks attendance
   └── Frontend: AttendanceMarkingPage

2. Frontend validates input and sends request
   └── POST /api/v1/attendance/bulk-mark
   
3. Backend processes request
   ├── AttendanceController.bulkMarkAttendance()
   ├── AttendanceService.bulkMarkAttendance()
   ├── Validation (date, student exists, duplicate check)
   ├── AttendanceRepository.saveAll()
   └── Database: INSERT INTO attendance_records

4. Success response sent back
   ├── Database → Repository (saved records)
   ├── Repository → Service (entity list)
   ├── Service → Controller (response DTOs)
   ├── Controller → Frontend (HTTP 200 + data)
   └── Frontend → Teacher (success notification)

Error Handling:
- Validation errors → HTTP 400 Bad Request
- Duplicate attendance → HTTP 409 Conflict
- Student not found → HTTP 404 Not Found
- Server errors → HTTP 500 Internal Server Error
```

## 📋 DTOs (Data Transfer Objects)

### Request DTOs
```java
public class AttendanceRequest {
    @NotNull private Long studentId;
    @NotNull private LocalDate date;
    @NotNull private AttendanceStatus status;
    private String note;
}

public class StudentCreateRequest {
    @NotBlank private String grNo;
    @NotBlank private String rollNo;
    @NotBlank private String firstName;
    @NotBlank private String lastName;
    @NotNull private LocalDate dateOfBirth;
    @NotNull private Gender gender;
    @NotBlank private String standard;
    private String section;
    private String mobileNumber;
    @NotNull private Long schoolId;
}
```

### Response DTOs
```java
public class AttendanceResponse {
    private Long id;
    private LocalDate date;
    private AttendanceStatus status;
    private String note;
    private LocalTime markedTime;
    private StudentSummary student;
    private TeacherSummary teacher;
}

public class StudentResponse {
    private Long id;
    private String grNo;
    private String rollNo;
    private String fullName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String standard;
    private String section;
    private Boolean isActive;
}
```

## 🔐 Security Considerations

### JWT Token Structure
```java
{
  "sub": "teacher123",
  "role": "TEACHER",
  "schoolId": 1,
  "teacherId": 5,
  "exp": 1694320800,
  "iat": 1694234400
}
```

### Authorization Rules
- **ADMIN**: Full access to all endpoints
- **TEACHER**: Can mark attendance for assigned classes only
- **STUDENT**: Can view own attendance only

---

**Document Version**: 1.0  
**Last Updated**: September 10, 2025  
**Author**: GitHub Copilot  
**Status**: Complete
