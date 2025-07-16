package com.tasktracker.notification.service;

import com.tasktracker.notification.model.NotificationTemplate;
import com.tasktracker.notification.model.NotificationType;
import com.tasktracker.notification.repository.NotificationTemplateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
public class NotificationTemplateService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationTemplateService.class);
    
    private final NotificationTemplateRepository templateRepository;
    
    public NotificationTemplateService(NotificationTemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }
    
    @Transactional(readOnly = true)
    public Optional<NotificationTemplate> getTemplate(NotificationType type, String channel) {
        return templateRepository.findByTypeAndChannel(type, channel);
    }
    
    @Transactional(readOnly = true)
    public Optional<NotificationTemplate> getTemplateByName(String name) {
        return templateRepository.findByName(name);
    }
    
    @Transactional(readOnly = true)
    public List<NotificationTemplate> getTemplatesByType(NotificationType type) {
        return templateRepository.findByType(type);
    }
    
    @Transactional(readOnly = true)
    public List<NotificationTemplate> getTemplatesByChannel(String channel) {
        return templateRepository.findByChannel(channel);
    }
    
    @Transactional(readOnly = true)
    public List<NotificationTemplate> getActiveTemplates() {
        return templateRepository.findByIsActiveTrue();
    }
    
    public NotificationTemplate createTemplate(NotificationTemplate template) {
        logger.info("Creating notification template: {}", template.getName());
        
        if (templateRepository.existsByName(template.getName())) {
            throw new RuntimeException("Template with name '" + template.getName() + "' already exists");
        }
        
        template = templateRepository.save(template);
        logger.info("Notification template created: {}", template.getName());
        
        return template;
    }
    
    public NotificationTemplate updateTemplate(UUID templateId, NotificationTemplate updatedTemplate) {
        logger.info("Updating notification template: {}", templateId);
        
        Optional<NotificationTemplate> existingTemplate = templateRepository.findById(templateId);
        if (existingTemplate.isEmpty()) {
            throw new RuntimeException("Template not found with id: " + templateId);
        }
        
        NotificationTemplate template = existingTemplate.get();
        
        // Update fields
        if (updatedTemplate.getName() != null) {
            template.setName(updatedTemplate.getName());
        }
        if (updatedTemplate.getType() != null) {
            template.setType(updatedTemplate.getType());
        }
        if (updatedTemplate.getChannel() != null) {
            template.setChannel(updatedTemplate.getChannel());
        }
        if (updatedTemplate.getSubject() != null) {
            template.setSubject(updatedTemplate.getSubject());
        }
        if (updatedTemplate.getTemplate() != null) {
            template.setTemplate(updatedTemplate.getTemplate());
        }
        if (updatedTemplate.getIsActive() != null) {
            template.setIsActive(updatedTemplate.getIsActive());
        }
        
        template = templateRepository.save(template);
        logger.info("Notification template updated: {}", template.getName());
        
        return template;
    }
    
    public void deleteTemplate(UUID templateId) {
        logger.info("Deleting notification template: {}", templateId);
        templateRepository.deleteById(templateId);
    }
    
    public void activateTemplate(UUID templateId) {
        logger.info("Activating notification template: {}", templateId);
        updateTemplateStatus(templateId, true);
    }
    
    public void deactivateTemplate(UUID templateId) {
        logger.info("Deactivating notification template: {}", templateId);
        updateTemplateStatus(templateId, false);
    }
    
    private void updateTemplateStatus(UUID templateId, boolean isActive) {
        Optional<NotificationTemplate> template = templateRepository.findById(templateId);
        if (template.isPresent()) {
            template.get().setIsActive(isActive);
            templateRepository.save(template.get());
        }
    }
    
    public String processTemplate(String templateContent, java.util.Map<String, Object> variables) {
        if (templateContent == null || variables == null) {
            return templateContent;
        }
        
        String result = templateContent;
        
        // Replace placeholders like {{variableName}} with actual values
        Pattern pattern = Pattern.compile("\\{\\{([^}]+)\\}\\}");
        Matcher matcher = pattern.matcher(templateContent);
        
        while (matcher.find()) {
            String placeholder = matcher.group(0);
            String variableName = matcher.group(1).trim();
            
            Object value = variables.get(variableName);
            if (value != null) {
                result = result.replace(placeholder, value.toString());
            }
        }
        
        return result;
    }
    
    public void initializeDefaultTemplates() {
        logger.info("Initializing default notification templates");
        
        // Task Created Email Template
        if (templateRepository.findByTypeAndChannel(NotificationType.TASK_CREATED, "EMAIL").isEmpty()) {
            NotificationTemplate template = new NotificationTemplate(
                "task-created-email",
                NotificationType.TASK_CREATED,
                "EMAIL",
                "New Task: {{taskTitle}}",
                "Hello {{userName}},\n\n" +
                "A new task has been created and assigned to you:\n\n" +
                "Task: {{taskTitle}}\n" +
                "Description: {{taskDescription}}\n" +
                "Priority: {{taskPriority}}\n" +
                "Due Date: {{dueDate}}\n\n" +
                "Please log in to Task Tracker to view more details.\n\n" +
                "Best regards,\n" +
                "Task Tracker Team"
            );
            templateRepository.save(template);
        }
        
        // Task Assigned Email Template
        if (templateRepository.findByTypeAndChannel(NotificationType.TASK_ASSIGNED, "EMAIL").isEmpty()) {
            NotificationTemplate template = new NotificationTemplate(
                "task-assigned-email",
                NotificationType.TASK_ASSIGNED,
                "EMAIL",
                "Task Assigned: {{taskTitle}}",
                "Hello {{userName}},\n\n" +
                "A task has been assigned to you:\n\n" +
                "Task: {{taskTitle}}\n" +
                "Description: {{taskDescription}}\n" +
                "Priority: {{taskPriority}}\n" +
                "Due Date: {{dueDate}}\n" +
                "Assigned by: {{assignedBy}}\n\n" +
                "Please log in to Task Tracker to start working on this task.\n\n" +
                "Best regards,\n" +
                "Task Tracker Team"
            );
            templateRepository.save(template);
        }
        
        // Task Overdue Email Template
        if (templateRepository.findByTypeAndChannel(NotificationType.TASK_OVERDUE, "EMAIL").isEmpty()) {
            NotificationTemplate template = new NotificationTemplate(
                "task-overdue-email",
                NotificationType.TASK_OVERDUE,
                "EMAIL",
                "Overdue Task: {{taskTitle}}",
                "Hello {{userName}},\n\n" +
                "The following task is overdue:\n\n" +
                "Task: {{taskTitle}}\n" +
                "Description: {{taskDescription}}\n" +
                "Priority: {{taskPriority}}\n" +
                "Due Date: {{dueDate}}\n\n" +
                "Please complete this task as soon as possible.\n\n" +
                "Best regards,\n" +
                "Task Tracker Team"
            );
            templateRepository.save(template);
        }
        
        // Welcome Email Template
        if (templateRepository.findByTypeAndChannel(NotificationType.USER_REGISTERED, "EMAIL").isEmpty()) {
            NotificationTemplate template = new NotificationTemplate(
                "user-registered-email",
                NotificationType.USER_REGISTERED,
                "EMAIL",
                "Welcome to Task Tracker!",
                "Hello {{userName}},\n\n" +
                "Welcome to Task Tracker! Your account has been created successfully.\n\n" +
                "You can now:\n" +
                "- Create and manage tasks\n" +
                "- Collaborate with team members\n" +
                "- Track your progress\n" +
                "- Receive notifications about important updates\n\n" +
                "Get started by logging in to your account.\n\n" +
                "Best regards,\n" +
                "Task Tracker Team"
            );
            templateRepository.save(template);
        }
        
        logger.info("Default notification templates initialized");
    }
} 