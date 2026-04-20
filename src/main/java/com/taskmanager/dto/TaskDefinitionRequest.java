package com.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating or updating a task definition.
 */
public record TaskDefinitionRequest(

        @NotBlank(message = "Name is required")
        @Size(max = 255, message = "Name must not exceed 255 characters")
        String name,

        String description,

        @NotBlank(message = "Task type is required")
        String taskType,

        /** JSON config string for the executor. */
        @NotBlank(message = "Config is required")
        String config,

        /** Cron expression for scheduled execution (nullable = one-shot). */
        String cronExpression,

        Boolean enabled,

        @PositiveOrZero(message = "Max retries must be >= 0")
        Integer maxRetries,

        @Positive(message = "Retry delay must be > 0")
        Integer retryDelaySeconds,

        @Positive(message = "Timeout must be > 0")
        Integer timeoutSeconds,

        @NotNull(message = "User ID is required")
        Long userId
) {
}
