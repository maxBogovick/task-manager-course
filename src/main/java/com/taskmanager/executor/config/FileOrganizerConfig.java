package com.taskmanager.executor.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FileOrganizerConfig(
        @NotBlank String sourceDir,
        Boolean dryRun,
        Map<String, String> rules
) {}
