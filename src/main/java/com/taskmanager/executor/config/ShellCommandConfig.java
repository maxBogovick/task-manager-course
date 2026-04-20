package com.taskmanager.executor.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ShellCommandConfig(
        @NotBlank String command,
        String workDir,
        Integer timeoutSeconds
) {}
