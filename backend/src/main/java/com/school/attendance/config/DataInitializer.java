package com.school.attendance.config;

import com.school.attendance.entity.Role;
import com.school.attendance.entity.User;
import com.school.attendance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// @Component  // Temporarily disabled to resolve startup issues
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            System.out.println("Initializing default users...");

            // Create Admin user
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@school.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            admin.setFullName("System Administrator");
            userRepository.save(admin);

            // Create Teacher user
            User teacher = new User();
            teacher.setUsername("teacher");
            teacher.setEmail("teacher@school.com");
            teacher.setPassword(passwordEncoder.encode("teacher123"));
            teacher.setRole(Role.TEACHER);
            teacher.setFullName("John Teacher");
            userRepository.save(teacher);

            // Create Student user
            User student = new User();
            student.setUsername("student");
            student.setEmail("student@school.com");
            student.setPassword(passwordEncoder.encode("student123"));
            student.setRole(Role.STUDENT);
            student.setFullName("Jane Student");
            userRepository.save(student);

            System.out.println("Default users created successfully!");
            System.out.println("Admin - username: admin, password: admin123");
            System.out.println("Teacher - username: teacher, password: teacher123");
            System.out.println("Student - username: student, password: student123");
        }
    }
}
