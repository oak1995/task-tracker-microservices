package com.tasktracker.notification.service;

import com.tasktracker.notification.dto.UserNotificationSettingsDto;
import com.tasktracker.notification.model.NotificationType;
import com.tasktracker.notification.model.UserNotificationSettings;
import com.tasktracker.notification.repository.UserNotificationSettingsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserNotificationSettingsService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserNotificationSettingsService.class);
    
    private final UserNotificationSettingsRepository settingsRepository;
    
    public UserNotificationSettingsService(UserNotificationSettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }
    
    public UserNotificationSettingsDto createDefaultSettings(Long userId, String email) {
        logger.info("Creating default notification settings for user: {}", userId);
        
        if (settingsRepository.existsByUserId(userId)) {
            logger.warn("Settings already exist for user: {}", userId);
            return getUserSettings(userId);
        }
        
        UserNotificationSettings settings = new UserNotificationSettings(userId, email);
        settings = settingsRepository.save(settings);
        
        logger.info("Default notification settings created for user: {}", userId);
        return convertToDto(settings);
    }
    
    @Transactional(readOnly = true)
    public UserNotificationSettingsDto getUserSettings(Long userId) {
        Optional<UserNotificationSettings> settings = settingsRepository.findByUserId(userId);
        if (settings.isPresent()) {
            return convertToDto(settings.get());
        }
        
        logger.warn("No notification settings found for user: {}", userId);
        return null;
    }
    
    public UserNotificationSettingsDto updateUserSettings(Long userId, UserNotificationSettingsDto settingsDto) {
        logger.info("Updating notification settings for user: {}", userId);
        
        Optional<UserNotificationSettings> existingSettings = settingsRepository.findByUserId(userId);
        if (existingSettings.isEmpty()) {
            logger.error("No settings found for user: {}", userId);
            throw new RuntimeException("User settings not found");
        }
        
        UserNotificationSettings settings = existingSettings.get();
        updateSettingsFromDto(settings, settingsDto);
        
        settings = settingsRepository.save(settings);
        logger.info("Notification settings updated for user: {}", userId);
        
        return convertToDto(settings);
    }
    
    @Transactional(readOnly = true)
    public boolean isNotificationEnabled(Long userId, NotificationType type, String channel) {
        Optional<UserNotificationSettings> settings = settingsRepository.findByUserId(userId);
        if (settings.isEmpty()) {
            logger.warn("No settings found for user: {}, defaulting to enabled", userId);
            return true; // Default to enabled if no settings found
        }
        
        UserNotificationSettings userSettings = settings.get();
        
        // Check channel preferences
        boolean channelEnabled = switch (channel) {
            case "EMAIL" -> userSettings.getEmailNotifications();
            case "PUSH" -> userSettings.getPushNotifications();
            case "SMS" -> userSettings.getSmsNotifications();
            default -> true; // Default to enabled for unknown channels
        };
        
        if (!channelEnabled) {
            return false;
        }
        
        // Check notification type preferences
        return switch (type) {
            case TASK_CREATED -> userSettings.getTaskCreated();
            case TASK_UPDATED -> userSettings.getTaskUpdated();
            case TASK_DELETED -> userSettings.getTaskDeleted();
            case TASK_ASSIGNED -> userSettings.getTaskAssigned();
            case TASK_COMPLETED -> userSettings.getTaskCompleted();
            case TASK_OVERDUE -> userSettings.getTaskOverdue();
            case SYSTEM_ALERT -> userSettings.getSystemAlerts();
            case REMINDER -> userSettings.getReminders();
            default -> true; // Default to enabled for unknown types
        };
    }
    
    @Transactional(readOnly = true)
    public String getUserEmail(Long userId) {
        Optional<UserNotificationSettings> settings = settingsRepository.findByUserId(userId);
        if (settings.isPresent()) {
            return settings.get().getEmail();
        }
        
        logger.warn("No email found for user: {}", userId);
        return null;
    }
    
    public void deleteUserSettings(Long userId) {
        logger.info("Deleting notification settings for user: {}", userId);
        settingsRepository.deleteByUserId(userId);
    }
    
    private UserNotificationSettingsDto convertToDto(UserNotificationSettings settings) {
        UserNotificationSettingsDto dto = new UserNotificationSettingsDto();
        dto.setId(settings.getId());
        dto.setUserId(settings.getUserId());
        dto.setEmail(settings.getEmail());
        dto.setEmailNotifications(settings.getEmailNotifications());
        dto.setPushNotifications(settings.getPushNotifications());
        dto.setSmsNotifications(settings.getSmsNotifications());
        dto.setTaskCreated(settings.getTaskCreated());
        dto.setTaskUpdated(settings.getTaskUpdated());
        dto.setTaskDeleted(settings.getTaskDeleted());
        dto.setTaskAssigned(settings.getTaskAssigned());
        dto.setTaskCompleted(settings.getTaskCompleted());
        dto.setTaskOverdue(settings.getTaskOverdue());
        dto.setSystemAlerts(settings.getSystemAlerts());
        dto.setReminders(settings.getReminders());
        dto.setCreatedAt(settings.getCreatedAt());
        dto.setUpdatedAt(settings.getUpdatedAt());
        return dto;
    }
    
    private void updateSettingsFromDto(UserNotificationSettings settings, UserNotificationSettingsDto dto) {
        if (dto.getEmail() != null) {
            settings.setEmail(dto.getEmail());
        }
        if (dto.getEmailNotifications() != null) {
            settings.setEmailNotifications(dto.getEmailNotifications());
        }
        if (dto.getPushNotifications() != null) {
            settings.setPushNotifications(dto.getPushNotifications());
        }
        if (dto.getSmsNotifications() != null) {
            settings.setSmsNotifications(dto.getSmsNotifications());
        }
        if (dto.getTaskCreated() != null) {
            settings.setTaskCreated(dto.getTaskCreated());
        }
        if (dto.getTaskUpdated() != null) {
            settings.setTaskUpdated(dto.getTaskUpdated());
        }
        if (dto.getTaskDeleted() != null) {
            settings.setTaskDeleted(dto.getTaskDeleted());
        }
        if (dto.getTaskAssigned() != null) {
            settings.setTaskAssigned(dto.getTaskAssigned());
        }
        if (dto.getTaskCompleted() != null) {
            settings.setTaskCompleted(dto.getTaskCompleted());
        }
        if (dto.getTaskOverdue() != null) {
            settings.setTaskOverdue(dto.getTaskOverdue());
        }
        if (dto.getSystemAlerts() != null) {
            settings.setSystemAlerts(dto.getSystemAlerts());
        }
        if (dto.getReminders() != null) {
            settings.setReminders(dto.getReminders());
        }
    }
} 