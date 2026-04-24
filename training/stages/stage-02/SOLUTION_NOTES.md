# Solution Notes Stage 02

## Что реализовано

- `TaskExecutionService` создает execution для существующей задачи;
- `TaskExecutionService` возвращает список execution-ов по `taskId`;
- отсутствующая родительская задача приводит к `TaskNotFoundException`;
- tests покрывают happy path и базовый not-found сценарий.

## Почему решение остается простым

Это решение специально не вводит:

- orchestration;
- lifecycle beyond basic status;
- retry/timeout/cancel;
- async execution;
- полную execution-платформу.

## Зачем это важно

Эталонное решение должно закрывать именно задачу Stage 02:

- вторую сущность;
- связь;
- отдельный service-срез;
- relation-aware API.

## Навигация

- Старт: [Stage 02](./README.md)
- Назад: [CHECKLIST.md](./CHECKLIST.md)
- Далее: [Этапы курса](../README.md)

