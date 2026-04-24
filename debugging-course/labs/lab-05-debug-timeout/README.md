# Lab 05 - Debug Timeout

## Цель

Научиться читать scheduled monitor и понимать timeout lifecycle.

## Симптом

Execution, который работает дольше `timeoutSeconds`, должен стать `TIMED_OUT`, получить `finishedAt` и error message.

## Файлы для чтения

- `TaskOrchestrator.monitorTimeouts`
- `TaskExecutionRepository`
- `TaskExecutionService.finishExecution`
- `TaskDefinition`
- `TaskExecution`

## Задание

1. Найди scheduled method, который проверяет timeout.
2. Объясни, какие executions он выбирает.
3. Объясни, почему timeout считается от `startedAt`, а не от `createdAt`.
4. Найди, как останавливается background task.
5. Найди защиту от последующей перезаписи `TIMED_OUT`.
6. Предложи тест с коротким timeout и долгой командой.

## Ограничения

- не менять fixed delay без причины;
- не использовать `Thread.sleep` без объяснения;
- не считать `PENDING` timed out по `startedAt`;
- не перезаписывать timeout результатом executor.

## Подсказки

### Уровень 1

Ищи `@Scheduled(fixedDelay = 15_000)`.

### Уровень 2

Repository method должен искать только `RUNNING`.

### Уровень 3

Если execution стал `TIMED_OUT`, `finishExecution` не должен менять его статус.

## Эталонное рассуждение

Timeout monitor берет `findAllRunning()`, сравнивает `startedAt + timeoutSeconds` с текущим временем, затем выставляет cancel flag, отменяет future и сохраняет `TIMED_OUT`. Защита от race condition находится в `TaskExecutionService.finishExecution`: если статус уже `TIMED_OUT`, метод возвращается без записи результата executor.

## Проверка

```bash
mvn test
```

Для точечного теста лучше вынести polling helper, чтобы ждать статус с предельным временем ожидания.

