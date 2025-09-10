-- Performance optimization indexes for School Attendance Management System
-- These indexes will significantly improve query performance for frequently accessed data

-- Indexes for attendance_records table (most queried table)
CREATE INDEX idx_attendance_student_id ON attendance_records(student_id);
CREATE INDEX idx_attendance_teacher_id ON attendance_records(teacher_id);
CREATE INDEX idx_attendance_date ON attendance_records(date);
CREATE INDEX idx_attendance_status ON attendance_records(status);

-- Composite indexes for common query patterns
CREATE INDEX idx_attendance_student_date ON attendance_records(student_id, date);
CREATE INDEX idx_attendance_teacher_date ON attendance_records(teacher_id, date);
CREATE INDEX idx_attendance_date_status ON attendance_records(date, status);

-- Indexes for students table
CREATE INDEX idx_students_gr_no ON students(gr_no);
CREATE INDEX idx_students_roll_no ON students(roll_no);
CREATE INDEX idx_students_class_id ON students(class_id);
CREATE INDEX idx_students_email ON students(email);

-- Indexes for teachers table
CREATE INDEX idx_teachers_employee_id ON teachers(employee_id);
CREATE INDEX idx_teachers_email ON teachers(email);
CREATE INDEX idx_teachers_department ON teachers(department);

-- Indexes for classes table
CREATE INDEX idx_classes_name ON classes(name);
CREATE INDEX idx_classes_grade ON classes(grade);

-- Indexes for subjects table
CREATE INDEX idx_subjects_name ON subjects(name);
CREATE INDEX idx_subjects_code ON subjects(code);

-- Additional performance indexes for reports and analytics
CREATE INDEX idx_attendance_created_at ON attendance_records(created_at);
CREATE INDEX idx_students_created_at ON students(created_at);
CREATE INDEX idx_teachers_created_at ON teachers(created_at);

-- Covering indexes for dashboard queries (include commonly selected columns)
CREATE INDEX idx_attendance_summary ON attendance_records(date, status, student_id, teacher_id);
