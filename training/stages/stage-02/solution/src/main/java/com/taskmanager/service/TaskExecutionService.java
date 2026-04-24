package com.taskmanager.service;

import com.taskmanager.dto.TaskExecutionRequest;
import com.taskmanager.dto.TaskExecutionResponse;
import com.taskmanager.entity.TaskDefinition;
import com.taskmanager.entity.TaskExecution;
import com.taskmanager.exception.TaskNotFoundException;
import com.taskmanager.mapper.TaskExecutionMapper;
import com.taskmanager.repository.TaskDefinitionRepository;
import com.taskmanager.repository.TaskExecutionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskExecutionService {

    private final TaskDefinitionRepository taskDefinitionRepository;
    private final TaskExecutionRepository taskExecutionRepository;
    private final TaskExecutionMapper taskExecutionMapper;

    public TaskExecutionService(
            TaskDefinitionRepository taskDefinitionRepository,
            TaskExecutionRepository taskExecutionRepository,
            TaskExecutionMapper taskExecutionMapper
    ) {
        this.taskDefinitionRepository = taskDefinitionRepository;
        this.taskExecutionRepository = taskExecutionRepository;
        this.taskExecutionMapper = taskExecutionMapper;
    }

    public TaskExecutionResponse create(Long taskId, TaskExecutionRequest request) {
        TaskDefinition taskDefinition = taskDefinitionRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        TaskExecution entity = taskExecutionMapper.toEntity(taskDefinition, request);
        TaskExecution saved = taskExecutionRepository.save(entity);
        return taskExecutionMapper.toResponse(saved);
    }

    public List<TaskExecutionResponse> getByTaskId(Long taskId) {
        if (!taskDefinitionRepository.existsById(taskId)) {
            throw new TaskNotFoundException(taskId);
        }

        return taskExecutionRepository.findAllByTaskDefinitionId(taskId).stream()
                .map(taskExecutionMapper::toResponse)
                .toList();
    }
}
