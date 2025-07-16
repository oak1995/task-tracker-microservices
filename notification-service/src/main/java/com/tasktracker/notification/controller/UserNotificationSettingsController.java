package com.tasktracker.notification.controller;

import com.tasktracker.notification.dto.UserNotificationSettingsDto;
import com.tasktracker.notification.service.UserNotificationSettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification-settings")
@Tag(name = "Notification Settings", description = "User notification settings management API")
public class UserNotificationSettingsController {
    
    private final UserNotificationSettingsService settingsService;
    
    public UserNotificationSettingsController(UserNotificationSettingsService settingsService) {
        this.settingsService = settingsService;
    }
    
    @PostMapping("/user/{userId}")
    @Operation(summary = "Create default notification settings", description = "Creates default notification settings for a user")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM') or #userId == authentication.principal.id")
    public ResponseEntity<UserNotificationSettingsDto> createDefaultSettings(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "User email") @RequestParam String email) {
        
        UserNotificationSettingsDto settings = settingsService.createDefaultSettings(userId, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(settings);
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user notification settings", description = "Gets notification settings for a specific user")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<UserNotificationSettingsDto> getUserSettings(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        
        UserNotificationSettingsDto settings = settingsService.getUserSettings(userId);
        if (settings == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(settings);
    }
    
    @PutMapping("/user/{userId}")
    @Operation(summary = "Update user notification settings", description = "Updates notification settings for a specific user")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<UserNotificationSettingsDto> updateUserSettings(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Valid @RequestBody UserNotificationSettingsDto settingsDto) {
        
        try {
            UserNotificationSettingsDto updatedSettings = settingsService.updateUserSettings(userId, settingsDto);
            return ResponseEntity.ok(updatedSettings);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/user/{userId}")
    @Operation(summary = "Delete user notification settings", description = "Deletes notification settings for a specific user")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Void> deleteUserSettings(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        
        settingsService.deleteUserSettings(userId);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/user/{userId}/email-notifications")
    @Operation(summary = "Toggle email notifications", description = "Enables or disables email notifications for a user")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<UserNotificationSettingsDto> toggleEmailNotifications(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Enable email notifications") @RequestParam boolean enabled) {
        
        UserNotificationSettingsDto currentSettings = settingsService.getUserSettings(userId);
        if (currentSettings == null) {
            return ResponseEntity.notFound().build();
        }
        
        currentSettings.setEmailNotifications(enabled);
        UserNotificationSettingsDto updatedSettings = settingsService.updateUserSettings(userId, currentSettings);
        return ResponseEntity.ok(updatedSettings);
    }
    
    @PutMapping("/user/{userId}/push-notifications")
    @Operation(summary = "Toggle push notifications", description = "Enables or disables push notifications for a user")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<UserNotificationSettingsDto> togglePushNotifications(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Enable push notifications") @RequestParam boolean enabled) {
        
        UserNotificationSettingsDto currentSettings = settingsService.getUserSettings(userId);
        if (currentSettings == null) {
            return ResponseEntity.notFound().build();
        }
        
        currentSettings.setPushNotifications(enabled);
        UserNotificationSettingsDto updatedSettings = settingsService.updateUserSettings(userId, currentSettings);
        return ResponseEntity.ok(updatedSettings);
    }
    
    @PutMapping("/user/{userId}/task-notifications")
    @Operation(summary = "Update task notification preferences", description = "Updates task-related notification preferences")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<UserNotificationSettingsDto> updateTaskNotifications(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Task created notifications") @RequestParam(required = false) Boolean taskCreated,
            @Parameter(description = "Task updated notifications") @RequestParam(required = false) Boolean taskUpdated,
            @Parameter(description = "Task assigned notifications") @RequestParam(required = false) Boolean taskAssigned,
            @Parameter(description = "Task completed notifications") @RequestParam(required = false) Boolean taskCompleted,
            @Parameter(description = "Task overdue notifications") @RequestParam(required = false) Boolean taskOverdue) {
        
        UserNotificationSettingsDto currentSettings = settingsService.getUserSettings(userId);
        if (currentSettings == null) {
            return ResponseEntity.notFound().build();
        }
        
        if (taskCreated != null) {
            currentSettings.setTaskCreated(taskCreated);
        }
        if (taskUpdated != null) {
            currentSettings.setTaskUpdated(taskUpdated);
        }
        if (taskAssigned != null) {
            currentSettings.setTaskAssigned(taskAssigned);
        }
        if (taskCompleted != null) {
            currentSettings.setTaskCompleted(taskCompleted);
        }
        if (taskOverdue != null) {
            currentSettings.setTaskOverdue(taskOverdue);
        }
        
        UserNotificationSettingsDto updatedSettings = settingsService.updateUserSettings(userId, currentSettings);
        return ResponseEntity.ok(updatedSettings);
    }
} 