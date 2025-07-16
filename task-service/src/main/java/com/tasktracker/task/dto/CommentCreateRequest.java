package com.tasktracker.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO для создания комментария
 * 
 * @author Orazbakhov Aibek
 * @version 1.0
 */
public class CommentCreateRequest {
    
    @NotBlank(message = "Комментарий не может быть пустым")
    @Size(min = 1, max = 1000, message = "Комментарий должен быть от 1 до 1000 символов")
    private String content;
    
    // Constructors
    public CommentCreateRequest() {}
    
    public CommentCreateRequest(String content) {
        this.content = content;
    }
    
    // Getters and Setters
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    @Override
    public String toString() {
        return "CommentCreateRequest{" +
                "content='" + content + '\'' +
                '}';
    }
} 