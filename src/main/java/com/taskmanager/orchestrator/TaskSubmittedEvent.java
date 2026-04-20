package com.taskmanager.orchestrator;

import com.taskmanager.entity.TaskDefinition;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Published after a new TaskExecution record is persisted.
 * The listener dispatches the actual work to the thread pool only after the
 * publishing transaction commits — guaranteeing the record is visible to other sessions.
 */
public record TaskSubmittedEvent(Long executionId, TaskDefinition definition, AtomicBoolean cancelFlag) {}
