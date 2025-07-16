package com.tasktracker.notification.repository;

import com.tasktracker.notification.model.Notification;
import com.tasktracker.notification.model.NotificationStatus;
import com.tasktracker.notification.model.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    
    Page<Notification> findByUserId(Long userId, Pageable pageable);
    
    Page<Notification> findByUserIdAndStatus(Long userId, NotificationStatus status, Pageable pageable);
    
    Page<Notification> findByUserIdAndType(Long userId, NotificationType type, Pageable pageable);
    
    List<Notification> findByStatusAndRetryCountLessThan(NotificationStatus status, Integer maxRetries);
    
    List<Notification> findByStatusAndCreatedAtBefore(NotificationStatus status, LocalDateTime createdBefore);
    
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.readAt IS NULL")
    List<Notification> findUnreadByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.userId = :userId AND n.readAt IS NULL")
    Long countUnreadByUserId(@Param("userId") Long userId);
    
    @Query("SELECT n FROM Notification n WHERE n.status = :status AND n.createdAt < :threshold")
    List<Notification> findOldNotifications(@Param("status") NotificationStatus status, @Param("threshold") LocalDateTime threshold);
    
    void deleteByUserIdAndCreatedAtBefore(Long userId, LocalDateTime createdBefore);
} 