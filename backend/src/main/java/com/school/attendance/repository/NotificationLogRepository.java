package com.school.attendance.repository;

import com.school.attendance.entity.NotificationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {

    // Get last 50 notifications
    Page<NotificationLog> findAllByOrderBySentAtDesc(Pageable pageable);

    // Get notifications by school
    Page<NotificationLog> findBySchoolIdOrderBySentAtDesc(Long schoolId, Pageable pageable);

    // Get notifications by type
    List<NotificationLog> findByTypeAndSentAtBetween(
        NotificationLog.NotificationType type, 
        LocalDateTime startDate, 
        LocalDateTime endDate
    );

    // Get notifications by status
    List<NotificationLog> findByStatus(NotificationLog.NotificationStatus status);

    // Count notifications by status for today
    @Query("SELECT COUNT(n) FROM NotificationLog n WHERE n.status = :status AND DATE(n.sentAt) = CURRENT_DATE")
    Long countByStatusToday(@Param("status") NotificationLog.NotificationStatus status);

    // Get failed notifications for retry
    List<NotificationLog> findByStatusAndSentAtBefore(
        NotificationLog.NotificationStatus status, 
        LocalDateTime dateTime
    );
    /**
     * Count notifications by status
     */
    long countByStatus(NotificationLog.NotificationStatus status);

    /**
     * Count notifications by type
     */
    long countByType(NotificationLog.NotificationType type);

    /**
     * Find notifications by type
     */
    List<NotificationLog> findByType(NotificationLog.NotificationType type);
}
