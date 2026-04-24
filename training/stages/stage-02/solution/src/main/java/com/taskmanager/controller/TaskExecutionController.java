package com.taskmanager.controller;

import com.taskmanager.dto.TaskExecutionRequest;
import com.taskmanager.dto.TaskExecutionResponse;
import com.taskmanager.service.TaskExecutionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks/{taskId}/executions")
public class TaskExecutionController {

    private final TaskExecutionService taskExecutionService;

    public TaskExecutionController(TaskExecutionService taskExecutionService) {
        this.taskExecutionService = taskExecutionService;
    }

    @PostMapping
    public ResponseEntity<TaskExecutionResponse> create(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskExecutionRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskExecutionService.create(taskId, request));
    }

    @GetMapping
    public ResponseEntity<List<TaskExecutionResponse>> getByTaskId(@PathVariable Long taskId) {
        return ResponseEntity.ok(taskExecutionService.getByTaskId(taskId));
    }
}

