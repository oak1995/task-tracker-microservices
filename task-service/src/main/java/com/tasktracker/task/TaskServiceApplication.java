package com.tasktracker.task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Основной класс Task Service
 * 
 * @author Orazbakhov Aibek
 * @version 1.0
 */
@SpringBootApplication
@EnableFeignClients
public class TaskServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(TaskServiceApplication.class, args);
    }
} 