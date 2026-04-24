# Lab 01 - Read Create Definition

## Цель

Научиться читать простой request flow без фикса бага.

## Сценарий

Пользователь отправляет `POST /api/tasks` и создает `TaskDefinition`.

## Файлы для чтения

- `TaskDefinitionController`
- `TaskDefinitionService`
- `TaskDefinitionRequest`
- `TaskMapper`
- `TaskDefinition`
- `TaskDefinitionRepository`
- `TaskManagerApplicationTests`

## Задание

1. Найди endpoint, который принимает `POST /api/tasks`.
2. Выпиши DTO, который приходит в controller.
3. Выпиши все validation steps до сохранения в БД.
4. Найди, где request превращается в entity.
5. Найди тест, который подтверждает создание.
6. Заполни `templates/debugging-report.md`, но без разделов "Причина" и "Фикс".

## Ограничения

- код не менять;
- не читать весь проект подряд;
- двигаться только по цепочке вызовов.

## Подсказки

### Уровень 1

Начни с controller package.

### Уровень 2

После controller ищи service method с названием `create`.

### Уровень 3

Проверь, что validation находится не только в DTO annotations.

## Эталонное рассуждение

Создание definition проходит через `TaskDefinitionController`, затем `TaskDefinitionService.create`. До сохранения проверяются cron expression и config через `ConfigValidator`. Маппинг request -> entity делает `TaskMapper`. Репозиторий сохраняет `TaskDefinition`. Тест `shouldCreateTaskDefinition` проверяет HTTP status, id и основные поля.

## Проверка

```bash
mvn test -Dtest="TaskManagerApplicationTests#shouldCreateTaskDefinition"
```

