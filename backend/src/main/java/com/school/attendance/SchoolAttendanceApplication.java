package com.school.attendance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

// Temporarily removed JPA annotations to debug entityManagerFactory issue
// @EnableJpaRepositories(basePackages = "com.school.attendance.repository")
// @EntityScan(basePackages = "com.school.attendance.entity")
@SpringBootApplication
@EnableScheduling
public class SchoolAttendanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchoolAttendanceApplication.class, args);
    }
}
