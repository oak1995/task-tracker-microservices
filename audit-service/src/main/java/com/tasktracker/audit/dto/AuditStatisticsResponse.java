package com.tasktracker.audit.dto;

import com.tasktracker.audit.entity.AuditAction;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO для статистики аудита
 */
public class AuditStatisticsResponse {
    
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private Long totalEvents;
    private Long successfulEvents;
    private Long failedEvents;
    private Long criticalEvents;
    private Double successRate;
    private Double failureRate;
    private Integer uniqueUsers;
    private Integer activeUsers;
    private Map<AuditAction, Long> actionCounts;
    private Map<String, Long> entityTypeCounts;
    private Map<String, Long> serviceCounts;
    private List<UserActivityStats> topActiveUsers;
    private List<DailyStats> dailyStats;
    private List<HourlyStats> hourlyStats;
    private Long averageExecutionTime;
    private Long slowestExecutionTime;
    private Long fastestExecutionTime;
    
    // Конструкторы
    public AuditStatisticsResponse() {}
    
    public AuditStatisticsResponse(LocalDateTime periodStart, LocalDateTime periodEnd) {
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
    }
    
    // Геттеры и сеттеры
    public LocalDateTime getPeriodStart() {
        return periodStart;
    }
    
    public void setPeriodStart(LocalDateTime periodStart) {
        this.periodStart = periodStart;
    }
    
    public LocalDateTime getPeriodEnd() {
        return periodEnd;
    }
    
    public void setPeriodEnd(LocalDateTime periodEnd) {
        this.periodEnd = periodEnd;
    }
    
    public Long getTotalEvents() {
        return totalEvents;
    }
    
    public void setTotalEvents(Long totalEvents) {
        this.totalEvents = totalEvents;
    }
    
    public Long getSuccessfulEvents() {
        return successfulEvents;
    }
    
    public void setSuccessfulEvents(Long successfulEvents) {
        this.successfulEvents = successfulEvents;
    }
    
    public Long getFailedEvents() {
        return failedEvents;
    }
    
    public void setFailedEvents(Long failedEvents) {
        this.failedEvents = failedEvents;
    }
    
    public Long getCriticalEvents() {
        return criticalEvents;
    }
    
    public void setCriticalEvents(Long criticalEvents) {
        this.criticalEvents = criticalEvents;
    }
    
    public Double getSuccessRate() {
        return successRate;
    }
    
    public void setSuccessRate(Double successRate) {
        this.successRate = successRate;
    }
    
    public Double getFailureRate() {
        return failureRate;
    }
    
    public void setFailureRate(Double failureRate) {
        this.failureRate = failureRate;
    }
    
    public Integer getUniqueUsers() {
        return uniqueUsers;
    }
    
    public void setUniqueUsers(Integer uniqueUsers) {
        this.uniqueUsers = uniqueUsers;
    }
    
    public Integer getActiveUsers() {
        return activeUsers;
    }
    
    public void setActiveUsers(Integer activeUsers) {
        this.activeUsers = activeUsers;
    }
    
    public Map<AuditAction, Long> getActionCounts() {
        return actionCounts;
    }
    
    public void setActionCounts(Map<AuditAction, Long> actionCounts) {
        this.actionCounts = actionCounts;
    }
    
    public Map<String, Long> getEntityTypeCounts() {
        return entityTypeCounts;
    }
    
    public void setEntityTypeCounts(Map<String, Long> entityTypeCounts) {
        this.entityTypeCounts = entityTypeCounts;
    }
    
    public Map<String, Long> getServiceCounts() {
        return serviceCounts;
    }
    
    public void setServiceCounts(Map<String, Long> serviceCounts) {
        this.serviceCounts = serviceCounts;
    }
    
    public List<UserActivityStats> getTopActiveUsers() {
        return topActiveUsers;
    }
    
    public void setTopActiveUsers(List<UserActivityStats> topActiveUsers) {
        this.topActiveUsers = topActiveUsers;
    }
    
    public List<DailyStats> getDailyStats() {
        return dailyStats;
    }
    
    public void setDailyStats(List<DailyStats> dailyStats) {
        this.dailyStats = dailyStats;
    }
    
    public List<HourlyStats> getHourlyStats() {
        return hourlyStats;
    }
    
    public void setHourlyStats(List<HourlyStats> hourlyStats) {
        this.hourlyStats = hourlyStats;
    }
    
    public Long getAverageExecutionTime() {
        return averageExecutionTime;
    }
    
    public void setAverageExecutionTime(Long averageExecutionTime) {
        this.averageExecutionTime = averageExecutionTime;
    }
    
    public Long getSlowestExecutionTime() {
        return slowestExecutionTime;
    }
    
    public void setSlowestExecutionTime(Long slowestExecutionTime) {
        this.slowestExecutionTime = slowestExecutionTime;
    }
    
    public Long getFastestExecutionTime() {
        return fastestExecutionTime;
    }
    
    public void setFastestExecutionTime(Long fastestExecutionTime) {
        this.fastestExecutionTime = fastestExecutionTime;
    }
    
    // Вспомогательные методы
    public void calculateRates() {
        if (totalEvents != null && totalEvents > 0) {
            if (successfulEvents != null) {
                this.successRate = (double) successfulEvents / totalEvents * 100;
            }
            if (failedEvents != null) {
                this.failureRate = (double) failedEvents / totalEvents * 100;
            }
        }
    }
    
    // Внутренние классы для статистики
    public static class UserActivityStats {
        private Long userId;
        private String username;
        private Long eventCount;
        private Double successRate;
        private LocalDateTime lastActivity;
        
        public UserActivityStats() {}
        
        public UserActivityStats(Long userId, String username, Long eventCount) {
            this.userId = userId;
            this.username = username;
            this.eventCount = eventCount;
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
        
        public Long getEventCount() {
            return eventCount;
        }
        
        public void setEventCount(Long eventCount) {
            this.eventCount = eventCount;
        }
        
        public Double getSuccessRate() {
            return successRate;
        }
        
        public void setSuccessRate(Double successRate) {
            this.successRate = successRate;
        }
        
        public LocalDateTime getLastActivity() {
            return lastActivity;
        }
        
        public void setLastActivity(LocalDateTime lastActivity) {
            this.lastActivity = lastActivity;
        }
    }
    
    public static class DailyStats {
        private LocalDateTime date;
        private Long eventCount;
        private Double successRate;
        
        public DailyStats() {}
        
        public DailyStats(LocalDateTime date, Long eventCount, Double successRate) {
            this.date = date;
            this.eventCount = eventCount;
            this.successRate = successRate;
        }
        
        // Геттеры и сеттеры
        public LocalDateTime getDate() {
            return date;
        }
        
        public void setDate(LocalDateTime date) {
            this.date = date;
        }
        
        public Long getEventCount() {
            return eventCount;
        }
        
        public void setEventCount(Long eventCount) {
            this.eventCount = eventCount;
        }
        
        public Double getSuccessRate() {
            return successRate;
        }
        
        public void setSuccessRate(Double successRate) {
            this.successRate = successRate;
        }
    }
    
    public static class HourlyStats {
        private Integer hour;
        private Long eventCount;
        private Double averageResponseTime;
        
        public HourlyStats() {}
        
        public HourlyStats(Integer hour, Long eventCount, Double averageResponseTime) {
            this.hour = hour;
            this.eventCount = eventCount;
            this.averageResponseTime = averageResponseTime;
        }
        
        // Геттеры и сеттеры
        public Integer getHour() {
            return hour;
        }
        
        public void setHour(Integer hour) {
            this.hour = hour;
        }
        
        public Long getEventCount() {
            return eventCount;
        }
        
        public void setEventCount(Long eventCount) {
            this.eventCount = eventCount;
        }
        
        public Double getAverageResponseTime() {
            return averageResponseTime;
        }
        
        public void setAverageResponseTime(Double averageResponseTime) {
            this.averageResponseTime = averageResponseTime;
        }
    }
    
    @Override
    public String toString() {
        return "AuditStatisticsResponse{" +
                "periodStart=" + periodStart +
                ", periodEnd=" + periodEnd +
                ", totalEvents=" + totalEvents +
                ", successfulEvents=" + successfulEvents +
                ", failedEvents=" + failedEvents +
                ", criticalEvents=" + criticalEvents +
                ", successRate=" + successRate +
                ", uniqueUsers=" + uniqueUsers +
                ", activeUsers=" + activeUsers +
                '}';
    }
} 