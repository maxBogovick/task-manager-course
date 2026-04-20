package com.taskmanager.service;

import com.taskmanager.entity.ExecutionStatus;
import com.taskmanager.entity.TaskExecution;
import com.taskmanager.repository.TaskExecutionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Cleans up executions that were left in RUNNING state by a previous JVM crash or restart.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReconciliationService {

    private final TaskExecutionRepository executionRepo;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void reconcileStaleExecutions() {
        List<TaskExecution> stale = executionRepo.findAllByStatus(ExecutionStatus.RUNNING);
        if (stale.isEmpty()) return;

        log.warn("Found {} stale RUNNING executions on startup — marking as FAILED", stale.size());
        LocalDateTime now = LocalDateTime.now();
        for (TaskExecution exec : stale) {
            exec.setStatus(ExecutionStatus.FAILED);
            exec.setFinishedAt(now);
            exec.setErrorMessage("Interrupted by application restart");
        }
        executionRepo.saveAll(stale);
    }
}
