package com.taskmanager.mapper;

import com.taskmanager.dto.TaskDefinitionRequest;
import com.taskmanager.dto.TaskDefinitionResponse;
import com.taskmanager.entity.TaskDefinition;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TaskDefinitionMapper {

    public TaskDefinition toEntity(TaskDefinitionRequest request) {
        TaskDefinition entity = new TaskDefinition();
        entity.setName(request.name());
        entity.setDescription(request.description());
        entity.setEnabled(true);
        entity.setCreatedAt(LocalDateTime.now());
        return entity;
    }

    public TaskDefinitionResponse toResponse(TaskDefinition entity) {
        return new TaskDefinitionResponse(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.isEnabled(),
                entity.getCreatedAt()
        );
    }
}

