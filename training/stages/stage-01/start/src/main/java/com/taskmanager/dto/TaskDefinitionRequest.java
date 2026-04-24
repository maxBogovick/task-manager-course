package com.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;

public record TaskDefinitionRequest(
        @NotBlank(message = "name must not be blank")
        String name,
        String description
) {
}

