# Задание Stage 00

## Цель этапа

Освоить минимальный backend-каркас и уверенно ориентироваться в базовой структуре проекта.

## Что нужно сделать

1. Запустить проект.
2. Запустить тесты.
3. Посмотреть, как устроена точка входа приложения.
4. Посмотреть, как объявлен минимальный HTTP endpoint.
5. Понять, какие части проекта относятся к инфраструктуре, а какие появятся позже как доменная логика.

## На что обратить внимание в коде

- [TaskManagerApplication.java](./start/src/main/java/com/taskmanager/TaskManagerApplication.java)
- [HealthController.java](./start/src/main/java/com/taskmanager/controller/HealthController.java)
- [TaskManagerApplicationSmokeTest.java](./start/src/test/java/com/taskmanager/TaskManagerApplicationSmokeTest.java)

## Что важно понять

- `TaskManagerApplication` запускает приложение, но не содержит бизнес-логику;
- `HealthController` показывает минимальный HTTP-срез;
- smoke test подтверждает, что приложение поднимается;
- структура проекта уже задает форму дальнейшего роста.

## Что здесь пока не нужно

На этом этапе не нужно:

- добавлять доменные сущности;
- подключать JPA;
- писать сложные тесты;
- придумывать дополнительную архитектуру;
- расширять endpoint сверх минимальной проверки.

## Практический результат

После завершения этапа должна быть уверенность в том, как запускается проект и где будут появляться следующие слои системы.

## Навигация

- Старт: [Stage 00](./README.md)
- Назад: [LECTURE.md](./LECTURE.md)
- Далее: [CHECKLIST.md](./CHECKLIST.md)

