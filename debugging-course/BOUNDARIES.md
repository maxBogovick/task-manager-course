# Границы Курса

## Входит

- чтение кода в `src/main/java`;
- чтение тестов в `src/test/java`;
- диагностика REST flow;
- диагностика validation flow;
- диагностика execution lifecycle;
- асинхронное выполнение, cancellation, timeout, retry;
- Spring transactions там, где они влияют на баг;
- Flyway migrations и persisted state;
- добавление нового executor type через существующий plugin mechanism.

## Не входит

- полное обучение Java с нуля;
- полное обучение Spring Boot с нуля;
- переписывание на другую архитектуру;
- микросервисы;
- Kubernetes;
- authentication/authorization;
- масштабный UI;
- performance tuning без конкретного дефекта;
- замена PostgreSQL;
- замена JUnit/AssertJ/TestRestTemplate.

## Технические границы

Основная рабочая зона курса:

- `src/main/java/com/taskmanager/controller`
- `src/main/java/com/taskmanager/service`
- `src/main/java/com/taskmanager/orchestrator`
- `src/main/java/com/taskmanager/executor`
- `src/main/java/com/taskmanager/entity`
- `src/main/java/com/taskmanager/repository`
- `src/main/resources/db/migration`
- `src/test/java/com/taskmanager/TaskManagerApplicationTests.java`

Студент может читать весь репозиторий, но изменения должны быть ограничены причиной дефекта.

## Definition of Done

Lab готов, если:

- симптом воспроизведен;
- причина названа конкретно;
- есть тест или проверка;
- фикс проходит `mvn test` или конкретный тест;
- студент может объяснить, почему исправление не ломает соседние сценарии.

