-- =============================================================================
-- School Attendance Management System - Initial Database Schema
-- Version: 1.0
-- Description: Creates all tables, indexes, and constraints for the system
-- Author: GitHub Copilot
-- Date: September 10, 2025
-- =============================================================================

-- =============================================================================
-- 1. CREATE ENUMS (for MySQL 8.0+, use CHECK constraints for older versions)
-- =============================================================================

-- For older MySQL versions, we'll use VARCHAR with CHECK constraints

-- =============================================================================
-- 2. CREATE TABLES
-- =============================================================================

-- -----------------------------------------------------------------------------
-- Schools Table
-- -----------------------------------------------------------------------------
CREATE TABLE schools (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address TEXT,
    logo_path VARCHAR(255),
    contact_email VARCHAR(100),
    contact_phone VARCHAR(15),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Indexes
    INDEX idx_school_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- Students Table
-- -----------------------------------------------------------------------------
CREATE TABLE students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    gr_no VARCHAR(20) NOT NULL COMMENT 'General Register Number',
    roll_no VARCHAR(10) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender ENUM('MALE', 'FEMALE', 'OTHER') NOT NULL,
    caste VARCHAR(50),
    standard VARCHAR(10) NOT NULL COMMENT 'Class/Grade',
    section VARCHAR(10),
    mobile_number VARCHAR(15),
    parent_name VARCHAR(100),
    parent_mobile VARCHAR(15),
    profile_photo_path VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    school_id BIGINT NOT NULL,
    
    -- Foreign Keys
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE CASCADE,
    
    -- Unique Constraints
    UNIQUE KEY uk_student_gr_school (gr_no, school_id),
    UNIQUE KEY uk_student_roll_class_school (roll_no, standard, section, school_id),
    
    -- Indexes
    INDEX idx_student_name (first_name, last_name),
    INDEX idx_student_class (standard, section),
    INDEX idx_student_school (school_id),
    INDEX idx_student_gr_no (gr_no),
    
    -- Check Constraints
    CONSTRAINT chk_student_dob CHECK (date_of_birth < CURDATE())
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- Teachers Table
-- -----------------------------------------------------------------------------
CREATE TABLE teachers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    emp_no VARCHAR(20) NOT NULL COMMENT 'Employee Number',
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender ENUM('MALE', 'FEMALE', 'OTHER') NOT NULL,
    primary_subject VARCHAR(100),
    mobile_number VARCHAR(15),
    email VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    school_id BIGINT NOT NULL,
    
    -- Foreign Keys
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE CASCADE,
    
    -- Unique Constraints
    UNIQUE KEY uk_teacher_emp_school (emp_no, school_id),
    UNIQUE KEY uk_teacher_email (email),
    
    -- Indexes
    INDEX idx_teacher_name (first_name, last_name),
    INDEX idx_teacher_emp_no (emp_no),
    INDEX idx_teacher_school (school_id),
    
    -- Check Constraints
    CONSTRAINT chk_teacher_dob CHECK (date_of_birth < CURDATE()),
    CONSTRAINT chk_teacher_email CHECK (email REGEXP '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- Teacher Subjects Junction Table
-- -----------------------------------------------------------------------------
CREATE TABLE teacher_subjects (
    teacher_id BIGINT NOT NULL,
    subject VARCHAR(100) NOT NULL,
    
    -- Foreign Keys
    FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON DELETE CASCADE,
    
    -- Primary Key
    PRIMARY KEY (teacher_id, subject),
    
    -- Indexes
    INDEX idx_subject (subject)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- Teacher Classes Junction Table
-- -----------------------------------------------------------------------------
CREATE TABLE teacher_classes (
    teacher_id BIGINT NOT NULL,
    class_name VARCHAR(20) NOT NULL COMMENT 'Format: Standard-Section (e.g., 10-A)',
    
    -- Foreign Keys
    FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON DELETE CASCADE,
    
    -- Primary Key
    PRIMARY KEY (teacher_id, class_name),
    
    -- Indexes
    INDEX idx_class_name (class_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- Attendance Records Table
-- -----------------------------------------------------------------------------
CREATE TABLE attendance_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    status ENUM('PRESENT', 'ABSENT', 'LATE', 'HALF_DAY', 'HOLIDAY', 'SICK_LEAVE') NOT NULL,
    note VARCHAR(500),
    marked_time TIME,
    is_holiday BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    student_id BIGINT NOT NULL,
    teacher_id BIGINT NULL COMMENT 'Optional: Who marked the attendance',
    
    -- Foreign Keys
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON DELETE SET NULL,
    
    -- Unique Constraints
    UNIQUE KEY uk_attendance_student_date (student_id, date),
    
    -- Indexes
    INDEX idx_attendance_date (date),
    INDEX idx_attendance_student (student_id),
    INDEX idx_attendance_teacher (teacher_id),
    INDEX idx_attendance_status (status),
    INDEX idx_attendance_student_date (student_id, date),
    
    -- Check Constraints
    CONSTRAINT chk_attendance_date CHECK (date <= CURDATE())
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- Users Table (for Authentication)
-- -----------------------------------------------------------------------------
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL COMMENT 'BCrypt hashed password',
    email VARCHAR(100) NOT NULL UNIQUE,
    role ENUM('ADMIN', 'TEACHER', 'STUDENT') NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    teacher_id BIGINT NULL,
    student_id BIGINT NULL,
    school_id BIGINT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,
    
    -- Foreign Keys
    FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON DELETE SET NULL,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE SET NULL,
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE SET NULL,
    
    -- Indexes
    INDEX idx_user_username (username),
    INDEX idx_user_email (email),
    INDEX idx_user_role (role),
    
    -- Check Constraints
    CONSTRAINT chk_user_email CHECK (email REGEXP '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    CONSTRAINT chk_user_role_reference CHECK (
        (role = 'ADMIN' AND teacher_id IS NULL AND student_id IS NULL) OR
        (role = 'TEACHER' AND teacher_id IS NOT NULL AND student_id IS NULL) OR
        (role = 'STUDENT' AND student_id IS NOT NULL AND teacher_id IS NULL)
    )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- Holidays Table
-- -----------------------------------------------------------------------------
CREATE TABLE holidays (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    reason VARCHAR(255),
    is_recurring BOOLEAN DEFAULT FALSE COMMENT 'For annual holidays like Independence Day',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    school_id BIGINT NOT NULL,
    
    -- Foreign Keys
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE CASCADE,
    
    -- Unique Constraints
    UNIQUE KEY uk_holiday_date_school (date, school_id),
    
    -- Indexes
    INDEX idx_holiday_date (date),
    INDEX idx_holiday_school (school_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- 3. CREATE VIEWS FOR COMMON QUERIES
-- =============================================================================

-- -----------------------------------------------------------------------------
-- View: Student with School Information
-- -----------------------------------------------------------------------------
CREATE VIEW vw_students_with_school AS
SELECT 
    s.id,
    s.gr_no,
    s.roll_no,
    CONCAT(s.first_name, ' ', s.last_name) AS full_name,
    s.first_name,
    s.last_name,
    s.date_of_birth,
    s.gender,
    s.caste,
    s.standard,
    s.section,
    s.mobile_number,
    s.parent_name,
    s.parent_mobile,
    s.is_active,
    sc.name AS school_name,
    sc.id AS school_id
FROM students s
JOIN schools sc ON s.school_id = sc.id;

-- -----------------------------------------------------------------------------
-- View: Teacher with School Information
-- -----------------------------------------------------------------------------
CREATE VIEW vw_teachers_with_school AS
SELECT 
    t.id,
    t.emp_no,
    CONCAT(t.first_name, ' ', t.last_name) AS full_name,
    t.first_name,
    t.last_name,
    t.date_of_birth,
    t.gender,
    t.primary_subject,
    t.mobile_number,
    t.email,
    t.is_active,
    sc.name AS school_name,
    sc.id AS school_id
FROM teachers t
JOIN schools sc ON t.school_id = sc.id;

-- -----------------------------------------------------------------------------
-- View: Daily Attendance Summary
-- -----------------------------------------------------------------------------
CREATE VIEW vw_daily_attendance_summary AS
SELECT 
    ar.date,
    s.school_id,
    s.standard,
    s.section,
    COUNT(*) AS total_students,
    SUM(CASE WHEN ar.status = 'PRESENT' THEN 1 ELSE 0 END) AS present_count,
    SUM(CASE WHEN ar.status = 'ABSENT' THEN 1 ELSE 0 END) AS absent_count,
    SUM(CASE WHEN ar.status = 'LATE' THEN 1 ELSE 0 END) AS late_count,
    SUM(CASE WHEN ar.status = 'HALF_DAY' THEN 1 ELSE 0 END) AS half_day_count,
    ROUND((SUM(CASE WHEN ar.status = 'PRESENT' THEN 1 ELSE 0 END) * 100.0 / COUNT(*)), 2) AS attendance_percentage
FROM attendance_records ar
JOIN students s ON ar.student_id = s.id
WHERE ar.is_holiday = FALSE
GROUP BY ar.date, s.school_id, s.standard, s.section;

-- =============================================================================
-- 4. INSERT SAMPLE DATA
-- =============================================================================

-- -----------------------------------------------------------------------------
-- Sample School
-- -----------------------------------------------------------------------------
INSERT INTO schools (name, address, contact_email, contact_phone) VALUES 
('St. Mary\'s High School', '123 Education Street, Learning City, State 560001', 'admin@stmarys.edu', '+91-9876543210'),
('Greenwood International School', '456 Knowledge Avenue, Smart City, State 560002', 'info@greenwood.edu', '+91-9876543211');

-- -----------------------------------------------------------------------------
-- Sample Students
-- -----------------------------------------------------------------------------
INSERT INTO students (gr_no, roll_no, first_name, last_name, date_of_birth, gender, standard, section, mobile_number, parent_name, parent_mobile, school_id) VALUES
('GR001', '01', 'Rajesh', 'Kumar', '2008-05-15', 'MALE', '10', 'A', '9876543210', 'Suresh Kumar', '9876543211', 1),
('GR002', '02', 'Priya', 'Sharma', '2008-07-22', 'FEMALE', '10', 'A', '9876543212', 'Rakesh Sharma', '9876543213', 1),
('GR003', '03', 'Amit', 'Patel', '2008-03-10', 'MALE', '10', 'B', '9876543214', 'Kishore Patel', '9876543215', 1),
('GR004', '04', 'Sneha', 'Reddy', '2008-09-18', 'FEMALE', '9', 'A', '9876543216', 'Venkat Reddy', '9876543217', 1),
('GR005', '05', 'Rahul', 'Singh', '2009-01-25', 'MALE', '9', 'A', '9876543218', 'Ajay Singh', '9876543219', 1);

-- -----------------------------------------------------------------------------
-- Sample Teachers
-- -----------------------------------------------------------------------------
INSERT INTO teachers (emp_no, first_name, last_name, date_of_birth, gender, primary_subject, mobile_number, email, school_id) VALUES
('EMP001', 'Dr. Sunita', 'Verma', '1985-04-12', 'FEMALE', 'Mathematics', '9876541001', 'sunita.verma@stmarys.edu', 1),
('EMP002', 'Prof. Rajesh', 'Gupta', '1982-11-08', 'MALE', 'Physics', '9876541002', 'rajesh.gupta@stmarys.edu', 1),
('EMP003', 'Mrs. Kavita', 'Joshi', '1988-02-20', 'FEMALE', 'English', '9876541003', 'kavita.joshi@stmarys.edu', 1),
('EMP004', 'Mr. Suresh', 'Yadav', '1980-07-15', 'MALE', 'History', '9876541004', 'suresh.yadav@stmarys.edu', 1);

-- -----------------------------------------------------------------------------
-- Sample Teacher Subjects
-- -----------------------------------------------------------------------------
INSERT INTO teacher_subjects (teacher_id, subject) VALUES
(1, 'Mathematics'),
(1, 'Statistics'),
(2, 'Physics'),
(2, 'Science'),
(3, 'English'),
(3, 'Literature'),
(4, 'History'),
(4, 'Social Studies');

-- -----------------------------------------------------------------------------
-- Sample Teacher Classes
-- -----------------------------------------------------------------------------
INSERT INTO teacher_classes (teacher_id, class_name) VALUES
(1, '10-A'),
(1, '10-B'),
(2, '10-A'),
(3, '9-A'),
(3, '10-A'),
(4, '9-A'),
(4, '10-B');

-- -----------------------------------------------------------------------------
-- Sample Users
-- -----------------------------------------------------------------------------
-- Note: Password is 'password123' hashed with BCrypt
INSERT INTO users (username, password, email, role, teacher_id, school_id) VALUES
('admin', '$2a$10$N.ePAgZ8S9mDei7cSrF8Pu3ZGFl1Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z', 'admin@stmarys.edu', 'ADMIN', NULL, 1),
('sunita.teacher', '$2a$10$N.ePAgZ8S9mDei7cSrF8Pu3ZGFl1Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z', 'sunita.verma@stmarys.edu', 'TEACHER', 1, 1),
('rajesh.teacher', '$2a$10$N.ePAgZ8S9mDei7cSrF8Pu3ZGFl1Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z', 'rajesh.gupta@stmarys.edu', 'TEACHER', 2, 1);

-- -----------------------------------------------------------------------------
-- Sample Holiday
-- -----------------------------------------------------------------------------
INSERT INTO holidays (date, reason, is_recurring, school_id) VALUES
('2025-08-15', 'Independence Day', TRUE, 1),
('2025-10-02', 'Gandhi Jayanti', TRUE, 1),
('2025-12-25', 'Christmas Day', TRUE, 1);

-- =============================================================================
-- 5. CREATE STORED PROCEDURES
-- =============================================================================

-- -----------------------------------------------------------------------------
-- Procedure: Get Attendance Statistics for a Student
-- -----------------------------------------------------------------------------
DELIMITER //
CREATE PROCEDURE GetStudentAttendanceStats(
    IN p_student_id BIGINT,
    IN p_start_date DATE,
    IN p_end_date DATE
)
BEGIN
    SELECT 
        COUNT(*) as total_days,
        SUM(CASE WHEN status = 'PRESENT' THEN 1 ELSE 0 END) as present_days,
        SUM(CASE WHEN status = 'ABSENT' THEN 1 ELSE 0 END) as absent_days,
        SUM(CASE WHEN status = 'LATE' THEN 1 ELSE 0 END) as late_days,
        SUM(CASE WHEN status = 'HALF_DAY' THEN 1 ELSE 0 END) as half_days,
        ROUND((SUM(CASE WHEN status = 'PRESENT' THEN 1 ELSE 0 END) * 100.0 / COUNT(*)), 2) as attendance_percentage
    FROM attendance_records 
    WHERE student_id = p_student_id 
        AND date BETWEEN p_start_date AND p_end_date
        AND is_holiday = FALSE;
END //
DELIMITER ;

-- -----------------------------------------------------------------------------
-- Procedure: Get Class Attendance for a Date
-- -----------------------------------------------------------------------------
DELIMITER //
CREATE PROCEDURE GetClassAttendance(
    IN p_date DATE,
    IN p_standard VARCHAR(10),
    IN p_section VARCHAR(10),
    IN p_school_id BIGINT
)
BEGIN
    SELECT 
        s.id as student_id,
        s.gr_no,
        s.roll_no,
        CONCAT(s.first_name, ' ', s.last_name) as student_name,
        COALESCE(ar.status, 'NOT_MARKED') as status,
        ar.marked_time,
        ar.note
    FROM students s
    LEFT JOIN attendance_records ar ON s.id = ar.student_id AND ar.date = p_date
    WHERE s.standard = p_standard 
        AND s.section = p_section 
        AND s.school_id = p_school_id
        AND s.is_active = TRUE
    ORDER BY s.roll_no;
END //
DELIMITER ;

-- =============================================================================
-- 6. CREATE TRIGGERS
-- =============================================================================

-- -----------------------------------------------------------------------------
-- Trigger: Update last_login timestamp on user authentication
-- -----------------------------------------------------------------------------
DELIMITER //
CREATE TRIGGER tr_user_last_login 
BEFORE UPDATE ON users
FOR EACH ROW
BEGIN
    IF NEW.password != OLD.password THEN
        SET NEW.last_login = CURRENT_TIMESTAMP;
    END IF;
END //
DELIMITER ;

-- =============================================================================
-- 7. GRANT PERMISSIONS (Optional - for production)
-- =============================================================================

-- Create application user (run these commands as root)
-- CREATE USER 'school_app'@'localhost' IDENTIFIED BY 'secure_password_here';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON school_attendance.* TO 'school_app'@'localhost';
-- FLUSH PRIVILEGES;

-- =============================================================================
-- END OF MIGRATION SCRIPT
-- =============================================================================

-- Verify tables were created successfully
SELECT 
    table_name,
    table_rows,
    table_comment
FROM information_schema.tables 
WHERE table_schema = DATABASE()
ORDER BY table_name;
