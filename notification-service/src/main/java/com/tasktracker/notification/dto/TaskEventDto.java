package com.tasktracker.notification.dto;

import java.time.LocalDateTime;

public class TaskEventDto {
    
    private String eventType; // CREATED, UPDATED, DELETED, ASSIGNED, COMPLETED
    private Long taskId;
    private String title;
    private String description;
    private String priority;
    private String status;
    private Long assignedUserId;
    private Long createdByUserId;
    private LocalDateTime dueDate;
    private LocalDateTime eventTime;
    private String metadata;
    
    // Constructors
    public TaskEventDto() {}
    
    public TaskEventDto(String eventType, Long taskId, String title, Long assignedUserId, LocalDateTime eventTime) {
        this.eventType = eventType;
        this.taskId = taskId;
        this.title = title;
        this.assignedUserId = assignedUserId;
        this.eventTime = eventTime;
    }
    
    // Getters and Setters
    public String getEventType() {
        return eventType;
    }
    
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    
    public Long getTaskId() {
        return taskId;
    }
    
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getPriority() {
        return priority;
    }
    
    public void setPriority(String priority) {
        this.priority = priority;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Long getAssignedUserId() {
        return assignedUserId;
    }
    
    public void setAssignedUserId(Long assignedUserId) {
        this.assignedUserId = assignedUserId;
    }
    
    public Long getCreatedByUserId() {
        return createdByUserId;
    }
    
    public void setCreatedByUserId(Long createdByUserId) {
        this.createdByUserId = createdByUserId;
    }
    
    public LocalDateTime getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }
    
    public LocalDateTime getEventTime() {
        return eventTime;
    }
    
    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
    }
    
    public String getMetadata() {
        return metadata;
    }
    
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
} 