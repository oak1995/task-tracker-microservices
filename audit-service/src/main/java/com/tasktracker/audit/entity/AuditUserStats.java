package com.tasktracker.audit.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Сущность для хранения статистики пользователей по аудиту
 */
@Entity
@Table(name = "audit_user_stats", indexes = {
    @Index(name = "idx_audit_stats_user_id", columnList = "user_id"),
    @Index(name = "idx_audit_stats_date", columnList = "stats_date")
})
public class AuditUserStats {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "username", nullable = false)
    private String username;
    
    @Column(name = "stats_date", nullable = false)
    private LocalDateTime statsDate;
    
    @Column(name = "total_actions")
    private Integer totalActions = 0;
    
    @Column(name = "successful_actions")
    private Integer successfulActions = 0;
    
    @Column(name = "failed_actions")
    private Integer failedActions = 0;
    
    @Column(name = "critical_actions")
    private Integer criticalActions = 0;
    
    @Column(name = "user_actions")
    private Integer userActions = 0;
    
    @Column(name = "task_actions")
    private Integer taskActions = 0;
    
    @Column(name = "system_actions")
    private Integer systemActions = 0;
    
    @Column(name = "login_count")
    private Integer loginCount = 0;
    
    @Column(name = "logout_count")
    private Integer logoutCount = 0;
    
    @Column(name = "tasks_created")
    private Integer tasksCreated = 0;
    
    @Column(name = "tasks_completed")
    private Integer tasksCompleted = 0;
    
    @Column(name = "comments_created")
    private Integer commentsCreated = 0;
    
    @Column(name = "last_activity")
    private LocalDateTime lastActivity;
    
    @Column(name = "average_session_duration_minutes")
    private Double averageSessionDurationMinutes = 0.0;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Конструкторы
    public AuditUserStats() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public AuditUserStats(Long userId, String username, LocalDateTime statsDate) {
        this();
        this.userId = userId;
        this.username = username;
        this.statsDate = statsDate;
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
    
    public LocalDateTime getStatsDate() {
        return statsDate;
    }
    
    public void setStatsDate(LocalDateTime statsDate) {
        this.statsDate = statsDate;
    }
    
    public Integer getTotalActions() {
        return totalActions;
    }
    
    public void setTotalActions(Integer totalActions) {
        this.totalActions = totalActions;
    }
    
    public Integer getSuccessfulActions() {
        return successfulActions;
    }
    
    public void setSuccessfulActions(Integer successfulActions) {
        this.successfulActions = successfulActions;
    }
    
    public Integer getFailedActions() {
        return failedActions;
    }
    
    public void setFailedActions(Integer failedActions) {
        this.failedActions = failedActions;
    }
    
    public Integer getCriticalActions() {
        return criticalActions;
    }
    
    public void setCriticalActions(Integer criticalActions) {
        this.criticalActions = criticalActions;
    }
    
    public Integer getUserActions() {
        return userActions;
    }
    
    public void setUserActions(Integer userActions) {
        this.userActions = userActions;
    }
    
    public Integer getTaskActions() {
        return taskActions;
    }
    
    public void setTaskActions(Integer taskActions) {
        this.taskActions = taskActions;
    }
    
    public Integer getSystemActions() {
        return systemActions;
    }
    
    public void setSystemActions(Integer systemActions) {
        this.systemActions = systemActions;
    }
    
    public Integer getLoginCount() {
        return loginCount;
    }
    
    public void setLoginCount(Integer loginCount) {
        this.loginCount = loginCount;
    }
    
    public Integer getLogoutCount() {
        return logoutCount;
    }
    
    public void setLogoutCount(Integer logoutCount) {
        this.logoutCount = logoutCount;
    }
    
    public Integer getTasksCreated() {
        return tasksCreated;
    }
    
    public void setTasksCreated(Integer tasksCreated) {
        this.tasksCreated = tasksCreated;
    }
    
    public Integer getTasksCompleted() {
        return tasksCompleted;
    }
    
    public void setTasksCompleted(Integer tasksCompleted) {
        this.tasksCompleted = tasksCompleted;
    }
    
    public Integer getCommentsCreated() {
        return commentsCreated;
    }
    
    public void setCommentsCreated(Integer commentsCreated) {
        this.commentsCreated = commentsCreated;
    }
    
    public LocalDateTime getLastActivity() {
        return lastActivity;
    }
    
    public void setLastActivity(LocalDateTime lastActivity) {
        this.lastActivity = lastActivity;
    }
    
    public Double getAverageSessionDurationMinutes() {
        return averageSessionDurationMinutes;
    }
    
    public void setAverageSessionDurationMinutes(Double averageSessionDurationMinutes) {
        this.averageSessionDurationMinutes = averageSessionDurationMinutes;
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
    
    // Вспомогательные методы
    public void incrementTotalActions() {
        this.totalActions++;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void incrementSuccessfulActions() {
        this.successfulActions++;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void incrementFailedActions() {
        this.failedActions++;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void incrementCriticalActions() {
        this.criticalActions++;
        this.updatedAt = LocalDateTime.now();
    }
    
    public double getSuccessRate() {
        if (totalActions == 0) return 0.0;
        return (double) successfulActions / totalActions * 100;
    }
    
    public double getFailureRate() {
        if (totalActions == 0) return 0.0;
        return (double) failedActions / totalActions * 100;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuditUserStats that = (AuditUserStats) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "AuditUserStats{" +
                "id=" + id +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", statsDate=" + statsDate +
                ", totalActions=" + totalActions +
                ", successfulActions=" + successfulActions +
                ", failedActions=" + failedActions +
                ", criticalActions=" + criticalActions +
                '}';
    }
} 