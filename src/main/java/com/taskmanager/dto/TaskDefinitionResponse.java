package com.taskmanager.dto;

import java.time.LocalDateTime;

/**
 * Response DTO for a task definition.
 */
public record TaskDefinitionResponse(
        Long id,
        String name,
        String description,
        String taskType,
        String config,
        String cronExpression,
        Boolean enabled,
        Integer maxRetries,
        Integer retryDelaySeconds,
        Integer timeoutSeconds,
        Long userId,
        String lastExecutionStatus,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
