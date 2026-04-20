package com.taskmanager.controller;

import com.taskmanager.dto.*;
import com.taskmanager.entity.ExecutionStatus;
import com.taskmanager.service.TaskExecutionService;
import com.taskmanager.service.TaskRunService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class TaskExecutionController {

    private final TaskRunService taskRunService;
    private final TaskExecutionService executionService;

    // ── Trigger & Cancel ─────────────────────────────────────────────────

    @PostMapping("/tasks/{id}/run")
    public ResponseEntity<TaskExecutionResponse> runTask(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(taskRunService.runTask(id));
    }

    @PostMapping("/executions/{id}/cancel")
    public ResponseEntity<CancelExecutionResponse> cancelExecution(@PathVariable Long id) {
        return ResponseEntity.ok(taskRunService.cancelExecution(id));
    }

    // ── Queries ──────────────────────────────────────────────────────────

    @GetMapping("/executions/{id}")
    public ResponseEntity<TaskExecutionResponse> getExecution(@PathVariable Long id) {
        return ResponseEntity.ok(executionService.getById(id));
    }

    @GetMapping("/tasks/{id}/executions")
    public ResponseEntity<PagedResponse<TaskExecutionResponse>> getExecutions(
            @PathVariable Long id,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(executionService.getByDefinitionId(id, pageable));
    }

    @GetMapping("/executions")
    public ResponseEntity<PagedResponse<TaskExecutionResponse>> getAllExecutions(
            @RequestParam(required = false) ExecutionStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        if (status != null) {
            return ResponseEntity.ok(executionService.getByStatus(status, pageable));
        }
        return ResponseEntity.ok(executionService.getAll(pageable));
    }

    @GetMapping("/orchestrator/status")
    public ResponseEntity<OrchestratorStatusResponse> getOrchestratorStatus() {
        return ResponseEntity.ok(taskRunService.getOrchestratorStatus());
    }
}
