# Архитектурная Карта

Эта карта нужна не для заучивания, а для ориентирования во время debugging.

## Главные доменные объекты

`TaskDefinition` - описание того, что нужно запускать:

- name;
- taskType;
- config;
- cronExpression;
- retry policy;
- timeout.

`TaskExecution` - один конкретный запуск definition:

- status;
- startedAt;
- finishedAt;
- output;
- errorMessage;
- attempt.

## Основные маршруты

### Создать definition

```text
POST /api/tasks
-> TaskDefinitionController
-> TaskDefinitionService.create
-> validate cron
-> ConfigValidator.validate
-> TaskMapper.toEntity
-> TaskDefinitionRepository.save
```

### Запустить task

```text
POST /api/tasks/{id}/run
-> TaskDefinitionController
-> TaskRunService
-> TaskDefinitionService.getEntityById
-> TaskOrchestrator.submitTask
-> TaskSubmittedEvent
-> TaskOrchestrator.dispatchTask
-> TaskOrchestrator.executeTask
-> TaskExecutor.execute
-> TaskExecutionService.finishExecution
```

### Отменить execution

```text
POST /api/executions/{id}/cancel
-> TaskExecutionController
-> TaskOrchestrator.cancelExecution
-> runningTasks cancel flag
-> future.cancel(true)
-> TaskExecution status CANCELLED
```

## Где искать баги

- Неверный HTTP status: controller или exception handler.
- Неверная validation: DTO annotations, `ConfigValidator`, typed config record.
- Неверный execution status: `TaskOrchestrator`, `TaskExecutionService`.
- Task не стартует: `TaskRunService`, `TaskOrchestrator.submitTask`, event listener.
- Retry не работает: `TaskOrchestrator.retryFailedTasks`, repository query.
- Cron не работает: `TaskSchedulerService`, `lastFiredAt`, cron expression.
- Новый executor не виден: `TaskExecutorRegistry`, `TaskExecutor.getType()`, Spring `@Component`.

## Статусы выполнения

```text
PENDING -> RUNNING -> COMPLETED
                  -> FAILED -> retry -> RUNNING
                  -> TIMED_OUT
                  -> CANCELLED
```

## Самые важные файлы

- `src/main/java/com/taskmanager/controller/TaskDefinitionController.java`
- `src/main/java/com/taskmanager/controller/TaskExecutionController.java`
- `src/main/java/com/taskmanager/service/TaskDefinitionService.java`
- `src/main/java/com/taskmanager/service/TaskRunService.java`
- `src/main/java/com/taskmanager/service/TaskExecutionService.java`
- `src/main/java/com/taskmanager/service/ConfigValidator.java`
- `src/main/java/com/taskmanager/orchestrator/TaskOrchestrator.java`
- `src/main/java/com/taskmanager/orchestrator/TaskSchedulerService.java`
- `src/main/java/com/taskmanager/executor/TaskExecutorRegistry.java`
- `src/test/java/com/taskmanager/TaskManagerApplicationTests.java`

