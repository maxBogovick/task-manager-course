# Урок 2. Пошаговая Сборка POST Endpoint-а

## Что мы строим

Наша цель:

сделать endpoint

`POST /api/tasks`

который принимает JSON и возвращает `201 Created` с response DTO.

---

## Полная цепочка

```text
POST /api/tasks
        ->
controller
        ->
request DTO
        ->
service
        ->
response DTO
        ->
HTTP 201 + JSON
```

---

## Шаг 1. Сначала фиксируем входной контракт

Создаем request DTO.

Например:

```java
public record TaskDefinitionRequest(
        String name,
        String description,
        String taskType,
        String config,
        String cronExpression,
        Boolean enabled,
        Integer maxRetries,
        Integer retryDelaySeconds,
        Integer timeoutSeconds,
        Long userId
) {
}
```

### Почему это важно

Мы явно говорим, какие данные клиент обязан или может прислать.

Это намного лучше, чем принимать `Map<String, Object>`.

---

## Шаг 2. Фиксируем выходной контракт

Теперь создаем response DTO.

```java
public record TaskDefinitionResponse(
        Long id,
        String name,
        String description,
        String taskType,
        String config,
        String cronExpression,
        Boolean enabled,
        Integer maxRetries,
        Integer retryDelaySeconds,
        Integer timeoutSeconds,
        Long userId
) {
}
```

### Почему это отдельный класс

Потому что вход и выход — это разные контракты.

У response появляется, например, `id`, которого не было у клиента до создания.

---

## Шаг 3. Делаем service

Сначала можно сделать учебную версию без БД:

```java
@Service
public class TaskDefinitionService {

    public TaskDefinitionResponse create(TaskDefinitionRequest request) {
        return new TaskDefinitionResponse(
                1L,
                request.name(),
                request.description(),
                request.taskType(),
                request.config(),
                request.cronExpression(),
                request.enabled(),
                request.maxRetries(),
                request.retryDelaySeconds(),
                request.timeoutSeconds(),
                request.userId()
        );
    }
}
```

### Почему так можно на учебном шаге

Потому что сейчас мы изучаем не БД, а форму create flow.

---

## Шаг 4. Делаем controller

```java
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskDefinitionController {

    private final TaskDefinitionService definitionService;

    @PostMapping
    public ResponseEntity<TaskDefinitionResponse> create(
            @RequestBody TaskDefinitionRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(definitionService.create(request));
    }
}
```

### Что здесь произошло

- endpoint стал `POST`
- route теперь `/api/tasks`
- JSON request body маппится в `TaskDefinitionRequest`
- controller делегирует use case в service
- ответ приходит как `TaskDefinitionResponse`

---

## Шаг 5. Добавляем `@Valid`

В реальном проекте request DTO содержит правила валидации.

Поэтому метод принимает данные так:

```java
public ResponseEntity<TaskDefinitionResponse> create(
        @Valid @RequestBody TaskDefinitionRequest request) {
    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(definitionService.create(request));
}
```

### Что это дает

Перед выполнением метода Spring проверит объект на ограничения.

Глубокую тему валидации будем разбирать отдельно, но место подключения уже нужно понимать сейчас.

---

## Шаг 6. Проверяем результат

Пример вызова:

```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "name": "List home directory",
    "description": "Runs echo hello world",
    "taskType": "SHELL_COMMAND",
    "config": "{\"command\": \"echo hello world\", \"workDir\": \"/tmp\"}",
    "cronExpression": null,
    "enabled": true,
    "maxRetries": 2,
    "retryDelaySeconds": 5,
    "timeoutSeconds": 60,
    "userId": 1
  }'
```

Ожидаемая идея:

- приходит `201 Created`
- в body приходит JSON с созданной задачей

---

## Шаг 7. Что делает Spring внутри

После запроса происходят такие шаги:

1. приходит HTTP POST запрос
2. Spring MVC видит route `/api/tasks`
3. выбирает метод `create(...)`
4. читает JSON из request body
5. превращает JSON в `TaskDefinitionRequest`
6. валидирует объект, если стоит `@Valid`
7. вызывает service
8. получает `TaskDefinitionResponse`
9. превращает его в JSON
10. отправляет клиенту `201 Created`

---

## Как это связано с реальным кодом проекта

В реальном проекте используется:

[TaskDefinitionController.java](../../src/main/java/com/taskmanager/controller/TaskDefinitionController.java)

и DTO:

- [TaskDefinitionRequest.java](../../src/main/java/com/taskmanager/dto/TaskDefinitionRequest.java)
- [TaskDefinitionResponse.java](../../src/main/java/com/taskmanager/dto/TaskDefinitionResponse.java)

То есть мы не выдумываем абстрактную архитектуру, а учимся на реальном коде проекта.
---

<!-- COURSE_NAV_START -->
## Навигация

- [К Началу Урока](./START_HERE.md)
- [Предыдущий Документ](./README.md)
- [Следующий Документ](./refactoring-from-naive-to-professional.md)
<!-- COURSE_NAV_END -->
