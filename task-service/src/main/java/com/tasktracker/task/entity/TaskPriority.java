package com.tasktracker.task.entity;

/**
 * Перечисление приоритетов задач
 * 
 * @author Orazbakhov Aibek
 * @version 1.0
 */
public enum TaskPriority {
    /**
     * Низкий приоритет
     */
    LOW("Низкий", 1),
    
    /**
     * Средний приоритет
     */
    MEDIUM("Средний", 2),
    
    /**
     * Высокий приоритет
     */
    HIGH("Высокий", 3),
    
    /**
     * Критический приоритет
     */
    CRITICAL("Критический", 4);
    
    private final String displayName;
    private final int level;
    
    TaskPriority(String displayName, int level) {
        this.displayName = displayName;
        this.level = level;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public int getLevel() {
        return level;
    }
    
    /**
     * Проверяет, является ли приоритет критическим
     */
    public boolean isCritical() {
        return this == CRITICAL;
    }
    
    /**
     * Проверяет, является ли приоритет высоким или критическим
     */
    public boolean isUrgent() {
        return this == HIGH || this == CRITICAL;
    }
    
    /**
     * Сравнивает приоритеты по уровню
     */
    public boolean isHigherThan(TaskPriority other) {
        return this.level > other.level;
    }
} 