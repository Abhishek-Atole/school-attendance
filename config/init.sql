-- School Attendance Database Initialization Script
-- This script will be executed when the MariaDB container starts for the first time

-- Create database if not exists (though docker-compose already handles this)
CREATE DATABASE IF NOT EXISTS school_attendance CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Use the database
USE school_attendance;

-- Grant privileges to the application user
GRANT ALL PRIVILEGES ON school_attendance.* TO 'attendance_user'@'%';
FLUSH PRIVILEGES;

-- Create indexes for better performance (these will be created by Hibernate as well)
-- The application will handle table creation through JPA/Hibernate

-- Optional: Insert initial admin user data
-- This can be uncommented and customized as needed
/*
INSERT IGNORE INTO users (id, username, password, email, role, created_at) VALUES 
(1, 'admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'admin@schoolattendance.com', 'ADMIN', NOW());

INSERT IGNORE INTO schools (id, name, address, phone, email, created_at) VALUES 
(1, 'Demo School', '123 Education Street', '+1234567890', 'info@demoschool.com', NOW());
*/

-- Performance optimizations
SET GLOBAL innodb_buffer_pool_size = 256M;
SET GLOBAL max_connections = 200;
SET GLOBAL wait_timeout = 3600;
SET GLOBAL interactive_timeout = 3600;
