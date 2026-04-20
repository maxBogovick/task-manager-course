package com.taskmanager.dto;

import com.taskmanager.entity.ExecutionStatus;

import java.time.LocalDateTime;

/**
 * Response DTO for a single task execution (run).
 */
public record TaskExecutionResponse(
        Long id,
        Long taskDefinitionId,
        String taskName,
        ExecutionStatus status,
        Integer attempt,
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        String output,
        String errorMessage,
        LocalDateTime createdAt
) {
}
