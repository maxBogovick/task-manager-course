package com.taskmanager.executor;

import java.util.Optional;

/**
 * Core contract for all task executors (plugins).
 * Each implementation handles a specific task type.
 *
 * <p>Implementations MUST be thread-safe — multiple executions may run concurrently.</p>
 */
public interface TaskExecutor {

    /**
     * Execute the task with the given context.
     *
     * @param context execution context with config, cancellation flag, etc.
     * @return result of the execution
     * @throws Exception on unrecoverable errors
     */
    ExecutionResult execute(ExecutionContext context) throws Exception;

    /**
     * @return the unique type key this executor handles (e.g. "SHELL_COMMAND")
     */
    String getType();

    /**
     * Returns the typed config class used for JSON validation at task creation time.
     * Defaults to empty — override to enable config validation for this executor type.
     */
    default Optional<Class<?>> getConfigClass() {
        return Optional.empty();
    }
}
