package com.tasktracker.task.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Основная сущность задачи
 * 
 * @author Orazbakhov Aibek
 * @version 1.0
 */
@Entity
@Table(name = "tasks")
public class Task {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Название задачи не может быть пустым")
    @Size(min = 3, max = 200, message = "Название задачи должно быть от 3 до 200 символов")
    @Column(nullable = false)
    private String title;
    
    @Size(max = 2000, message = "Описание не может превышать 2000 символов")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.NEW;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskPriority priority = TaskPriority.MEDIUM;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    
    @Column(name = "assigned_to_user_id")
    private Long assignedToUserId;
    
    @Column(name = "created_by_user_id", nullable = false)
    private Long createdByUserId;
    
    @Column(name = "due_date")
    private LocalDateTime dueDate;
    
    @Column(name = "estimated_hours")
    private Double estimatedHours;
    
    @Column(name = "actual_hours")
    private Double actualHours;
    
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public Task() {}
    
    public Task(String title, String description, TaskPriority priority, Long createdByUserId) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.createdByUserId = createdByUserId;
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
    
    public Category getCategory() {
        return category;
    }
    
    public void setCategory(Category category) {
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
    
    public List<Comment> getComments() {
        return comments;
    }
    
    public void setComments(List<Comment> comments) {
        this.comments = comments;
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
    
    // Business methods
    public void assignTo(Long userId) {
        this.assignedToUserId = userId;
        if (this.status == TaskStatus.NEW) {
            this.status = TaskStatus.IN_PROGRESS;
        }
    }
    
    public void markAsCompleted() {
        this.status = TaskStatus.COMPLETED;
    }
    
    public void markAsCancelled() {
        this.status = TaskStatus.CANCELLED;
    }
    
    public boolean isOverdue() {
        return dueDate != null && dueDate.isBefore(LocalDateTime.now()) && !status.isFinal();
    }
    
    public boolean isAssigned() {
        return assignedToUserId != null;
    }
    
    public int getCommentsCount() {
        return comments != null ? comments.size() : 0;
    }
    
    @Override
    public String toString() {
        return "Task{" +
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