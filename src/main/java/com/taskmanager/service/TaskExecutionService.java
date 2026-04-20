package com.taskmanager.service;

import com.taskmanager.dto.PagedResponse;
import com.taskmanager.dto.TaskExecutionResponse;
import com.taskmanager.entity.ExecutionStatus;
import com.taskmanager.entity.TaskExecution;
import com.taskmanager.exception.ExecutionNotFoundException;
import com.taskmanager.mapper.TaskMapper;
import com.taskmanager.repository.TaskExecutionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Queries and state transitions for task executions.
 * Write methods are intentionally kept here so the async executor thread
 * can call them through the Spring proxy and get proper transaction boundaries.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class TaskExecutionService {

    private static final int MAX_OUTPUT_LENGTH = 64 * 1024;

    private final TaskExecutionRepository executionRepo;
    private final TaskMapper mapper;

    public TaskExecutionResponse getById(Long id) {
        TaskExecution exec = executionRepo.findById(id)
                .orElseThrow(() -> new ExecutionNotFoundException(id));
        return mapper.toExecutionResponse(exec);
    }

    public PagedResponse<TaskExecutionResponse> getByDefinitionId(Long definitionId, Pageable pageable) {
        return toPagedResponse(executionRepo.findAllByTaskDefinitionId(definitionId, pageable));
    }

    public PagedResponse<TaskExecutionResponse> getByStatus(ExecutionStatus status, Pageable pageable) {
        return toPagedResponse(executionRepo.findAllByStatus(status, pageable));
    }

    public PagedResponse<TaskExecutionResponse> getAll(Pageable pageable) {
        return toPagedResponse(executionRepo.findAll(pageable));
    }

    // ── Write operations (called from background threads via Spring proxy) ──

    @Transactional
    public void markAsRunning(Long executionId) {
        executionRepo.findById(executionId).ifPresent(exec -> {
            exec.setStatus(ExecutionStatus.RUNNING);
            exec.setStartedAt(LocalDateTime.now());
            executionRepo.save(exec);
        });
    }

    @Transactional
    public void finishExecution(Long executionId, boolean success, String output, String errorMessage) {
        executionRepo.findById(executionId).ifPresent(exec -> {
            if (exec.getStatus() == ExecutionStatus.CANCELLED
                    || exec.getStatus() == ExecutionStatus.TIMED_OUT) {
                return;
            }
            exec.setStatus(success ? ExecutionStatus.COMPLETED : ExecutionStatus.FAILED);
            exec.setFinishedAt(LocalDateTime.now());
            exec.setOutput(truncate(output));
            exec.setErrorMessage(errorMessage);
            executionRepo.save(exec);
            log.info("{} Execution {} finished: {}", success ? "✅" : "❌", executionId,
                    success ? "COMPLETED" : "FAILED — " + errorMessage);
        });
    }

    // ── Helpers ──────────────────────────────────────────────────────────

    private String truncate(String s) {
        if (s == null || s.length() <= MAX_OUTPUT_LENGTH) return s;
        return s.substring(0, MAX_OUTPUT_LENGTH) + "\n...[truncated]";
    }

    private PagedResponse<TaskExecutionResponse> toPagedResponse(Page<TaskExecution> page) {
        var content = page.getContent().stream()
                .map(mapper::toExecutionResponse)
                .toList();
        return new PagedResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}
