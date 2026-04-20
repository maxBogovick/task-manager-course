package com.taskmanager.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Enables Spring Scheduling for the orchestrator and cron scheduler.
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
}
