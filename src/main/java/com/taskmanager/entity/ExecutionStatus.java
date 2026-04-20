package com.taskmanager.entity;

/**
 * Lifecycle statuses for a single task execution.
 */
public enum ExecutionStatus {
    /** Created, waiting to be picked up by the orchestrator. */
    PENDING,
    /** Currently running. */
    RUNNING,
    /** Finished successfully. */
    COMPLETED,
    /** Finished with an error. */
    FAILED,
    /** Manually cancelled by the user. */
    CANCELLED,
    /** Exceeded the configured timeout. */
    TIMED_OUT
}
