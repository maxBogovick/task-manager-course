package com.taskmanager.service;

import com.taskmanager.dto.TaskDefinitionRequest;
import com.taskmanager.dto.TaskDefinitionResponse;
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
        throw new UnsupportedOperationException("TODO: implement create()");
    }

    public TaskDefinitionResponse getById(Long id) {
        throw new UnsupportedOperationException("TODO: implement getById()");
    }

    public List<TaskDefinitionResponse> getAll() {
        throw new UnsupportedOperationException("TODO: implement getAll()");
    }
}

