package com.tasktracker.notification.repository;

import com.tasktracker.notification.model.UserNotificationSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserNotificationSettingsRepository extends JpaRepository<UserNotificationSettings, UUID> {
    
    Optional<UserNotificationSettings> findByUserId(Long userId);
    
    Optional<UserNotificationSettings> findByEmail(String email);
    
    boolean existsByUserId(Long userId);
    
    boolean existsByEmail(String email);
    
    void deleteByUserId(Long userId);
} 