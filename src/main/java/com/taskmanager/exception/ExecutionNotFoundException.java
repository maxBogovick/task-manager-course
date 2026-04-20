package com.taskmanager.exception;

/**
 * Thrown when a task execution with the requested ID does not exist.
 */
public class ExecutionNotFoundException extends RuntimeException {

    public ExecutionNotFoundException(Long id) {
        super("Task execution not found with id: " + id);
    }
}
