package com.tasktracker.notification.dto;

import java.time.LocalDateTime;

public class AuthEventDto {
    
    private String eventType; // USER_REGISTERED, USER_LOGIN, USER_LOGOUT, PASSWORD_RESET
    private Long userId;
    private String username;
    private String email;
    private String role;
    private LocalDateTime eventTime;
    private String ipAddress;
    private String userAgent;
    private String metadata;
    
    // Constructors
    public AuthEventDto() {}
    
    public AuthEventDto(String eventType, Long userId, String username, String email, LocalDateTime eventTime) {
        this.eventType = eventType;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.eventTime = eventTime;
    }
    
    // Getters and Setters
    public String getEventType() {
        return eventType;
    }
    
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public LocalDateTime getEventTime() {
        return eventTime;
    }
    
    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    public String getMetadata() {
        return metadata;
    }
    
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
} 