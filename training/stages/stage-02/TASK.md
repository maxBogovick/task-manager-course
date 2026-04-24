# Задание Stage 02

## Цель этапа

Добавить вторую сущность `TaskExecution` и связать ее с `TaskDefinition`.

## Что нужно реализовать

1. создание execution для существующей задачи;
2. список execution-ов по `taskId`;
3. проверку существования родительской задачи;
4. корректное сохранение связи в базе.

## С какими файлами нужно работать

- [TaskExecutionController.java](./start/src/main/java/com/taskmanager/controller/TaskExecutionController.java)
- [TaskExecutionService.java](./start/src/main/java/com/taskmanager/service/TaskExecutionService.java)
- [TaskExecutionRepository.java](./start/src/main/java/com/taskmanager/repository/TaskExecutionRepository.java)
- [TaskExecutionMapper.java](./start/src/main/java/com/taskmanager/mapper/TaskExecutionMapper.java)
- [TaskExecution.java](./start/src/main/java/com/taskmanager/entity/TaskExecution.java)
- [TaskExecutionStage02Tests.java](./start/src/test/java/com/taskmanager/TaskExecutionStage02Tests.java)

## Что уже подготовлено

- рабочий Stage 01;
- новая entity;
- relation mapping;
- DTO;
- controller;
- mapper;
- repository;
- migration;
- test scaffold.

## На что нужно обратить внимание

- `TaskExecution` должен быть связан с существующей задачей;
- новая логика должна жить в `TaskExecutionService`;
- связь должна быть выражена и в БД, и в коде;
- ошибки при работе с несуществующей задачей должны быть осмысленными;
- этот этап пока не про orchestration, а про модель и связи.

## Что пока не нужно добавлять

На этом этапе не нужно:

- orchestration;
- retry/timeout/cancel;
- async execution;
- полный CRUD для execution;
- сложный lifecycle;
- расширяемые task types.

## Практический результат

После завершения этапа система должна уметь работать с двумя связанными сущностями:

- `TaskDefinition`;
- `TaskExecution`.

## Навигация

- Старт: [Stage 02](./README.md)
- Назад: [LECTURE.md](./LECTURE.md)
- Далее: [CHECKLIST.md](./CHECKLIST.md)

