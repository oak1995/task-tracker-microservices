package com.tasktracker.task.dto;

import java.time.LocalDateTime;

/**
 * DTO для ответа с данными комментария
 * 
 * @author Orazbakhov Aibek
 * @version 1.0
 */
public class CommentResponse {
    
    private Long id;
    private String content;
    private Long taskId;
    private Long authorUserId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public CommentResponse() {}
    
    public CommentResponse(Long id, String content, Long taskId, Long authorUserId) {
        this.id = id;
        this.content = content;
        this.taskId = taskId;
        this.authorUserId = authorUserId;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Long getTaskId() {
        return taskId;
    }
    
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }
    
    public Long getAuthorUserId() {
        return authorUserId;
    }
    
    public void setAuthorUserId(Long authorUserId) {
        this.authorUserId = authorUserId;
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
        return "CommentResponse{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", taskId=" + taskId +
                ", authorUserId=" + authorUserId +
                ", createdAt=" + createdAt +
                '}';
    }
} 