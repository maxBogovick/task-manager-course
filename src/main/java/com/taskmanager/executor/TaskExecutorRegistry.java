package com.taskmanager.executor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Registry that maps task type keys to their {@link TaskExecutor} implementations.
 * All executors are auto-discovered via Spring DI — no manual wiring required.
 */
@Component
@Slf4j
public class TaskExecutorRegistry {

    private final Map<String, TaskExecutor> executors = new HashMap<>();

    public TaskExecutorRegistry(List<TaskExecutor> executorList) {
        for (TaskExecutor executor : executorList) {
            executors.put(executor.getType(), executor);
            log.info("Registered task executor: {}", executor.getType());
        }
    }

    public TaskExecutor getExecutor(String type) {
        return executors.get(type);
    }

    public Set<String> getRegisteredTypes() {
        return executors.keySet();
    }

    public Optional<Class<?>> getConfigClass(String type) {
        TaskExecutor executor = executors.get(type);
        return executor != null ? executor.getConfigClass() : Optional.empty();
    }
}
