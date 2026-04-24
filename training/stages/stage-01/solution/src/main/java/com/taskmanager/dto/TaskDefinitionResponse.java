package com.taskmanager.dto;

import java.time.LocalDateTime;

public record TaskDefinitionResponse(
        Long id,
        String name,
        String description,
        boolean enabled,
        LocalDateTime createdAt
) {
}

