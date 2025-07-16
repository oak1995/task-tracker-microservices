package com.tasktracker.notification.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserNotificationSettingsDto {
    
    private UUID id;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    private Boolean emailNotifications = true;
    private Boolean pushNotifications = true;
    private Boolean smsNotifications = false;
    private Boolean taskCreated = true;
    private Boolean taskUpdated = true;
    private Boolean taskDeleted = true;
    private Boolean taskAssigned = true;
    private Boolean taskCompleted = true;
    private Boolean taskOverdue = true;
    private Boolean systemAlerts = true;
    private Boolean reminders = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public UserNotificationSettingsDto() {}
    
    public UserNotificationSettingsDto(Long userId, String email) {
        this.userId = userId;
        this.email = email;
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Boolean getEmailNotifications() {
        return emailNotifications;
    }
    
    public void setEmailNotifications(Boolean emailNotifications) {
        this.emailNotifications = emailNotifications;
    }
    
    public Boolean getPushNotifications() {
        return pushNotifications;
    }
    
    public void setPushNotifications(Boolean pushNotifications) {
        this.pushNotifications = pushNotifications;
    }
    
    public Boolean getSmsNotifications() {
        return smsNotifications;
    }
    
    public void setSmsNotifications(Boolean smsNotifications) {
        this.smsNotifications = smsNotifications;
    }
    
    public Boolean getTaskCreated() {
        return taskCreated;
    }
    
    public void setTaskCreated(Boolean taskCreated) {
        this.taskCreated = taskCreated;
    }
    
    public Boolean getTaskUpdated() {
        return taskUpdated;
    }
    
    public void setTaskUpdated(Boolean taskUpdated) {
        this.taskUpdated = taskUpdated;
    }
    
    public Boolean getTaskDeleted() {
        return taskDeleted;
    }
    
    public void setTaskDeleted(Boolean taskDeleted) {
        this.taskDeleted = taskDeleted;
    }
    
    public Boolean getTaskAssigned() {
        return taskAssigned;
    }
    
    public void setTaskAssigned(Boolean taskAssigned) {
        this.taskAssigned = taskAssigned;
    }
    
    public Boolean getTaskCompleted() {
        return taskCompleted;
    }
    
    public void setTaskCompleted(Boolean taskCompleted) {
        this.taskCompleted = taskCompleted;
    }
    
    public Boolean getTaskOverdue() {
        return taskOverdue;
    }
    
    public void setTaskOverdue(Boolean taskOverdue) {
        this.taskOverdue = taskOverdue;
    }
    
    public Boolean getSystemAlerts() {
        return systemAlerts;
    }
    
    public void setSystemAlerts(Boolean systemAlerts) {
        this.systemAlerts = systemAlerts;
    }
    
    public Boolean getReminders() {
        return reminders;
    }
    
    public void setReminders(Boolean reminders) {
        this.reminders = reminders;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
} 