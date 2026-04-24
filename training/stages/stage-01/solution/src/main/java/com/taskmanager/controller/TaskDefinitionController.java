package com.taskmanager.controller;

import com.taskmanager.dto.TaskDefinitionRequest;
import com.taskmanager.dto.TaskDefinitionResponse;
import com.taskmanager.service.TaskDefinitionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskDefinitionController {

    private final TaskDefinitionService taskDefinitionService;

    public TaskDefinitionController(TaskDefinitionService taskDefinitionService) {
        this.taskDefinitionService = taskDefinitionService;
    }

    @PostMapping
    public ResponseEntity<TaskDefinitionResponse> create(@Valid @RequestBody TaskDefinitionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskDefinitionService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDefinitionResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(taskDefinitionService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<TaskDefinitionResponse>> getAll() {
        return ResponseEntity.ok(taskDefinitionService.getAll());
    }
}

