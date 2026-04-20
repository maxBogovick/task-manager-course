package com.taskmanager.exception;

/**
 * Thrown when a task definition already has a running execution
 * and a duplicate run is attempted.
 */
public class TaskAlreadyRunningException extends IllegalStateException {

    public TaskAlreadyRunningException(String taskName) {
        super("Task '" + taskName + "' already has a running execution");
    }
}
