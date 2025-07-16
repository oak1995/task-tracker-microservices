package com.tasktracker.notification.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tasktracker.notification.dto.CreateNotificationRequest;
import com.tasktracker.notification.dto.TaskEventDto;
import com.tasktracker.notification.model.NotificationType;
import com.tasktracker.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TaskEventListener {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskEventListener.class);
    
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;
    
    public TaskEventListener(NotificationService notificationService, ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }
    
    @KafkaListener(topics = "task-events", groupId = "notification-service-group")
    public void handleTaskEvent(String message) {
        logger.info("Received task event: {}", message);
        
        try {
            TaskEventDto taskEvent = objectMapper.readValue(message, TaskEventDto.class);
            processTaskEvent(taskEvent);
        } catch (Exception e) {
            logger.error("Error processing task event: {}", message, e);
        }
    }
    
    private void processTaskEvent(TaskEventDto taskEvent) {
        logger.debug("Processing task event: {}", taskEvent.getEventType());
        
        switch (taskEvent.getEventType()) {
            case "CREATED":
                handleTaskCreated(taskEvent);
                break;
            case "UPDATED":
                handleTaskUpdated(taskEvent);
                break;
            case "DELETED":
                handleTaskDeleted(taskEvent);
                break;
            case "ASSIGNED":
                handleTaskAssigned(taskEvent);
                break;
            case "COMPLETED":
                handleTaskCompleted(taskEvent);
                break;
            case "OVERDUE":
                handleTaskOverdue(taskEvent);
                break;
            default:
                logger.warn("Unknown task event type: {}", taskEvent.getEventType());
        }
    }
    
    private void handleTaskCreated(TaskEventDto taskEvent) {
        if (taskEvent.getAssignedUserId() != null) {
            CreateNotificationRequest request = new CreateNotificationRequest(
                taskEvent.getAssignedUserId(),
                "New Task Created",
                "A new task '" + taskEvent.getTitle() + "' has been created and assigned to you.",
                NotificationType.TASK_CREATED,
                "EMAIL"
            );
            request.setMetadata(createMetadata(taskEvent));
            notificationService.createNotification(request);
        }
        
        // Also notify the creator if different from assignee
        if (taskEvent.getCreatedByUserId() != null && 
            !taskEvent.getCreatedByUserId().equals(taskEvent.getAssignedUserId())) {
            CreateNotificationRequest request = new CreateNotificationRequest(
                taskEvent.getCreatedByUserId(),
                "Task Created Successfully",
                "Your task '" + taskEvent.getTitle() + "' has been created successfully.",
                NotificationType.TASK_CREATED,
                "EMAIL"
            );
            request.setMetadata(createMetadata(taskEvent));
            notificationService.createNotification(request);
        }
    }
    
    private void handleTaskUpdated(TaskEventDto taskEvent) {
        if (taskEvent.getAssignedUserId() != null) {
            CreateNotificationRequest request = new CreateNotificationRequest(
                taskEvent.getAssignedUserId(),
                "Task Updated",
                "Task '" + taskEvent.getTitle() + "' has been updated.",
                NotificationType.TASK_UPDATED,
                "EMAIL"
            );
            request.setMetadata(createMetadata(taskEvent));
            notificationService.createNotification(request);
        }
    }
    
    private void handleTaskDeleted(TaskEventDto taskEvent) {
        if (taskEvent.getAssignedUserId() != null) {
            CreateNotificationRequest request = new CreateNotificationRequest(
                taskEvent.getAssignedUserId(),
                "Task Deleted",
                "Task '" + taskEvent.getTitle() + "' has been deleted.",
                NotificationType.TASK_DELETED,
                "EMAIL"
            );
            request.setMetadata(createMetadata(taskEvent));
            notificationService.createNotification(request);
        }
    }
    
    private void handleTaskAssigned(TaskEventDto taskEvent) {
        if (taskEvent.getAssignedUserId() != null) {
            CreateNotificationRequest request = new CreateNotificationRequest(
                taskEvent.getAssignedUserId(),
                "Task Assigned to You",
                "Task '" + taskEvent.getTitle() + "' has been assigned to you.",
                NotificationType.TASK_ASSIGNED,
                "EMAIL"
            );
            request.setMetadata(createMetadata(taskEvent));
            notificationService.createNotification(request);
        }
    }
    
    private void handleTaskCompleted(TaskEventDto taskEvent) {
        if (taskEvent.getCreatedByUserId() != null) {
            CreateNotificationRequest request = new CreateNotificationRequest(
                taskEvent.getCreatedByUserId(),
                "Task Completed",
                "Task '" + taskEvent.getTitle() + "' has been completed.",
                NotificationType.TASK_COMPLETED,
                "EMAIL"
            );
            request.setMetadata(createMetadata(taskEvent));
            notificationService.createNotification(request);
        }
    }
    
    private void handleTaskOverdue(TaskEventDto taskEvent) {
        if (taskEvent.getAssignedUserId() != null) {
            CreateNotificationRequest request = new CreateNotificationRequest(
                taskEvent.getAssignedUserId(),
                "Task Overdue",
                "Task '" + taskEvent.getTitle() + "' is overdue. Please complete it as soon as possible.",
                NotificationType.TASK_OVERDUE,
                "EMAIL"
            );
            request.setMetadata(createMetadata(taskEvent));
            notificationService.createNotification(request);
        }
    }
    
    private String createMetadata(TaskEventDto taskEvent) {
        try {
            return objectMapper.writeValueAsString(taskEvent);
        } catch (Exception e) {
            logger.error("Error creating metadata", e);
            return "{}";
        }
    }
} 