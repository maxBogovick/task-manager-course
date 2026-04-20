package com.taskmanager.service;

import com.taskmanager.dto.CancelExecutionResponse;
import com.taskmanager.dto.OrchestratorStatusResponse;
import com.taskmanager.dto.TaskExecutionResponse;
import com.taskmanager.entity.TaskDefinition;
import com.taskmanager.entity.TaskExecution;
import com.taskmanager.mapper.TaskMapper;
import com.taskmanager.orchestrator.TaskOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Facade for task run/cancel use cases.
 * Isolates controllers from direct orchestrator and entity access.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TaskRunService {

    private final TaskOrchestrator orchestrator;
    private final TaskDefinitionService definitionService;
    private final TaskMapper mapper;

    public TaskExecutionResponse runTask(Long definitionId) {
        TaskDefinition def = definitionService.getEntityById(definitionId);

        if (!Boolean.TRUE.equals(def.getEnabled())) {
            throw new IllegalStateException(
                    "Task '" + def.getName() + "' is disabled and cannot be run manually");
        }

        TaskExecution exec = orchestrator.submitTask(def);
        log.info("Task '{}' (id={}) submitted, execution id={}", def.getName(), def.getId(), exec.getId());
        return mapper.toExecutionResponse(exec);
    }

    public CancelExecutionResponse cancelExecution(Long executionId) {
        boolean cancelled = orchestrator.cancelExecution(executionId);
        return new CancelExecutionResponse(executionId, cancelled);
    }

    public OrchestratorStatusResponse getOrchestratorStatus() {
        return new OrchestratorStatusResponse(orchestrator.getRunningCount(), "ACTIVE");
    }
}
