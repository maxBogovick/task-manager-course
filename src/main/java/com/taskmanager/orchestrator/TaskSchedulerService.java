package com.taskmanager.orchestrator;

import com.taskmanager.entity.TaskDefinition;
import com.taskmanager.repository.TaskDefinitionRepository;
import com.taskmanager.service.TaskDefinitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Cron-based scheduler that checks task definitions every 10 seconds
 * and submits them to the orchestrator when their cron expression is due.
 *
 * <p>Uses the DB-persisted {@code last_fired_at} column so scheduled tasks
 * resume correctly after a restart without double-firing.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TaskSchedulerService {

    private final TaskDefinitionRepository definitionRepo;
    private final TaskDefinitionService definitionService;
    private final TaskOrchestrator orchestrator;

    @Scheduled(fixedDelay = 10_000)
    @Transactional(readOnly = true)
    public void checkScheduledTasks() {
        List<TaskDefinition> definitions = definitionRepo.findAllByEnabledTrueAndCronExpressionIsNotNull();
        LocalDateTime now = LocalDateTime.now();

        for (TaskDefinition def : definitions) {
            try {
                CronExpression cron = CronExpression.parse(def.getCronExpression());
                LocalDateTime lastFired = def.getLastFiredAt();
                LocalDateTime nextRun = cron.next(lastFired != null ? lastFired : now.minusMinutes(1));

                if (nextRun != null && !nextRun.isAfter(now)) {
                    log.info("⏰ Cron trigger for task '{}' (cron={})", def.getName(), def.getCronExpression());
                    orchestrator.submitTask(def);
                    definitionService.updateLastFiredAt(def.getId(), now);
                }
            } catch (IllegalArgumentException e) {
                log.error("Invalid cron expression '{}' for task '{}': {}",
                        def.getCronExpression(), def.getName(), e.getMessage());
            }
        }
    }
}
