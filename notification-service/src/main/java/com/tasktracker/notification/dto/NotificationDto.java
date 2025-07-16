package com.tasktracker.notification.dto;

import com.tasktracker.notification.model.NotificationStatus;
import com.tasktracker.notification.model.NotificationType;

import java.time.LocalDateTime;
import java.util.UUID;

public class NotificationDto {
    
    private UUID id;
    private Long userId;
    private String title;
    private String content;
    private NotificationType type;
    private NotificationStatus status;
    private String channel;
    private String recipientEmail;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
    private LocalDateTime readAt;
    private String errorMessage;
    private Integer retryCount;
    private String metadata;
    
    // Constructors
    public NotificationDto() {}
    
    public NotificationDto(UUID id, Long userId, String title, String content, NotificationType type, 
                          NotificationStatus status, String channel, String recipientEmail,
                          LocalDateTime createdAt, LocalDateTime sentAt, LocalDateTime readAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.type = type;
        this.status = status;
        this.channel = channel;
        this.recipientEmail = recipientEmail;
        this.createdAt = createdAt;
        this.sentAt = sentAt;
        this.readAt = readAt;
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
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public NotificationType getType() {
        return type;
    }
    
    public void setType(NotificationType type) {
        this.type = type;
    }
    
    public NotificationStatus getStatus() {
        return status;
    }
    
    public void setStatus(NotificationStatus status) {
        this.status = status;
    }
    
    public String getChannel() {
        return channel;
    }
    
    public void setChannel(String channel) {
        this.channel = channel;
    }
    
    public String getRecipientEmail() {
        return recipientEmail;
    }
    
    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getSentAt() {
        return sentAt;
    }
    
    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
    
    public LocalDateTime getReadAt() {
        return readAt;
    }
    
    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public Integer getRetryCount() {
        return retryCount;
    }
    
    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }
    
    public String getMetadata() {
        return metadata;
    }
    
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
} 