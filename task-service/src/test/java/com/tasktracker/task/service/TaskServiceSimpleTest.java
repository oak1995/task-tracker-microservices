package com.tasktracker.task.service;

import com.tasktracker.task.dto.TaskCreateRequest;
import com.tasktracker.task.dto.TaskResponse;
import com.tasktracker.task.entity.Category;
import com.tasktracker.task.entity.Task;
import com.tasktracker.task.entity.TaskPriority;
import com.tasktracker.task.entity.TaskStatus;
import com.tasktracker.task.repository.CategoryRepository;
import com.tasktracker.task.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit тесты для TaskService
 */
@ExtendWith(MockitoExtension.class)
class TaskServiceSimpleTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private TaskService taskService;

    private Task mockTask;
    private Category mockCategory;
    private TaskCreateRequest mockCreateRequest;

    @BeforeEach
    void setUp() {
        mockCategory = new Category();
        mockCategory.setId(1L);
        mockCategory.setName("Test Category");
        mockCategory.setDescription("Test Description");

        mockTask = new Task();
        mockTask.setId(1L);
        mockTask.setTitle("Test Task");
        mockTask.setDescription("Test Description");
        mockTask.setStatus(TaskStatus.NEW);
        mockTask.setPriority(TaskPriority.MEDIUM);
        mockTask.setCreatedByUserId(1L);
        mockTask.setCategory(mockCategory);
        mockTask.setCreatedAt(LocalDateTime.now());
        mockTask.setUpdatedAt(LocalDateTime.now());

        mockCreateRequest = new TaskCreateRequest();
        mockCreateRequest.setTitle("New Task");
        mockCreateRequest.setDescription("New Description");
        mockCreateRequest.setPriority(TaskPriority.HIGH);
        mockCreateRequest.setCategoryId(1L);
        mockCreateRequest.setAssignedToUserId(2L);
        mockCreateRequest.setDueDate(LocalDateTime.now().plusDays(7));
        mockCreateRequest.setEstimatedHours(8.0);
    }

    @Test
    void createTask_Success() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(mockCategory));
        when(taskRepository.save(any(Task.class))).thenReturn(mockTask);

        // Act
        TaskResponse result = taskService.createTask(mockCreateRequest, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(mockTask.getId(), result.getId());
        assertEquals(mockTask.getTitle(), result.getTitle());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void getTaskById_Success() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));

        // Act
        TaskResponse result = taskService.getTaskById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(mockTask.getId(), result.getId());
        assertEquals(mockTask.getTitle(), result.getTitle());
    }

    @Test
    void getTaskById_NotFound() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            taskService.getTaskById(1L);
        });
    }

    @Test
    void getAllTasks_Success() {
        // Arrange
        when(taskRepository.findAll()).thenReturn(Arrays.asList(mockTask));

        // Act
        Page<TaskResponse> result = taskService.getAllTasks(0, 10, null, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(mockTask.getTitle(), result.getContent().get(0).getTitle());
    }

    @Test
    void updateTask_Success() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(mockCategory));
        when(taskRepository.save(any(Task.class))).thenReturn(mockTask);

        // Act
        TaskResponse result = taskService.updateTask(1L, mockCreateRequest, 1L);

        // Assert
        assertNotNull(result);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void deleteTask_Success() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));

        // Act
        taskService.deleteTask(1L, 1L);

        // Assert
        verify(taskRepository).delete(mockTask);
    }

    @Test
    void assignTask_Success() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(taskRepository.save(any(Task.class))).thenReturn(mockTask);

        // Act
        TaskResponse result = taskService.assignTask(1L, 2L);

        // Assert
        assertNotNull(result);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void updateTaskStatus_Success() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(taskRepository.save(any(Task.class))).thenReturn(mockTask);

        // Act
        TaskResponse result = taskService.updateTaskStatus(1L, TaskStatus.IN_PROGRESS, 1L);

        // Assert
        assertNotNull(result);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void getTasksByStatus_Success() {
        // Arrange
        when(taskRepository.findByStatus(TaskStatus.NEW)).thenReturn(Arrays.asList(mockTask));

        // Act
        Page<TaskResponse> result = taskService.getTasksByStatus(TaskStatus.NEW, 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(mockTask.getTitle(), result.getContent().get(0).getTitle());
    }

    @Test
    void getTasksByAssignedUser_Success() {
        // Arrange
        when(taskRepository.findByAssignedToUserId(2L)).thenReturn(Arrays.asList(mockTask));

        // Act
        Page<TaskResponse> result = taskService.getTasksByAssignedUser(2L, 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(mockTask.getTitle(), result.getContent().get(0).getTitle());
    }

    @Test
    void searchTasks_Success() {
        // Arrange
        when(taskRepository.findByTitleContainingIgnoreCase("test")).thenReturn(Arrays.asList(mockTask));

        // Act
        List<TaskResponse> result = taskService.searchTasks("test");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockTask.getTitle(), result.get(0).getTitle());
    }

    @Test
    void getOverdueTasks_Success() {
        // Arrange
        when(taskRepository.findOverdueTasks(any(LocalDateTime.class))).thenReturn(Arrays.asList(mockTask));

        // Act
        List<TaskResponse> result = taskService.getOverdueTasks();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockTask.getTitle(), result.get(0).getTitle());
    }

    @Test
    void getTaskStatistics_Success() {
        // Arrange
        when(taskRepository.countByStatus(TaskStatus.NEW)).thenReturn(5L);
        when(taskRepository.countByStatus(TaskStatus.IN_PROGRESS)).thenReturn(3L);
        when(taskRepository.countByStatus(TaskStatus.COMPLETED)).thenReturn(10L);

        // Act
        TaskService.TaskStatistics result = taskService.getTaskStatistics();

        // Assert
        assertNotNull(result);
        assertEquals(18L, result.getTotalTasks());
        assertEquals(5L, result.getNewTasks());
        assertEquals(3L, result.getInProgressTasks());
        assertEquals(10L, result.getCompletedTasks());
    }
} 