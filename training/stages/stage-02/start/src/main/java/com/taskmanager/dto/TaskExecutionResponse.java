package com.taskmanager.dto;

import java.time.LocalDateTime;

public record TaskExecutionResponse(
        Long id,
        Long taskDefinitionId,
        String status,
        String output,
        LocalDateTime createdAt
) {
}

