package com.taskmanager.mapper;

import com.taskmanager.dto.TaskDefinitionRequest;
import com.taskmanager.dto.TaskDefinitionResponse;
import com.taskmanager.dto.TaskExecutionResponse;
import com.taskmanager.entity.TaskDefinition;
import com.taskmanager.entity.TaskExecution;
import org.springframework.stereotype.Component;

/**
 * Manual mapper for converting between entities and DTOs.
 */
@Component
public class TaskMapper {

    /**
     * Maps a definition to its response DTO.
     *
     * @param def        the task definition entity
     * @param lastStatus the latest execution status string, or {@code null} if none
     */
    public TaskDefinitionResponse toDefinitionResponse(TaskDefinition def, String lastStatus) {
        return new TaskDefinitionResponse(
                def.getId(),
                def.getName(),
                def.getDescription(),
                def.getTaskType(),
                def.getConfig(),
                def.getCronExpression(),
                def.getEnabled(),
                def.getMaxRetries(),
                def.getRetryDelaySeconds(),
                def.getTimeoutSeconds(),
                def.getUserId(),
                lastStatus,
                def.getCreatedAt(),
                def.getUpdatedAt()
        );
    }

    public TaskExecutionResponse toExecutionResponse(TaskExecution exec) {
        return new TaskExecutionResponse(
                exec.getId(),
                exec.getTaskDefinition().getId(),
                exec.getTaskDefinition().getName(),
                exec.getStatus(),
                exec.getAttempt(),
                exec.getStartedAt(),
                exec.getFinishedAt(),
                exec.getOutput(),
                exec.getErrorMessage(),
                exec.getCreatedAt()
        );
    }

    public TaskDefinition toEntity(TaskDefinitionRequest req) {
        var def = new TaskDefinition();
        def.setName(req.name());
        def.setDescription(req.description());
        def.setTaskType(req.taskType());
        def.setConfig(req.config());
        def.setCronExpression(req.cronExpression());
        def.setEnabled(req.enabled() != null ? req.enabled() : true);
        def.setMaxRetries(req.maxRetries() != null ? req.maxRetries() : 0);
        def.setRetryDelaySeconds(req.retryDelaySeconds() != null ? req.retryDelaySeconds() : 60);
        def.setTimeoutSeconds(req.timeoutSeconds() != null ? req.timeoutSeconds() : 3600);
        def.setUserId(req.userId());
        return def;
    }

    public void updateEntity(TaskDefinitionRequest req, TaskDefinition def) {
        def.setName(req.name());
        def.setDescription(req.description());
        def.setTaskType(req.taskType());
        def.setConfig(req.config());
        def.setCronExpression(req.cronExpression());
        if (req.enabled() != null) def.setEnabled(req.enabled());
        if (req.maxRetries() != null) def.setMaxRetries(req.maxRetries());
        if (req.retryDelaySeconds() != null) def.setRetryDelaySeconds(req.retryDelaySeconds());
        if (req.timeoutSeconds() != null) def.setTimeoutSeconds(req.timeoutSeconds());
        def.setUserId(req.userId());
    }
}
