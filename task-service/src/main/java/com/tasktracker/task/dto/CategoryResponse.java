package com.tasktracker.task.dto;

import java.time.LocalDateTime;

/**
 * DTO для ответа с данными категории
 * 
 * @author Orazbakhov Aibek
 * @version 1.0
 */
public class CategoryResponse {
    
    private Long id;
    private String name;
    private String description;
    private String colorCode;
    private Boolean isActive;
    private int taskCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public CategoryResponse() {}
    
    public CategoryResponse(Long id, String name, String description, String colorCode) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.colorCode = colorCode;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getColorCode() {
        return colorCode;
    }
    
    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public int getTaskCount() {
        return taskCount;
    }
    
    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
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
    
    @Override
    public String toString() {
        return "CategoryResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", colorCode='" + colorCode + '\'' +
                ", isActive=" + isActive +
                ", taskCount=" + taskCount +
                ", createdAt=" + createdAt +
                '}';
    }
} 