# Lab 07 - Debug Scheduler

## Цель

Научиться расследовать cron-based behavior и persisted scheduler state.

## Симптом

Enabled definition с cron expression должен запускаться, когда cron due. После запуска `lastFiredAt` должен обновиться, чтобы task не запускался повторно на каждом scheduler tick.

## Файлы для чтения

- `TaskSchedulerService`
- `TaskDefinitionRepository`
- `TaskDefinitionService.updateLastFiredAt`
- `TaskDefinition`
- `V2__add_last_fired_at_and_composite_index.sql`

## Задание

1. Найди scheduler method.
2. Объясни, какие definitions он выбирает.
3. Объясни, как считается `nextRun`.
4. Найди, где обновляется `lastFiredAt`.
5. Объясни, зачем `lastFiredAt` хранится в БД.
6. Предложи тест или manual verification для cron task.

## Ограничения

- не запускать disabled tasks;
- не игнорировать `lastFiredAt`;
- не обновлять `lastFiredAt` до успешного submit без причины;
- не ловить все exceptions молча.

## Подсказки

### Уровень 1

Ищи `findAllByEnabledTrueAndCronExpressionIsNotNull`.

### Уровень 2

Если `lastFiredAt` null, baseline берется как `now.minusMinutes(1)`.

### Уровень 3

Persisted `lastFiredAt` защищает от double fire после restart.

## Эталонное рассуждение

Scheduler каждые 10 секунд берет enabled definitions с cron expression, считает следующий запуск от `lastFiredAt` или от недавнего baseline, и если `nextRun` не позже `now`, вызывает orchestrator. После submit обновляет `lastFiredAt`. Если это поле не обновлять, одна и та же due cron expression может запускаться снова и снова.

## Проверка

```bash
mvn test
```

Для отдельного теста удобно создавать cron, который due в текущую минуту, вызывать `checkScheduledTasks()` и проверять execution count plus `lastFiredAt`.

