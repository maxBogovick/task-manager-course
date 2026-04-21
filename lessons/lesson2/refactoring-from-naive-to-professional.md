# Урок 2. Рефакторинг: От Наивного POST Endpoint-а К Профессиональному

## Исходная наивная версия

```java
@RestController
public class TaskController {

    @PostMapping("/api/tasks")
    public Map<String, Object> create(@RequestBody Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", 1L);
        response.put("name", body.get("name"));
        response.put("taskType", body.get("taskType"));
        response.put("enabled", true);
        return response;
    }
}
```

Этот код может работать.

Но теперь надо понять, почему это слабое решение.

---

## Проблема 1. Неявный входной контракт

`Map<String, Object>` не говорит ясно:

- какие поля обязательны
- какие типы ожидаются
- как выглядит request contract

### Улучшаем

Выделяем request DTO:

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

---

## Проблема 2. Неявный выходной контракт

Ответ тоже собран через `Map`.

### Улучшаем

Выделяем response DTO:

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

Теперь контракт API стал явным.

---

## Проблема 3. Controller делает слишком много

Он принимает HTTP и сам решает create use case.

### Улучшаем

Выносим create use case в service:

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

И controller становится тоньше:

```java
@RestController
@RequestMapping("/api/tasks")
public class TaskDefinitionController {

    private final TaskDefinitionService definitionService;

    public TaskDefinitionController(TaskDefinitionService definitionService) {
        this.definitionService = definitionService;
    }

    @PostMapping
    public ResponseEntity<TaskDefinitionResponse> create(@RequestBody TaskDefinitionRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(definitionService.create(request));
    }
}
```

---

## Проблема 4. Контроль HTTP-статуса должен быть явным

Если мы делаем create endpoint, полезно сразу показать правильный HTTP status.

### Улучшаем

Возвращаем:

```java
ResponseEntity.status(HttpStatus.CREATED)
```

### Что это значит

Сервер не просто “успешно отдал объект”, а сообщает:

“ресурс был создан”.

---

## Проблема 5. Входной объект должен быть подготовлен к валидации

Следующий шаг взросления endpoint-а:

```java
public ResponseEntity<TaskDefinitionResponse> create(
        @Valid @RequestBody TaskDefinitionRequest request) {
    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(definitionService.create(request));
}
```

Даже если тему валидации мы разбираем позже, студент уже видит, где она подключается и зачем она рядом с request DTO.

---

## Финальная версия

```java
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskDefinitionController {

    private final TaskDefinitionService definitionService;

    @PostMapping
    public ResponseEntity<TaskDefinitionResponse> create(
            @Valid @RequestBody TaskDefinitionRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(definitionService.create(request));
    }
}
```

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

---

## Что улучшилось после рефакторинга

1. Входной контракт стал явным.
2. Выходной контракт стал явным.
3. Controller стал тонким.
4. Use case вынесен в service.
5. HTTP semantics стали точнее за счет `201 Created`.
6. Архитектура стала пригодной для роста.
---

<!-- COURSE_NAV_START -->
## Навигация

- [К Началу Урока](./START_HERE.md)
- [Предыдущий Документ](./step-by-step-build.md)
- [Следующий Документ](./patterns-principles-best-practices.md)
<!-- COURSE_NAV_END -->
