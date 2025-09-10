# Entity Relationship Diagram (ERD) - School Attendance Management System

## 📊 Database Schema Overview

This document provides the Entity Relationship Diagram and database schema for the School Attendance Management System.

## 🔗 Entity Relationship Diagram (Text-Based)

```
┌─────────────────────────────────────────┐
│                 SCHOOL                  │
├─────────────────────────────────────────┤
│ PK  id (BIGINT)                        │
│     name (VARCHAR(100)) NOT NULL       │
│     address (TEXT)                     │
│     logo_path (VARCHAR(255))           │
│     contact_email (VARCHAR(100))       │
│     contact_phone (VARCHAR(15))        │
│     created_at (TIMESTAMP)             │
│     updated_at (TIMESTAMP)             │
└─────────────────────────────────────────┘
           │                    │
           │ 1:N                │ 1:N
           ▼                    ▼
┌─────────────────────────────────────────┐    ┌─────────────────────────────────────────┐
│                STUDENT                  │    │                TEACHER                  │
├─────────────────────────────────────────┤    ├─────────────────────────────────────────┤
│ PK  id (BIGINT)                        │    │ PK  id (BIGINT)                        │
│     gr_no (VARCHAR(20)) NOT NULL       │    │     emp_no (VARCHAR(20)) NOT NULL      │
│     roll_no (VARCHAR(10)) NOT NULL     │    │     first_name (VARCHAR(100)) NOT NULL │
│     first_name (VARCHAR(100)) NOT NULL │    │     last_name (VARCHAR(100)) NOT NULL  │
│     last_name (VARCHAR(100)) NOT NULL  │    │     date_of_birth (DATE) NOT NULL      │
│     date_of_birth (DATE) NOT NULL      │    │     gender (ENUM) NOT NULL             │
│     gender (ENUM) NOT NULL             │    │     primary_subject (VARCHAR(100))     │
│     caste (VARCHAR(50))                │    │     mobile_number (VARCHAR(15))        │
│     standard (VARCHAR(10)) NOT NULL    │    │     email (VARCHAR(100))               │
│     section (VARCHAR(10))              │    │     is_active (BOOLEAN) DEFAULT TRUE   │
│     mobile_number (VARCHAR(15))        │    │     created_at (TIMESTAMP)             │
│     parent_name (VARCHAR(100))         │    │     updated_at (TIMESTAMP)             │
│     parent_mobile (VARCHAR(15))        │    │ FK  school_id (BIGINT) NOT NULL        │
│     profile_photo_path (VARCHAR(255))  │    └─────────────────────────────────────────┘
│     is_active (BOOLEAN) DEFAULT TRUE   │                         │
│     created_at (TIMESTAMP)             │                         │ 1:N
│     updated_at (TIMESTAMP)             │                         ▼
│ FK  school_id (BIGINT) NOT NULL        │    ┌─────────────────────────────────────────┐
└─────────────────────────────────────────┘    │            TEACHER_SUBJECTS             │
           │                                   ├─────────────────────────────────────────┤
           │ 1:N                              │ FK  teacher_id (BIGINT)                │
           ▼                                   │     subject (VARCHAR(100))             │
┌─────────────────────────────────────────┐    └─────────────────────────────────────────┘
│           ATTENDANCE_RECORD             │    
├─────────────────────────────────────────┤    ┌─────────────────────────────────────────┐
│ PK  id (BIGINT)                        │    │            TEACHER_CLASSES              │
│     date (DATE) NOT NULL               │    ├─────────────────────────────────────────┤
│     status (ENUM) NOT NULL             │    │ FK  teacher_id (BIGINT)                │
│     note (VARCHAR(500))                │    │     class_name (VARCHAR(20))           │
│     marked_time (TIME)                 │    └─────────────────────────────────────────┘
│     is_holiday (BOOLEAN) DEFAULT FALSE │    
│     created_at (TIMESTAMP)             │    
│     updated_at (TIMESTAMP)             │    
│ FK  student_id (BIGINT) NOT NULL       │    
│ FK  teacher_id (BIGINT) [OPTIONAL]     │────┘
└─────────────────────────────────────────┘    

┌─────────────────────────────────────────┐
│                  USER                   │
├─────────────────────────────────────────┤
│ PK  id (BIGINT)                        │
│     username (VARCHAR(50)) UNIQUE      │
│     password (VARCHAR(255)) NOT NULL   │
│     email (VARCHAR(100)) UNIQUE        │
│     role (ENUM) NOT NULL               │
│     is_active (BOOLEAN) DEFAULT TRUE   │
│     teacher_id (BIGINT) [OPTIONAL]     │──┐
│     student_id (BIGINT) [OPTIONAL]     │──┼─┐
│     school_id (BIGINT) [OPTIONAL]      │──┼─┼─┐
│     created_at (TIMESTAMP)             │  │ │ │
└─────────────────────────────────────────┘  │ │ │
                                            │ │ │
                          ┌─────────────────┘ │ │
                          │ ┌─────────────────┘ │
                          │ │ ┌─────────────────┘
                          ▼ ▼ ▼
                    (References to respective tables)

┌─────────────────────────────────────────┐
│                HOLIDAY                  │
├─────────────────────────────────────────┤
│ PK  id (BIGINT)                        │
│     date (DATE) NOT NULL               │
│     reason (VARCHAR(255))              │
│     is_recurring (BOOLEAN)             │
│ FK  school_id (BIGINT) NOT NULL        │
└─────────────────────────────────────────┘
```

## 🔑 Key Relationships

### Primary Relationships:
1. **School ↔ Student** (1:N)
   - One school has many students
   - Each student belongs to one school

2. **School ↔ Teacher** (1:N)
   - One school has many teachers
   - Each teacher belongs to one school

3. **Student ↔ AttendanceRecord** (1:N)
   - One student has many attendance records
   - Each attendance record belongs to one student

4. **Teacher ↔ AttendanceRecord** (1:N) [Optional]
   - One teacher can mark many attendance records
   - Each attendance record can optionally reference who marked it

5. **Teacher ↔ Subjects** (1:N)
   - One teacher can teach multiple subjects
   - Stored in separate junction table

6. **Teacher ↔ Classes** (1:N)
   - One teacher can be assigned to multiple classes
   - Stored in separate junction table

### Authentication Relationships:
1. **User ↔ Teacher** (1:0..1)
   - A user account can optionally reference a teacher

2. **User ↔ Student** (1:0..1)
   - A user account can optionally reference a student

3. **User ↔ School** (1:0..1)
   - A user account can optionally reference a school (for admin users)

## 📝 Business Rules & Constraints

### Unique Constraints:
- `(gr_no, school_id)` - GR Number must be unique within a school
- `(roll_no, standard, school_id)` - Roll Number must be unique within a class
- `(emp_no, school_id)` - Employee Number must be unique within a school
- `(date, student_id)` - Only one attendance record per student per day
- `username` - Must be globally unique
- `email` - Must be globally unique

### Check Constraints:
- `date_of_birth` must be in the past
- `status` must be one of: PRESENT, ABSENT, LATE, HALF_DAY, HOLIDAY, SICK_LEAVE
- `gender` must be one of: MALE, FEMALE, OTHER
- `role` must be one of: ADMIN, TEACHER, STUDENT

### Foreign Key Constraints:
- All foreign keys have CASCADE DELETE for data integrity
- `teacher_id` in attendance_record is optional (can be NULL)

## 📊 Indexes for Performance

### Primary Indexes:
- Primary keys on all tables (auto-indexed)

### Secondary Indexes:
```sql
-- Student search optimization
CREATE INDEX idx_student_name ON students(first_name, last_name);
CREATE INDEX idx_student_class ON students(standard, section);
CREATE INDEX idx_student_school ON students(school_id);

-- Teacher search optimization
CREATE INDEX idx_teacher_name ON teachers(first_name, last_name);
CREATE INDEX idx_teacher_subject ON teacher_subjects(subject);

-- Attendance query optimization
CREATE INDEX idx_attendance_date ON attendance_records(date);
CREATE INDEX idx_attendance_student_date ON attendance_records(student_id, date);
CREATE INDEX idx_attendance_class_date ON attendance_records(date) 
    WHERE student_id IN (SELECT id FROM students WHERE standard = ? AND section = ?);

-- User authentication optimization
CREATE INDEX idx_user_username ON users(username);
CREATE INDEX idx_user_email ON users(email);
```

## 🔢 Estimated Data Volume

### Small School (500 students, 50 teachers):
- **Students**: 500 records
- **Teachers**: 50 records
- **Attendance Records**: ~90,000 records/year (500 students × 180 school days)
- **Users**: ~550 records

### Medium School (2000 students, 200 teachers):
- **Students**: 2,000 records
- **Teachers**: 200 records
- **Attendance Records**: ~360,000 records/year
- **Users**: ~2,200 records

### Large School (5000 students, 500 teachers):
- **Students**: 5,000 records
- **Teachers**: 500 records
- **Attendance Records**: ~900,000 records/year
- **Users**: ~5,500 records

## 🔄 Data Lifecycle

### Daily Operations:
1. **Morning**: Teachers mark attendance for their classes
2. **Throughout Day**: Late arrivals and corrections
3. **Evening**: Generate daily reports

### Monthly Operations:
1. **Month End**: Generate monthly attendance reports
2. **Archive**: Move old attendance data to archive tables (optional)

### Yearly Operations:
1. **Academic Year End**: 
   - Archive student/teacher data for graduated/transferred students
   - Prepare for new academic year setup
   - Generate annual reports

## 💾 Backup Strategy

### Daily Backups:
- Full database backup every night
- Transaction log backups every hour

### Weekly Backups:
- Export critical data (students, teachers, current month attendance)
- Store in multiple locations

### Monthly Backups:
- Full system backup including file uploads
- Archive older data if needed

---

**Document Version**: 1.0  
**Last Updated**: September 10, 2025  
**Author**: GitHub Copilot  
**Status**: Complete
