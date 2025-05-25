package com.taskmang.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taskmang.dto.request.TaskRequest;
import com.taskmang.dto.response.TaskResponse;
import com.taskmang.service.TaskService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class TaskController {
    private final TaskService taskService;

    @GetMapping
    @Operation(summary = "Get all tasks for the current user")
    public ResponseEntity<List<TaskResponse>> getAllTasks(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(taskService.findAllByUser(username));
    }

    @GetMapping("/{taskId}")
    @Operation(summary = "Get a task by ID")
    public ResponseEntity<TaskResponse> getTaskById(
            @PathVariable Long taskId, 
            Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(taskService.findById(taskId, username));
    }

    @PostMapping
    @Operation(summary = "Create a new task")
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody TaskRequest taskRequest,
            Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(taskService.create(username, taskRequest));
    }

    @PutMapping("/{taskId}")
    @Operation(summary = "Update an existing task")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskRequest taskRequest,
            Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(taskService.update(taskId, username, taskRequest));
    }

    @DeleteMapping("/{taskId}")
    @Operation(summary = "Delete a task")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long taskId,
            Authentication authentication) {
        String username = authentication.getName();
        taskService.delete(taskId, username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/today")
    @Operation(summary = "Get tasks due today")
    public ResponseEntity<List<TaskResponse>> getTasksDueToday(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(taskService.findTasksDueToday(username));
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Get upcoming tasks")
    public ResponseEntity<List<TaskResponse>> getUpcomingTasks(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(taskService.findUpcomingTasks(username));
    }

    @GetMapping("/stats")
    @Operation(summary = "Get task statistics")
    public ResponseEntity<Object> getTaskStats(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(taskService.getTaskStats(username));
    }

    @GetMapping("/export")
    @Operation(summary = "Export tasks")
    public ResponseEntity<byte[]> exportTasks(
            @RequestParam String format,
            Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(taskService.exportTasks(username, format));
    }
}