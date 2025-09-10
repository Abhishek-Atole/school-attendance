package com.school.attendance.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "teachers", 
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"empNo", "school_id"}),
           @UniqueConstraint(columnNames = {"email"})
       })
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Employee Number is required")
    @Column(name = "emp_no", nullable = false, length = 20)
    private String empNo; // Employee Number

    @NotBlank(message = "First name is required")
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Gender is required")
    private Student.Gender gender;

    @Column(name = "primary_subject", length = 100)
    private String primarySubject;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid mobile number format")
    @Column(name = "mobile_number", length = 15)
    private String mobileNumber;

    @Email(message = "Invalid email format")
    @Column(length = 100, unique = true)
    private String email;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Many-to-One relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    // One-to-Many relationship
    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AttendanceRecord> attendanceRecords;

    // Many-to-Many relationship for subjects
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "teacher_subjects", 
                    joinColumns = @JoinColumn(name = "teacher_id"))
    @Column(name = "subject")
    private Set<String> subjects;

    // Many-to-Many relationship for assigned classes
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "teacher_classes", 
                    joinColumns = @JoinColumn(name = "teacher_id"))
    @Column(name = "class_name")
    private Set<String> assignedClasses;

    // Derived property for full name
    @Transient
    public String getFullName() {
        return firstName + " " + lastName;
    }

    // Constructor without ID for creation
    public Teacher(String empNo, String firstName, String lastName, 
                  LocalDate dateOfBirth, Student.Gender gender, String primarySubject,
                  String mobileNumber, String email, School school) {
        this.empNo = empNo;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.primarySubject = primarySubject;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.school = school;
        this.isActive = true;
    }
}
