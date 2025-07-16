package com.tasktracker.notification.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_notification_settings")
public class UserNotificationSettings {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @Column(nullable = false, unique = true)
    private Long userId;
    
    @Column(nullable = false)
    private String email;
    
    @Column(name = "email_notifications", nullable = false)
    private Boolean emailNotifications = true;
    
    @Column(name = "push_notifications", nullable = false)
    private Boolean pushNotifications = true;
    
    @Column(name = "sms_notifications", nullable = false)
    private Boolean smsNotifications = false;
    
    @Column(name = "task_created", nullable = false)
    private Boolean taskCreated = true;
    
    @Column(name = "task_updated", nullable = false)
    private Boolean taskUpdated = true;
    
    @Column(name = "task_deleted", nullable = false)
    private Boolean taskDeleted = true;
    
    @Column(name = "task_assigned", nullable = false)
    private Boolean taskAssigned = true;
    
    @Column(name = "task_completed", nullable = false)
    private Boolean taskCompleted = true;
    
    @Column(name = "task_overdue", nullable = false)
    private Boolean taskOverdue = true;
    
    @Column(name = "system_alerts", nullable = false)
    private Boolean systemAlerts = true;
    
    @Column(name = "reminders", nullable = false)
    private Boolean reminders = true;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public UserNotificationSettings() {}
    
    public UserNotificationSettings(Long userId, String email) {
        this.userId = userId;
        this.email = email;
        this.createdAt = LocalDateTime.now();
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
    
    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 