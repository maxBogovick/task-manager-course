package com.taskmanager.controller;

import com.taskmanager.dto.*;
import com.taskmanager.executor.TaskExecutorRegistry;
import com.taskmanager.service.TaskDefinitionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Slf4j
public class TaskDefinitionController {

    private final TaskDefinitionService definitionService;
    private final TaskExecutorRegistry executorRegistry;

    @PostMapping
    public ResponseEntity<TaskDefinitionResponse> create(@Valid @RequestBody TaskDefinitionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(definitionService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDefinitionResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(definitionService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDefinitionResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody TaskDefinitionRequest request) {
        return ResponseEntity.ok(definitionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        definitionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/enabled")
    public ResponseEntity<TaskDefinitionResponse> toggleEnabled(
            @PathVariable Long id,
            @RequestParam boolean enabled) {
        return ResponseEntity.ok(definitionService.toggleEnabled(id, enabled));
    }

    @GetMapping
    public ResponseEntity<PagedResponse<TaskDefinitionResponse>> getAll(
            @RequestParam(required = false) String type,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        if (type != null && !type.isBlank()) {
            return ResponseEntity.ok(definitionService.getByType(type, pageable));
        }
        return ResponseEntity.ok(definitionService.getAll(pageable));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<PagedResponse<TaskDefinitionResponse>> getByUser(
            @PathVariable Long userId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(definitionService.getByUserId(userId, pageable));
    }

    @GetMapping("/types")
    public ResponseEntity<Set<String>> getAvailableTypes() {
        return ResponseEntity.ok(executorRegistry.getRegisteredTypes());
    }
}
