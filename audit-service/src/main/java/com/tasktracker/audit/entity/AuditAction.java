package com.tasktracker.audit.entity;

/**
 * Enum для типов действий в системе аудита
 */
public enum AuditAction {
    // Действия с пользователями
    USER_CREATED("Создан пользователь"),
    USER_UPDATED("Обновлен пользователь"),
    USER_DELETED("Удален пользователь"),
    USER_LOGIN("Вход пользователя"),
    USER_LOGOUT("Выход пользователя"),
    USER_PASSWORD_CHANGED("Изменен пароль"),
    USER_PROFILE_UPDATED("Обновлен профиль"),
    
    // Действия с задачами
    TASK_CREATED("Создана задача"),
    TASK_UPDATED("Обновлена задача"),
    TASK_DELETED("Удалена задача"),
    TASK_ASSIGNED("Назначена задача"),
    TASK_UNASSIGNED("Отменено назначение задачи"),
    TASK_STATUS_CHANGED("Изменен статус задачи"),
    TASK_PRIORITY_CHANGED("Изменен приоритет задачи"),
    TASK_COMPLETED("Завершена задача"),
    
    // Действия с комментариями
    COMMENT_CREATED("Создан комментарий"),
    COMMENT_UPDATED("Обновлен комментарий"),
    COMMENT_DELETED("Удален комментарий"),
    
    // Действия с категориями
    CATEGORY_CREATED("Создана категория"),
    CATEGORY_UPDATED("Обновлена категория"),
    CATEGORY_DELETED("Удалена категория"),
    
    // Системные действия
    SYSTEM_STARTED("Запущена система"),
    SYSTEM_STOPPED("Остановлена система"),
    SYSTEM_ERROR("Ошибка системы"),
    
    // Действия с безопасностью
    UNAUTHORIZED_ACCESS("Несанкционированный доступ"),
    PERMISSION_DENIED("Отказано в доступе"),
    TOKEN_EXPIRED("Истек токен"),
    SECURITY_VIOLATION("Нарушение безопасности");
    
    private final String description;
    
    AuditAction(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Проверяет, является ли действие критическим
     */
    public boolean isCritical() {
        return this == SYSTEM_ERROR || 
               this == UNAUTHORIZED_ACCESS || 
               this == PERMISSION_DENIED ||
               this == SECURITY_VIOLATION;
    }
    
    /**
     * Проверяет, является ли действие пользовательским
     */
    public boolean isUserAction() {
        return this.name().startsWith("USER_");
    }
    
    /**
     * Проверяет, является ли действие системным
     */
    public boolean isSystemAction() {
        return this == SYSTEM_STARTED || 
               this == SYSTEM_STOPPED || 
               this == SYSTEM_ERROR;
    }
    
    /**
     * Проверяет, является ли действие связанным с задачами
     */
    public boolean isTaskAction() {
        return this.name().startsWith("TASK_");
    }
} 