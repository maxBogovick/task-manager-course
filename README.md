# Task Manager — Движок Оркестрации Задач

Система оркестрации задач на **Java 21**, **Spring Boot 3.4**, **Spring Data JPA** и **PostgreSQL**.  
Позволяет создавать, запускать, мониторить, перезапускать и отменять задачи произвольного типа.

## Архитектура

```
┌─────────────────────────────────────────────────────────┐
│                    REST API Layer                       │
│  TaskDefinitionController  │  TaskExecutionController   │
└────────────┬───────────────┴────────────┬───────────────┘
             │                            │
┌────────────▼────────────┐  ┌────────────▼───────────────┐
│  TaskDefinitionService  │  │     TaskOrchestrator       │
│  (CRUD определений)     │  │  • Запуск задач            │
│                         │  │  • Мониторинг таймаутов    │
│                         │  │  • Автоматические ретраи   │
│                         │  │  • Отмена выполнения       │
└─────────────────────────┘  └──────────┬─────────────────┘
                                        │
┌───────────────────────────────────────▼─────────────────┐
│              TaskExecutorRegistry (Plugin System)        │
├──────────────┬──────────────┬──────────────┬────────────┤
│ SHELL_COMMAND│ HTTP_REQUEST │  TELEGRAM    │   FILE     │
│              │              │  _MESSAGE    │ _ORGANIZER │
│ Запускает    │ HTTP-клиент  │  Bot API     │ Сортировка │
│ процессы ОС  │ GET/POST/... │  sendMessage │ по типу    │
└──────────────┴──────────────┴──────────────┴────────────┘
```

## Технологии

| Компонент    | Технология                          |
|-------------|--------------------------------------|
| Язык        | Java 21 (records, virtual threads)   |
| Фреймворк   | Spring Boot 3.4.4                    |
| ORM         | Spring Data JPA / Hibernate 6        |
| База данных  | PostgreSQL (H2 для тестов)           |
| Миграции     | Flyway                               |
| Валидация    | Jakarta Bean Validation              |
| Потоки       | Virtual Threads (Project Loom)       |

## Быстрый старт

```bash
# Создать БД
createdb taskmanager

# Запустить (postgres/postgres по умолчанию)
mvn spring-boot:run

# Или с кастомными креденшелами
DB_USERNAME=myuser DB_PASSWORD=mypass mvn spring-boot:run
```

API: `http://localhost:8080`

### Тесты
```bash
mvn test   # 18 тестов, ~11 сек
```

## API Endpoints

### Определения задач

| Метод   | URL                           | Описание                        |
|---------|-------------------------------|---------------------------------|
| `POST`  | `/api/tasks`                  | Создать определение задачи      |
| `GET`   | `/api/tasks`                  | Список (пагинация + фильтр)    |
| `GET`   | `/api/tasks/{id}`             | Получить по ID                  |
| `PUT`   | `/api/tasks/{id}`             | Обновить определение            |
| `DELETE`| `/api/tasks/{id}`             | Удалить (каскадно с историей)   |
| `PATCH` | `/api/tasks/{id}/enabled`     | Включить/выключить              |
| `GET`   | `/api/tasks/user/{userId}`    | Задачи пользователя             |
| `GET`   | `/api/tasks/types`            | Список доступных типов          |

### Запуск и управление

| Метод   | URL                           | Описание                        |
|---------|-------------------------------|---------------------------------|
| `POST`  | `/api/tasks/{id}/run`         | Запустить задачу                |
| `POST`  | `/api/executions/{id}/cancel` | Отменить выполнение             |

### История выполнений

| Метод   | URL                           | Описание                        |
|---------|-------------------------------|---------------------------------|
| `GET`   | `/api/executions`             | Все выполнения (+ ?status=)     |
| `GET`   | `/api/executions/{id}`        | Детали выполнения               |
| `GET`   | `/api/tasks/{id}/executions`  | История по задаче               |

### Мониторинг

| Метод   | URL                           | Описание                        |
|---------|-------------------------------|---------------------------------|
| `GET`   | `/api/orchestrator/status`    | Статус оркестратора             |

## Типы задач (Executors)

### `SHELL_COMMAND` — Выполнение команд ОС
```json
{
  "command": "find ~/Downloads -name '*.tmp' -delete",
  "workDir": "/home/user",
  "timeoutSeconds": 120
}
```

### `HTTP_REQUEST` — HTTP-запросы
```json
{
  "url": "https://api.weather.com/current?city=Moscow",
  "method": "GET",
  "headers": {"Authorization": "Bearer xxx"},
  "timeoutSeconds": 15
}
```

### `TELEGRAM_MESSAGE` — Telegram бот
```json
{
  "botToken": "123456:ABC-DEF",
  "chatId": "-1001234567890",
  "message": "Привет из оркестратора!",
  "parseMode": "HTML"
}
```

### `FILE_ORGANIZER` — Сортировка файлов
```json
{
  "sourceDir": "/Users/user/Downloads",
  "dryRun": false,
  "rules": {
    "psd": "Design",
    "sketch": "Design"
  }
}
```

## Примеры использования

### 1. Создать задачу — ежеминутно слать в Telegram погоду
```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Weather to Telegram",
    "description": "Каждую минуту шлёт погоду в чат",
    "taskType": "SHELL_COMMAND",
    "config": "{\"command\": \"curl -s wttr.in/Moscow?format=3\"}",
    "cronExpression": "0 * * * * *",
    "enabled": true,
    "maxRetries": 2,
    "retryDelaySeconds": 10,
    "timeoutSeconds": 30,
    "userId": 1
  }'
```

### 2. Запустить вручную
```bash
curl -X POST http://localhost:8080/api/tasks/1/run
```

### 3. Посмотреть результат
```bash
curl http://localhost:8080/api/executions/1
```

### 4. Отменить зависшую задачу
```bash
curl -X POST http://localhost:8080/api/executions/1/cancel
```

### 5. Упорядочить папку загрузки
```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Organize Downloads",
    "taskType": "FILE_ORGANIZER",
    "config": "{\"sourceDir\": \"/Users/user/Downloads\", \"dryRun\": true}",
    "userId": 1
  }'
```

## Оркестратор

### Жизненный цикл выполнения
```
PENDING → RUNNING → COMPLETED
                  → FAILED → (авто-ретрай) → RUNNING → ...
                  → TIMED_OUT
                  → CANCELLED
```

### Фичи
- **Virtual Threads** — задачи исполняются на виртуальных потоках (Project Loom)
- **Таймауты** — мониторинг каждые 15 сек, принудительная остановка
- **Автоматические ретраи** — проверка каждые 30 сек, с учётом `retryDelaySeconds`
- **Дедупликация** — не запускает задачу, если она уже в RUNNING
- **Cooperative cancellation** — AtomicBoolean для плавной отмены
- **Cron-расписание** — поддержка Spring CronExpression

## Структура проекта

```
src/main/java/com/taskmanager/
├── TaskManagerApplication.java
├── config/
│   └── SchedulingConfig.java
├── controller/
│   ├── TaskDefinitionController.java
│   └── TaskExecutionController.java
├── dto/
│   ├── TaskDefinitionRequest.java
│   ├── TaskDefinitionResponse.java
│   ├── TaskExecutionResponse.java
│   └── PagedResponse.java
├── entity/
│   ├── TaskDefinition.java
│   ├── TaskExecution.java
│   └── ExecutionStatus.java
├── exception/
│   ├── ErrorResponse.java
│   ├── GlobalExceptionHandler.java
│   └── TaskNotFoundException.java
├── executor/                          ← Plugin System
│   ├── TaskExecutor.java              ← Интерфейс
│   ├── ExecutionContext.java
│   ├── ExecutionResult.java
│   ├── TaskExecutorRegistry.java      ← Auto-discovery
│   └── impl/
│       ├── ShellCommandExecutor.java
│       ├── HttpRequestExecutor.java
│       ├── TelegramMessageExecutor.java
│       └── FileOrganizerExecutor.java
├── mapper/
│   └── TaskMapper.java
├── orchestrator/                      ← Мозг системы
│   ├── TaskOrchestrator.java          ← Запуск, мониторинг, ретраи
│   └── TaskSchedulerService.java      ← Cron-планировщик
├── repository/
│   ├── TaskDefinitionRepository.java
│   └── TaskExecutionRepository.java
└── service/
    ├── TaskDefinitionService.java
    └── TaskExecutionService.java
```

## Добавление нового типа задачи

Просто создай класс, реализующий `TaskExecutor`:

```java
@Component
public class MyCustomExecutor implements TaskExecutor {
    @Override
    public String getType() { return "MY_CUSTOM_TYPE"; }

    @Override
    public ExecutionResult execute(ExecutionContext ctx) {
        String param = (String) ctx.config().get("myParam");
        // ... логика ...
        return ExecutionResult.ok("Done: " + param);
    }
}
```

Он автоматически зарегистрируется в реестре и станет доступен через API.
