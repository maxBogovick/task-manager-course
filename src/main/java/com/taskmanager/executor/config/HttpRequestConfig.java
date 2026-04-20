package com.taskmanager.executor.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HttpRequestConfig(
        @NotBlank String url,
        String method,
        Map<String, String> headers,
        String body,
        Integer timeoutSeconds
) {}
