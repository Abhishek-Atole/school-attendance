package com.school.attendance.repository;

import com.school.attendance.entity.NotificationSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationSettingsRepository extends JpaRepository<NotificationSettings, Long> {

    // Get settings by school
    Optional<NotificationSettings> findBySchoolId(Long schoolId);

    // Check if settings exist for school
    boolean existsBySchoolId(Long schoolId);
}
