package com.taskmanager.service;

import com.taskmanager.dto.TaskDefinitionRequest;
import com.taskmanager.dto.TaskDefinitionResponse;
import com.taskmanager.entity.TaskDefinition;
import com.taskmanager.exception.TaskNotFoundException;
import com.taskmanager.mapper.TaskDefinitionMapper;
import com.taskmanager.repository.TaskDefinitionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskDefinitionService {

    private final TaskDefinitionRepository taskDefinitionRepository;
    private final TaskDefinitionMapper taskDefinitionMapper;

    public TaskDefinitionService(
            TaskDefinitionRepository taskDefinitionRepository,
            TaskDefinitionMapper taskDefinitionMapper
    ) {
        this.taskDefinitionRepository = taskDefinitionRepository;
        this.taskDefinitionMapper = taskDefinitionMapper;
    }

    public TaskDefinitionResponse create(TaskDefinitionRequest request) {
        TaskDefinition entity = taskDefinitionMapper.toEntity(request);
        TaskDefinition saved = taskDefinitionRepository.save(entity);
        return taskDefinitionMapper.toResponse(saved);
    }

    public TaskDefinitionResponse getById(Long id) {
        TaskDefinition entity = taskDefinitionRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        return taskDefinitionMapper.toResponse(entity);
    }

    public List<TaskDefinitionResponse> getAll() {
        return taskDefinitionRepository.findAll().stream()
                .map(taskDefinitionMapper::toResponse)
                .toList();
    }
}
