package com.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;

public record TaskExecutionRequest(
        @NotBlank(message = "output must not be blank")
        String output
) {
}

