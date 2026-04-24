package com.taskmanager.mapper;

import com.taskmanager.dto.TaskExecutionRequest;
import com.taskmanager.dto.TaskExecutionResponse;
import com.taskmanager.entity.ExecutionStatus;
import com.taskmanager.entity.TaskDefinition;
import com.taskmanager.entity.TaskExecution;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TaskExecutionMapper {

    public TaskExecution toEntity(TaskDefinition taskDefinition, TaskExecutionRequest request) {
        TaskExecution entity = new TaskExecution();
        entity.setTaskDefinition(taskDefinition);
        entity.setStatus(ExecutionStatus.PENDING);
        entity.setOutput(request.output());
        entity.setCreatedAt(LocalDateTime.now());
        return entity;
    }

    public TaskExecutionResponse toResponse(TaskExecution entity) {
        return new TaskExecutionResponse(
                entity.getId(),
                entity.getTaskDefinition().getId(),
                entity.getStatus().name(),
                entity.getOutput(),
                entity.getCreatedAt()
        );
    }
}

