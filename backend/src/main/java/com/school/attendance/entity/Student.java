package com.school.attendance.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "students", 
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"gr_no", "school_id"}),
           @UniqueConstraint(columnNames = {"roll_no", "standard", "section", "school_id"})
       })
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "GR Number is required")
    @Column(name = "gr_no", nullable = false, length = 20)
    private String grNo; // General Register Number

    @NotBlank(message = "Roll Number is required")
    @Column(name = "roll_no", nullable = false, length = 10)
    private String rollNo;

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
    private Gender gender;

    @Column(length = 50)
    private String caste;

    @NotBlank(message = "Standard is required")
    @Column(nullable = false, length = 10)
    private String standard; // Class/Grade

    @Column(length = 10)
    private String section;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid mobile number format")
    @Column(name = "mobile_number", length = 15)
    private String mobileNumber;

    @Column(name = "parent_name", length = 100)
    private String parentName;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid parent mobile number format")
    @Column(name = "parent_mobile", length = 15)
    private String parentMobile;

    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Invalid parent email format")
    @Column(name = "parent_email", length = 100)
    private String parentEmail;

    @Column(name = "profile_photo_path")
    private String profilePhotoPath;

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
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AttendanceRecord> attendanceRecords;

    // Derived property for full name
    @Transient
    public String getFullName() {
        return firstName + " " + lastName;
    }

    // Constructor without ID for creation
    public Student(String grNo, String rollNo, String firstName, String lastName, 
                  LocalDate dateOfBirth, Gender gender, String standard, String section,
                  String mobileNumber, School school) {
        this.grNo = grNo;
        this.rollNo = rollNo;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.standard = standard;
        this.section = section;
        this.mobileNumber = mobileNumber;
        this.school = school;
        this.isActive = true;
    }

    public enum Gender {
        MALE, FEMALE, OTHER
    }
}
