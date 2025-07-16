package com.tasktracker.task.dto;

import com.tasktracker.task.entity.TaskPriority;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

/**
 * DTO для создания новой задачи
 * 
 * @author Orazbakhov Aibek
 * @version 1.0
 */
public class TaskCreateRequest {
    
    @NotBlank(message = "Название задачи не может быть пустым")
    @Size(min = 3, max = 200, message = "Название задачи должно быть от 3 до 200 символов")
    private String title;
    
    @Size(max = 2000, message = "Описание не может превышать 2000 символов")
    private String description;
    
    @NotNull(message = "Приоритет задачи обязателен")
    private TaskPriority priority;
    
    private Long categoryId;
    
    private Long assignedToUserId;
    
    private LocalDateTime dueDate;
    
    @DecimalMin(value = "0.1", message = "Оценка времени должна быть больше 0")
    @DecimalMax(value = "9999.0", message = "Оценка времени не может превышать 9999 часов")
    private Double estimatedHours;
    
    // Constructors
    public TaskCreateRequest() {}
    
    public TaskCreateRequest(String title, String description, TaskPriority priority) {
        this.title = title;
        this.description = description;
        this.priority = priority;
    }
    
    // Getters and Setters
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
    
    public TaskPriority getPriority() {
        return priority;
    }
    
    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }
    
    public Long getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
    
    public Long getAssignedToUserId() {
        return assignedToUserId;
    }
    
    public void setAssignedToUserId(Long assignedToUserId) {
        this.assignedToUserId = assignedToUserId;
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
    
    @Override
    public String toString() {
        return "TaskCreateRequest{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", priority=" + priority +
                ", categoryId=" + categoryId +
                ", assignedToUserId=" + assignedToUserId +
                ", dueDate=" + dueDate +
                ", estimatedHours=" + estimatedHours +
                '}';
    }
} 