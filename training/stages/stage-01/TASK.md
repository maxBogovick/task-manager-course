# Задание Stage 01

## Цель этапа

Собрать первый доменный MVP на основе `TaskDefinition`.

## Что нужно реализовать

1. `POST /api/tasks`
2. `GET /api/tasks/{id}`
3. `GET /api/tasks`
4. сохранение данных в базе;
5. базовую validation входных данных.

## С какими файлами нужно работать

- [TaskDefinitionController.java](./start/src/main/java/com/taskmanager/controller/TaskDefinitionController.java)
- [TaskDefinitionService.java](./start/src/main/java/com/taskmanager/service/TaskDefinitionService.java)
- [TaskDefinitionRepository.java](./start/src/main/java/com/taskmanager/repository/TaskDefinitionRepository.java)
- [TaskDefinitionMapper.java](./start/src/main/java/com/taskmanager/mapper/TaskDefinitionMapper.java)
- [TaskDefinition.java](./start/src/main/java/com/taskmanager/entity/TaskDefinition.java)
- [TaskDefinitionStage01Tests.java](./start/src/test/java/com/taskmanager/TaskDefinitionStage01Tests.java)

## Что уже подготовлено

- entity;
- DTO;
- controller;
- repository;
- mapper;
- migration;
- test scaffold;
- базовая обработка ошибок.

## На что нужно обратить внимание

- controller должен остаться тонким;
- основная логика должна появиться в service;
- repository должен использоваться как слой доступа к данным;
- mapper должен отвечать за преобразование между DTO и entity;
- validation должна отрабатывать на входе.

## Что пока не нужно добавлять

На этом этапе не нужно:

- update/delete;
- pagination;
- сложную модель ошибок;
- вторую сущность;
- orchestration;
- паттерны следующего этапа.

## Практический результат

После завершения этапа система должна уметь:

- принимать новую задачу через API;
- сохранять ее в БД;
- возвращать список задач;
- возвращать задачу по id;
- корректно обрабатывать базовые ошибочные сценарии.

## Навигация

- Старт: [Stage 01](./README.md)
- Назад: [LECTURE.md](./LECTURE.md)
- Далее: [CHECKLIST.md](./CHECKLIST.md)

