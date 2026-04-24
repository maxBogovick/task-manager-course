package com.taskmanager.service;

import com.taskmanager.dto.TaskExecutionRequest;
import com.taskmanager.dto.TaskExecutionResponse;
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
        throw new UnsupportedOperationException("TODO: implement create(taskId, request)");
    }

    public List<TaskExecutionResponse> getByTaskId(Long taskId) {
        throw new UnsupportedOperationException("TODO: implement getByTaskId(taskId)");
    }
}

