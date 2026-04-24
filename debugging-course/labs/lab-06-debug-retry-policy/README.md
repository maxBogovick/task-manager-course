# Lab 06 - Debug Retry Policy

## Цель

Научиться расследовать retry behavior и off-by-one ошибки.

## Симптом

FAILED execution должен быть перезапущен только если retry policy разрешает следующую попытку и прошел `retryDelaySeconds`.

## Файлы для чтения

- `TaskOrchestrator.retryFailedTasks`
- `TaskOrchestrator.submitTask`
- `TaskExecutionRepository`
- `TaskDefinition`
- `TaskExecution`

## Задание

1. Найди scheduled retry method.
2. Найди repository query, которая выбирает retryable executions.
3. Объясни разницу между `maxRetries` и `attempt`.
4. Проверь, где увеличивается attempt.
5. Объясни, почему `submitTask` вызывается через `self`.
6. Предложи тест для задачи, которая всегда падает.

## Ограничения

- не ретраить `CANCELLED` и `TIMED_OUT`, если бизнес-правило этого не требует;
- не сбрасывать attempt в 1 при retry;
- не обходить concurrency/running protection;
- не удалять `self` proxy без понимания transactions.

## Подсказки

### Уровень 1

Ищи `@Scheduled(fixedDelay = 30_000)`.

### Уровень 2

Retry создает новый `TaskExecution`, а не переписывает старый.

### Уровень 3

Self proxy нужен, чтобы self-call прошел через Spring transactional proxy.

## Эталонное рассуждение

Retry scanner выбирает failed executions, проверяет delay и вызывает `submitTask(def, failed.getAttempt() + 1)`. Новый attempt должен быть больше предыдущего. Если при регрессии attempt не растет, retry может стать бесконечным или нарушить `maxRetries`. Если `self` заменить прямым `this.submitTask`, можно потерять ожидаемое transactional behavior для `REQUIRES_NEW`.

## Проверка

```bash
mvn test
```

Дополнительный regression test должен проверять количество execution rows и attempt values.

