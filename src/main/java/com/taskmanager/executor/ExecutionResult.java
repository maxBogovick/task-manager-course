package com.taskmanager.executor;

/**
 * Result of a task execution.
 *
 * @param success      whether the task completed successfully
 * @param output       captured output / result message
 * @param errorMessage error details (null on success)
 */
public record ExecutionResult(
        boolean success,
        String output,
        String errorMessage
) {
    public static ExecutionResult ok(String output) {
        return new ExecutionResult(true, output, null);
    }

    public static ExecutionResult fail(String errorMessage) {
        return new ExecutionResult(false, null, errorMessage);
    }

    public static ExecutionResult fail(String output, String errorMessage) {
        return new ExecutionResult(false, output, errorMessage);
    }
}
