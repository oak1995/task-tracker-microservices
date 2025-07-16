package com.tasktracker.task.dto;

import com.tasktracker.task.entity.TaskStatus;
import com.tasktracker.task.entity.TaskPriority;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO для ответа с данными задачи
 * 
 * @author Orazbakhov Aibek
 * @version 1.0
 */
public class TaskResponse {
    
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private CategoryResponse category;
    private Long assignedToUserId;
    private Long createdByUserId;
    private LocalDateTime dueDate;
    private Double estimatedHours;
    private Double actualHours;
    private int commentsCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public TaskResponse() {}
    
    public TaskResponse(Long id, String title, String description, TaskStatus status, TaskPriority priority) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public TaskStatus getStatus() {
        return status;
    }
    
    public void setStatus(TaskStatus status) {
        this.status = status;
    }
    
    public TaskPriority getPriority() {
        return priority;
    }
    
    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }
    
    public CategoryResponse getCategory() {
        return category;
    }
    
    public void setCategory(CategoryResponse category) {
        this.category = category;
    }
    
    public Long getAssignedToUserId() {
        return assignedToUserId;
    }
    
    public void setAssignedToUserId(Long assignedToUserId) {
        this.assignedToUserId = assignedToUserId;
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
    
    public Double getEstimatedHours() {
        return estimatedHours;
    }
    
    public void setEstimatedHours(Double estimatedHours) {
        this.estimatedHours = estimatedHours;
    }
    
    public Double getActualHours() {
        return actualHours;
    }
    
    public void setActualHours(Double actualHours) {
        this.actualHours = actualHours;
    }
    
    public int getCommentsCount() {
        return commentsCount;
    }
    
    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
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
    
    // Helper methods
    public boolean isOverdue() {
        return dueDate != null && dueDate.isBefore(LocalDateTime.now()) && !status.isFinal();
    }
    
    public boolean isAssigned() {
        return assignedToUserId != null;
    }
    
    public boolean hasComments() {
        return commentsCount > 0;
    }
    
    @Override
    public String toString() {
        return "TaskResponse{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", priority=" + priority +
                ", assignedToUserId=" + assignedToUserId +
                ", dueDate=" + dueDate +
                ", createdAt=" + createdAt +
                '}';
    }
} 