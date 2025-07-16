package com.tasktracker.audit.dto;

import com.tasktracker.audit.entity.AuditAction;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

/**
 * DTO для фильтрации событий аудита
 */
public class AuditFilterRequest {
    
    private Long userId;
    private String username;
    private AuditAction action;
    private String entityType;
    private Long entityId;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDate;
    
    private Boolean isSuccess;
    private String ipAddress;
    private String serviceName;
    private String keyword;
    private Boolean criticalOnly;
    private Long minExecutionTime;
    private Long maxExecutionTime;
    
    // Параметры пагинации
    private Integer page = 0;
    private Integer size = 20;
    private String sortBy = "timestamp";
    private String sortDirection = "desc";
    
    // Конструкторы
    public AuditFilterRequest() {}
    
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
    
    public LocalDateTime getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }
    
    public LocalDateTime getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
    
    public Boolean getIsSuccess() {
        return isSuccess;
    }
    
    public void setIsSuccess(Boolean isSuccess) {
        this.isSuccess = isSuccess;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getServiceName() {
        return serviceName;
    }
    
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    
    public String getKeyword() {
        return keyword;
    }
    
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    
    public Boolean getCriticalOnly() {
        return criticalOnly;
    }
    
    public void setCriticalOnly(Boolean criticalOnly) {
        this.criticalOnly = criticalOnly;
    }
    
    public Long getMinExecutionTime() {
        return minExecutionTime;
    }
    
    public void setMinExecutionTime(Long minExecutionTime) {
        this.minExecutionTime = minExecutionTime;
    }
    
    public Long getMaxExecutionTime() {
        return maxExecutionTime;
    }
    
    public void setMaxExecutionTime(Long maxExecutionTime) {
        this.maxExecutionTime = maxExecutionTime;
    }
    
    public Integer getPage() {
        return page;
    }
    
    public void setPage(Integer page) {
        this.page = page;
    }
    
    public Integer getSize() {
        return size;
    }
    
    public void setSize(Integer size) {
        this.size = size;
    }
    
    public String getSortBy() {
        return sortBy;
    }
    
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
    
    public String getSortDirection() {
        return sortDirection;
    }
    
    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }
    
    // Вспомогательные методы
    public boolean hasDateRange() {
        return startDate != null && endDate != null;
    }
    
    public boolean hasUserFilter() {
        return userId != null || (username != null && !username.trim().isEmpty());
    }
    
    public boolean hasEntityFilter() {
        return entityType != null || entityId != null;
    }
    
    public boolean hasExecutionTimeFilter() {
        return minExecutionTime != null || maxExecutionTime != null;
    }
    
    @Override
    public String toString() {
        return "AuditFilterRequest{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", action=" + action +
                ", entityType='" + entityType + '\'' +
                ", entityId=" + entityId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", isSuccess=" + isSuccess +
                ", criticalOnly=" + criticalOnly +
                ", page=" + page +
                ", size=" + size +
                '}';
    }
} 