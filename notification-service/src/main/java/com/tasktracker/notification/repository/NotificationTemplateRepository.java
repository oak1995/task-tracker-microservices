package com.tasktracker.notification.repository;

import com.tasktracker.notification.model.NotificationTemplate;
import com.tasktracker.notification.model.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, UUID> {
    
    Optional<NotificationTemplate> findByName(String name);
    
    Optional<NotificationTemplate> findByTypeAndChannel(NotificationType type, String channel);
    
    List<NotificationTemplate> findByType(NotificationType type);
    
    List<NotificationTemplate> findByChannel(String channel);
    
    List<NotificationTemplate> findByIsActiveTrue();
    
    List<NotificationTemplate> findByTypeAndIsActiveTrue(NotificationType type);
    
    List<NotificationTemplate> findByChannelAndIsActiveTrue(String channel);
    
    boolean existsByName(String name);
} 