package com.taskmanager.dto;

public record CancelExecutionResponse(Long executionId, boolean cancelled) {}
