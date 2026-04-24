# Lab 04 - Debug Cancellation

## Цель

Научиться расследовать race condition между cancel request и background execution.

## Симптом

Если пользователь отменил `RUNNING` или `PENDING` execution, финальный статус не должен позже стать `FAILED` или `COMPLETED` из фонового потока.

## Файлы для чтения

- `TaskExecutionController`
- `TaskOrchestrator.cancelExecution`
- `TaskOrchestrator.executeTask`
- `TaskExecutionService.finishExecution`
- `ExecutionContext`
- `ShellCommandExecutor`

## Задание

1. Найди endpoint отмены.
2. Объясни два механизма отмены: cancel flag и `future.cancel(true)`.
3. Найди место, где статус записывается как `CANCELLED`.
4. Найди защиту от перезаписи `CANCELLED`.
5. Предложи regression test для сценария "cancel during long-running command".
6. Объясни, почему cooperative cancellation важнее одного interrupt.

## Ограничения

- не считать interrupt достаточным;
- не удалять cancel flag;
- не менять статус `CANCELLED` на `FAILED`;
- не делать busy waiting в тесте без timeout.

## Подсказки

### Уровень 1

Ищи `cancelExecution`.

### Уровень 2

Даже после `future.cancel(true)` executor может успеть вернуть result.

### Уровень 3

Защита должна быть в write path, который завершает execution.

## Эталонное рассуждение

Отмена удаляет execution из `runningTasks`, выставляет cancel flag, вызывает `future.cancel(true)` и сохраняет `CANCELLED`, если execution еще `RUNNING` или `PENDING`. Но фоновый поток может дойти до `finishExecution`. Поэтому `TaskExecutionService.finishExecution` обязан не перезаписывать `CANCELLED` и `TIMED_OUT`. Это ключевой guard от race condition.

## Проверка

```bash
mvn test -Dtest="TaskManagerApplicationTests#shouldCancelExecution"
```

Для усиленного lab студент добавляет отдельный long-running shell command test.

