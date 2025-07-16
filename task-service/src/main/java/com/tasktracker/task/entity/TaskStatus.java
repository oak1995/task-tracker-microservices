package com.tasktracker.task.entity;

/**
 * Перечисление статусов задач
 * 
 * @author Orazbakhov Aibek
 * @version 1.0
 */
public enum TaskStatus {
    /**
     * Новая задача (создана, но еще не назначена)
     */
    NEW("Новая"),
    
    /**
     * Задача в процессе выполнения
     */
    IN_PROGRESS("В процессе"),
    
    /**
     * Задача на проверке
     */
    UNDER_REVIEW("На проверке"),
    
    /**
     * Задача завершена успешно
     */
    COMPLETED("Завершена"),
    
    /**
     * Задача отменена
     */
    CANCELLED("Отменена"),
    
    /**
     * Задача приостановлена
     */
    ON_HOLD("Приостановлена");
    
    private final String displayName;
    
    TaskStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Проверяет, является ли статус финальным (задача завершена)
     */
    public boolean isFinal() {
        return this == COMPLETED || this == CANCELLED;
    }
    
    /**
     * Проверяет, является ли статус активным (задача в работе)
     */
    public boolean isActive() {
        return this == IN_PROGRESS || this == UNDER_REVIEW;
    }
} 