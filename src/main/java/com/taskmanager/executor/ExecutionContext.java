package com.taskmanager.executor;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Context provided to a {@link TaskExecutor} during execution.
 *
 * @param executionId  the execution record ID
 * @param definitionId the parent task definition ID
 * @param config       parsed JSON config as a map
 * @param cancelled    cancellation flag — executors should check this periodically
 */
public record ExecutionContext(
        Long executionId,
        Long definitionId,
        Map<String, Object> config,
        AtomicBoolean cancelled
) {
    /** Check if this execution has been cancelled. */
    public boolean isCancelled() {
        return cancelled.get();
    }
}
