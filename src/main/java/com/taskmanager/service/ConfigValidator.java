package com.taskmanager.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.executor.TaskExecutorRegistry;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Validates the JSON config of a task definition against the typed config record
 * for the given task type, failing fast before the execution ever runs.
 *
 * <p>Config classes are discovered automatically from the executor registry —
 * no manual mapping required when adding a new executor type.</p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ConfigValidator {

    private final ObjectMapper objectMapper;
    private final Validator validator;
    private final TaskExecutorRegistry executorRegistry;

    public void validate(String taskType, String configJson) {
        executorRegistry.getConfigClass(taskType).ifPresent(configClass -> {
            Object config;
            try {
                config = objectMapper.readValue(configJson, configClass);
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException(
                        "Invalid config for task type " + taskType + ": " + e.getOriginalMessage());
            }

            @SuppressWarnings("unchecked")
            Set<ConstraintViolation<Object>> violations =
                    (Set<ConstraintViolation<Object>>) (Set<?>) validator.validate(config);

            if (!violations.isEmpty()) {
                String msg = violations.stream()
                        .map(v -> "'" + v.getPropertyPath() + "' " + v.getMessage())
                        .collect(Collectors.joining(", "));
                throw new IllegalArgumentException("Config validation failed for " + taskType + ": " + msg);
            }
        });
    }
}
