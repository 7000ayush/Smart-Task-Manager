package com.taskmang.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taskmang.dto.request.TaskRequest;
import com.taskmang.dto.response.TaskResponse;
import com.taskmang.entity.Task;
import com.taskmang.entity.User;
import com.taskmang.exception.ResourceNotFoundException;
import com.taskmang.repository.TaskRepository;
import com.taskmang.service.AuditLogService;
import com.taskmang.service.TaskService;
import com.taskmang.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final AuditLogService auditLogService;

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> findAllByUser(String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return taskRepository.findByUser(user, Sort.by(Sort.Direction.ASC, "dueDate"))
                .stream()
                .map(task -> modelMapper.map(task, TaskResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse findById(Long taskId, String username) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        
        if (!task.getUser().getUsername().equals(username)) {
            throw new ResourceNotFoundException("Task not found for this user");
        }
        
        return modelMapper.map(task, TaskResponse.class);
    }

    @Override
    @Transactional
    public TaskResponse create(String username, TaskRequest taskRequest) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Task task = modelMapper.map(taskRequest, Task.class);
        task.setUser(user);
        Task savedTask = taskRepository.save(task);
        
        auditLogService.logAction("CREATE", "TASK", savedTask.getId(), null, 
            taskToString(savedTask), username);
        
        return modelMapper.map(savedTask, TaskResponse.class);
    }

    @Override
    @Transactional
    public TaskResponse update(Long taskId, String username, TaskRequest taskRequest) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        
        if (!task.getUser().getUsername().equals(username)) {
            throw new ResourceNotFoundException("Task not found for this user");
        }
        
        String oldTask = taskToString(task);
        modelMapper.map(taskRequest, task);
        Task updatedTask = taskRepository.save(task);
        
        auditLogService.logAction("UPDATE", "TASK", taskId, oldTask, 
            taskToString(updatedTask), username);
        
        return modelMapper.map(updatedTask, TaskResponse.class);
    }

    @Override
    @Transactional
    public void delete(Long taskId, String username) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        
        if (!task.getUser().getUsername().equals(username)) {
            throw new ResourceNotFoundException("Task not found for this user");
        }
        
        auditLogService.logAction("DELETE", "TASK", taskId, 
            taskToString(task), null, username);
        
        taskRepository.delete(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> findTasksDueToday(String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = now.toLocalDate().atTime(23, 59, 59);
        
        return taskRepository.findByUserAndDueDateBetween(
                user, 
                Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant()),
                Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant()),
                Sort.by(Sort.Direction.ASC, "dueDate"))
                .stream()
                .map(task -> modelMapper.map(task, TaskResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> findUpcomingTasks(String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1).toLocalDate().atStartOfDay();
        
        return taskRepository.findByUserAndDueDateAfter(
                user, 
                Date.from(tomorrow.atZone(ZoneId.systemDefault()).toInstant()),
                Sort.by(Sort.Direction.ASC, "dueDate"))
                .stream()
                .map(task -> modelMapper.map(task, TaskResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Object getTaskStats(String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Implementation to get task statistics
        return null;
    }

    @Override
    public byte[] exportTasks(String username, String format) {
        // Implementation for exporting tasks
        return new byte[0];
    }

    private String taskToString(Task task) {
        return String.format("Task[id=%d, name='%s', status='%s', dueDate='%s']", 
                task.getId(), task.getName(), task.getStatus(), task.getDueDate());
    }
}
