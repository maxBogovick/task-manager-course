# Lab 03 - Debug Run Request Flow

## Цель

Научиться читать запуск task от HTTP request до executor.

## Симптом

`POST /api/tasks/{id}/run` должен создать `TaskExecution` со статусом `PENDING`, затем фоновый поток должен довести его до `COMPLETED` или `FAILED`.

## Файлы для чтения

- `TaskDefinitionController`
- `TaskRunService`
- `TaskDefinitionService`
- `TaskOrchestrator`
- `TaskSubmittedEvent`
- `TaskExecutionService`
- `TaskExecutorRegistry`
- `ShellCommandExecutor`

## Задание

1. Найди endpoint запуска.
2. Построй цепочку до `TaskOrchestrator.submitTask`.
3. Объясни, зачем execution сначала сохраняется как `PENDING`.
4. Объясни, почему dispatch идет через event after commit.
5. Найди место, где статус становится `RUNNING`.
6. Найди место, где статус становится `COMPLETED`.

## Ограничения

- не менять async model;
- не заменять event listener прямым вызовом;
- не удалять transaction boundaries.

## Подсказки

### Уровень 1

Ищи endpoint с suffix `/run`.

### Уровень 2

`submitTask` не выполняет task сразу; он сохраняет execution и публикует event.

### Уровень 3

Фоновому потоку нужна committed execution row, поэтому важен `AFTER_COMMIT`.

## Эталонное рассуждение

Запуск идет через controller и `TaskRunService`, который достает definition и вызывает orchestrator. `TaskOrchestrator.submitTask` создает `TaskExecution(PENDING)`, кладет cancel flag в `runningTasks` и публикует `TaskSubmittedEvent`. `dispatchTask` срабатывает after commit, отправляет работу в virtual thread pool. `executeTask` вызывает `executionService.markAsRunning`, выполняет executor и затем вызывает `finishExecution`.

## Проверка

```bash
mvn test -Dtest="TaskManagerApplicationTests#shouldRunTaskAndCreateExecution"
mvn test -Dtest="TaskManagerApplicationTests#shouldGetExecutionResult"
```

