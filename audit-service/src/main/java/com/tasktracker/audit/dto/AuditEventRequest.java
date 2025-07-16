package com.tasktracker.audit.dto;

import com.tasktracker.audit.entity.AuditAction;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO для создания событий аудита
 */
public class AuditEventRequest {
    
    @NotNull(message = "ID пользователя не может быть null")
    private Long userId;
    
    @NotNull(message = "Имя пользователя не может быть null")
    @Size(min = 2, max = 50, message = "Имя пользователя должно быть от 2 до 50 символов")
    private String username;
    
    @NotNull(message = "Действие не может быть null")
    private AuditAction action;
    
    @Size(max = 100, message = "Тип сущности не может быть длиннее 100 символов")
    private String entityType;
    
    private Long entityId;
    
    @Size(max = 45, message = "IP адрес не может быть длиннее 45 символов")
    private String ipAddress;
    
    @Size(max = 500, message = "User agent не может быть длиннее 500 символов")
    private String userAgent;
    
    @Size(max = 100, message = "ID сессии не может быть длиннее 100 символов")
    private String sessionId;
    
    @Size(max = 1000, message = "Описание не может быть длиннее 1000 символов")
    private String description;
    
    private String oldValues;
    
    private String newValues;
    
    private Boolean isSuccess = true;
    
    @Size(max = 500, message = "Сообщение об ошибке не может быть длиннее 500 символов")
    private String errorMessage;
    
    @Size(max = 100, message = "Имя сервиса не может быть длиннее 100 символов")
    private String serviceName;
    
    @Size(max = 100, message = "Имя метода не может быть длиннее 100 символов")
    private String methodName;
    
    private Long executionTimeMs;
    
    // Конструкторы
    public AuditEventRequest() {}
    
    public AuditEventRequest(Long userId, String username, AuditAction action) {
        this.userId = userId;
        this.username = username;
        this.action = action;
    }
    
    // Геттеры и сеттеры
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
    
    public AuditAction getAction() {
        return action;
    }
    
    public void setAction(AuditAction action) {
        this.action = action;
    }
    
    public String getEntityType() {
        return entityType;
    }
    
    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }
    
    public Long getEntityId() {
        return entityId;
    }
    
    public void setEntityId(Long entityId) {
        this.entityId = entityId;
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
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getOldValues() {
        return oldValues;
    }
    
    public void setOldValues(String oldValues) {
        this.oldValues = oldValues;
    }
    
    public String getNewValues() {
        return newValues;
    }
    
    public void setNewValues(String newValues) {
        this.newValues = newValues;
    }
    
    public Boolean getIsSuccess() {
        return isSuccess;
    }
    
    public void setIsSuccess(Boolean isSuccess) {
        this.isSuccess = isSuccess;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public String getServiceName() {
        return serviceName;
    }
    
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    
    public String getMethodName() {
        return methodName;
    }
    
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
    
    public Long getExecutionTimeMs() {
        return executionTimeMs;
    }
    
    public void setExecutionTimeMs(Long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }
    
    @Override
    public String toString() {
        return "AuditEventRequest{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", action=" + action +
                ", entityType='" + entityType + '\'' +
                ", entityId=" + entityId +
                ", description='" + description + '\'' +
                ", isSuccess=" + isSuccess +
                '}';
    }
} 