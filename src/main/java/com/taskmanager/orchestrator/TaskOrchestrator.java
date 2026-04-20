package com.taskmanager.orchestrator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.entity.ExecutionStatus;
import com.taskmanager.entity.TaskDefinition;
import com.taskmanager.entity.TaskExecution;
import com.taskmanager.exception.TaskAlreadyRunningException;
import com.taskmanager.executor.ExecutionContext;
import com.taskmanager.executor.ExecutionResult;
import com.taskmanager.executor.TaskExecutor;
import com.taskmanager.executor.TaskExecutorRegistry;
import com.taskmanager.repository.TaskExecutionRepository;
import com.taskmanager.service.TaskExecutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskOrchestrator {

    private final TaskExecutorRegistry executorRegistry;
    private final TaskExecutionRepository executionRepo;
    private final TaskExecutionService executionService;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    /**
     * Self-reference through the Spring proxy — required so that {@code retryFailedTasks}
     * (a self-call) triggers the {@code @Transactional(REQUIRES_NEW)} on {@code submitTask}.
     */
    @Autowired
    @Lazy
    private TaskOrchestrator self;

    private final ExecutorService taskPool = Executors.newVirtualThreadPerTaskExecutor();

    /** Limits concurrent executions to prevent overloading external systems. */
    private final Semaphore concurrencyLimit = new Semaphore(Runtime.getRuntime().availableProcessors() * 4);

    /** In-flight executions: executionId → (future, cancelFlag, startedAt). */
    private final ConcurrentHashMap<Long, RunningTask> runningTasks = new ConcurrentHashMap<>();

    private record RunningTask(Future<?> future, AtomicBoolean cancelFlag, LocalDateTime startedAt) {}

    // ── Public API ───────────────────────────────────────────────────────

    /**
     * Persist a new execution record and schedule the task for dispatch.
     * Uses REQUIRES_NEW so the record is committed — and visible to the background
     * thread — before the {@link TaskSubmittedEvent} listener fires.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TaskExecution submitTask(TaskDefinition definition) {
        return submitTask(definition, 1);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TaskExecution submitTask(TaskDefinition definition, int attempt) {
        long running = executionRepo.countByTaskDefinitionIdAndStatus(
                definition.getId(), ExecutionStatus.RUNNING);
        if (running > 0) {
            throw new TaskAlreadyRunningException(definition.getName());
        }

        TaskExecution execution = new TaskExecution();
        execution.setTaskDefinition(definition);
        execution.setStatus(ExecutionStatus.PENDING);
        execution.setAttempt(attempt);
        TaskExecution saved = executionRepo.save(execution);

        log.info("▶ Submitting task '{}' (def={}, exec={}, attempt={})",
                definition.getName(), definition.getId(), saved.getId(), attempt);

        AtomicBoolean cancelFlag = new AtomicBoolean(false);
        // Register the cancel flag immediately so cancelExecution works before dispatch
        runningTasks.put(saved.getId(), new RunningTask(null, cancelFlag, LocalDateTime.now()));

        // Dispatch only after this transaction commits — prevents the background thread
        // from reading a PENDING record that doesn't exist yet in other sessions
        eventPublisher.publishEvent(new TaskSubmittedEvent(saved.getId(), definition, cancelFlag));

        return saved;
    }

    /**
     * Fires after the publishing transaction commits.
     * At this point the execution record is guaranteed to be visible to all sessions.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void dispatchTask(TaskSubmittedEvent event) {
        RunningTask placeholder = runningTasks.get(event.executionId());
        if (placeholder == null || placeholder.cancelFlag().get()) {
            log.info("Execution {} was cancelled before dispatch, skipping", event.executionId());
            return;
        }

        Future<?> future = taskPool.submit(() -> {
            concurrencyLimit.acquireUninterruptibly();
            try {
                executeTask(event.executionId(), event.definition(), event.cancelFlag());
            } finally {
                concurrencyLimit.release();
            }
        });

        runningTasks.put(event.executionId(),
                new RunningTask(future, event.cancelFlag(), placeholder.startedAt()));
    }

    @Transactional
    public boolean cancelExecution(Long executionId) {
        RunningTask rt = runningTasks.remove(executionId);
        if (rt != null) {
            rt.cancelFlag().set(true);
            if (rt.future() != null) rt.future().cancel(true);
            log.info("⏹ Cancelled execution {}", executionId);
        }

        return executionRepo.findById(executionId).map(exec -> {
            if (exec.getStatus() == ExecutionStatus.RUNNING
                    || exec.getStatus() == ExecutionStatus.PENDING) {
                exec.setStatus(ExecutionStatus.CANCELLED);
                exec.setFinishedAt(LocalDateTime.now());
                exec.setErrorMessage("Cancelled by user");
                executionRepo.save(exec);
                return true;
            }
            return false;
        }).orElse(false);
    }

    // ── Scheduled monitoring ─────────────────────────────────────────────

    @Scheduled(fixedDelay = 15_000)
    @Transactional
    public void monitorTimeouts() {
        LocalDateTime now = LocalDateTime.now();
        var timedOut = executionRepo.findAllRunning().stream()
                .filter(exec -> exec.getStartedAt().plusSeconds(
                        exec.getTaskDefinition().getTimeoutSeconds()).isBefore(now))
                .toList();

        for (var exec : timedOut) {
            int timeoutSec = exec.getTaskDefinition().getTimeoutSeconds();
            log.warn("⏰ Execution {} timed out ({}s limit)", exec.getId(), timeoutSec);

            RunningTask rt = runningTasks.remove(exec.getId());
            if (rt != null) {
                rt.cancelFlag().set(true);
                if (rt.future() != null) rt.future().cancel(true);
            }

            exec.setStatus(ExecutionStatus.TIMED_OUT);
            exec.setFinishedAt(now);
            exec.setErrorMessage("Timed out after " + timeoutSec + " seconds");
        }

        if (!timedOut.isEmpty()) {
            executionRepo.saveAll(timedOut);
        }
    }

    @Scheduled(fixedDelay = 30_000)
    @Transactional(readOnly = true)
    public void retryFailedTasks() {
        var retryable = executionRepo.findRetryableExecutions(LocalDateTime.now());

        for (TaskExecution failed : retryable) {
            TaskDefinition def = failed.getTaskDefinition();
            int retryDelay = def.getRetryDelaySeconds();

            if (failed.getFinishedAt() != null
                    && failed.getFinishedAt().plusSeconds(retryDelay).isAfter(LocalDateTime.now())) {
                continue;
            }

            log.info("🔄 Retrying task '{}' (attempt {}→{})",
                    def.getName(), failed.getAttempt(), failed.getAttempt() + 1);

            // Use self-proxy so @Transactional(REQUIRES_NEW) on submitTask takes effect
            self.submitTask(def, failed.getAttempt() + 1);
        }
    }

    public int getRunningCount() {
        return runningTasks.size();
    }

    // ── Internal execution ───────────────────────────────────────────────

    private void executeTask(Long executionId, TaskDefinition definition, AtomicBoolean cancelFlag) {
        // Phase 1 — transaction: mark as RUNNING
        executionService.markAsRunning(executionId);

        TaskExecutor executor = executorRegistry.getExecutor(definition.getTaskType());
        if (executor == null) {
            executionService.finishExecution(executionId, false, null,
                    "No executor registered for type: " + definition.getTaskType());
            return;
        }

        Map<String, Object> configMap;
        try {
            configMap = objectMapper.readValue(
                    definition.getConfig(), new TypeReference<>() {});
        } catch (Exception e) {
            executionService.finishExecution(executionId, false, null,
                    "Failed to parse config JSON: " + e.getMessage());
            return;
        }

        // Phase 2 — outside any transaction: execute (may take seconds/minutes)
        ExecutionContext context = new ExecutionContext(executionId, definition.getId(), configMap, cancelFlag);
        ExecutionResult result;
        try {
            result = executor.execute(context);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            executionService.finishExecution(executionId, false, null, "Interrupted: " + e.getMessage());
            return;
        } catch (Exception e) {
            log.error("Task execution {} failed with exception", executionId, e);
            executionService.finishExecution(executionId, false, null,
                    e.getClass().getSimpleName() + ": " + e.getMessage());
            return;
        } finally {
            runningTasks.remove(executionId);
        }

        // Phase 3 — transaction: persist result
        if (cancelFlag.get()) {
            executionService.finishExecution(executionId, false, result.output(), "Cancelled during execution");
        } else {
            executionService.finishExecution(executionId, result.success(), result.output(), result.errorMessage());
        }
    }
}
