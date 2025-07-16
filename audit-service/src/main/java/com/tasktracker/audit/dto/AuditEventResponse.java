package com.tasktracker.audit.dto;

import com.tasktracker.audit.entity.AuditAction;
import com.tasktracker.audit.entity.AuditEvent;
import java.time.LocalDateTime;

/**
 * DTO для ответов с событиями аудита
 */
public class AuditEventResponse {
    
    private Long id;
    private Long userId;
    private String username;
    private AuditAction action;
    private String actionDescription;
    private String entityType;
    private Long entityId;
    private LocalDateTime timestamp;
    private String ipAddress;
    private String userAgent;
    private String sessionId;
    private String description;
    private String oldValues;
    private String newValues;
    private Boolean isSuccess;
    private String errorMessage;
    private String serviceName;
    private String methodName;
    private Long executionTimeMs;
    private boolean isCritical;
    
    // Конструкторы
    public AuditEventResponse() {}
    
    public AuditEventResponse(AuditEvent auditEvent) {
        this.id = auditEvent.getId();
        this.userId = auditEvent.getUserId();
        this.username = auditEvent.getUsername();
        this.action = auditEvent.getAction();
        this.actionDescription = auditEvent.getAction().getDescription();
        this.entityType = auditEvent.getEntityType();
        this.entityId = auditEvent.getEntityId();
        this.timestamp = auditEvent.getTimestamp();
        this.ipAddress = auditEvent.getIpAddress();
        this.userAgent = auditEvent.getUserAgent();
        this.sessionId = auditEvent.getSessionId();
        this.description = auditEvent.getDescription();
        this.oldValues = auditEvent.getOldValues();
        this.newValues = auditEvent.getNewValues();
        this.isSuccess = auditEvent.getIsSuccess();
        this.errorMessage = auditEvent.getErrorMessage();
        this.serviceName = auditEvent.getServiceName();
        this.methodName = auditEvent.getMethodName();
        this.executionTimeMs = auditEvent.getExecutionTimeMs();
        this.isCritical = auditEvent.isCritical();
    }
    
    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public AuditAction getAction() {
        return action;
    }
    
    public void setAction(AuditAction action) {
        this.action = action;
    }
    
    public String getActionDescription() {
        return actionDescription;
    }
    
    public void setActionDescription(String actionDescription) {
        this.actionDescription = actionDescription;
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
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
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
    
    public boolean isCritical() {
        return isCritical;
    }
    
    public void setCritical(boolean critical) {
        isCritical = critical;
    }
    
    @Override
    public String toString() {
        return "AuditEventResponse{" +
                "id=" + id +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", action=" + action +
                ", timestamp=" + timestamp +
                ", isSuccess=" + isSuccess +
                ", isCritical=" + isCritical +
                '}';
    }
} 