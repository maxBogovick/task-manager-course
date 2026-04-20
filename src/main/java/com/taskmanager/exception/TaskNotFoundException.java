package com.taskmanager.exception;

/**
 * Thrown when a task definition with the requested ID does not exist.
 */
public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(Long id) {
        super("Task definition not found with id: " + id);
    }
}
