# Урок 2. Паттерны, Принципы И Best Practices

## Наивная точка старта

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

---

## Почему этот вариант плох

1. Входной контракт неявный.
2. Выходной контракт неявный.
3. Controller смешивает HTTP и use case.
4. Плохо масштабируется.
5. Не учит нормальной архитектурной дисциплине.

---

## Улучшенная версия

```java
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskDefinitionController {

    private final TaskDefinitionService definitionService;

    @PostMapping
    public ResponseEntity<TaskDefinitionResponse> create(
            @Valid @RequestBody TaskDefinitionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(definitionService.create(request));
    }
}
```

---

## Какой шаблон программирования здесь используется

### Layered Architecture

Слои выглядят так:

- controller принимает HTTP
- service реализует create use case
- request DTO описывает вход
- response DTO описывает выход

Это помогает не смешивать роли в одном классе.

---

## Какие принципы проектирования здесь видны

### SRP

- controller отвечает за HTTP
- service отвечает за use case
- request DTO отвечает за входной контракт
- response DTO отвечает за выходной контракт

### Separation of Concerns

HTTP-заботы, структура входных данных и бизнес-действие разделены.

### Explicit Contracts

DTO делает API-контракты явными.

### Low Coupling

Controller не знает деталей создания сущности, а делегирует их service-слою.

---

## Какие best practices студент должен вынести

1. Для `POST` endpoint-а использовать request DTO, а не `Map`.
2. Не возвращать сырые структуры, когда можно вернуть response DTO.
3. Выставлять `201 Created` для create endpoint-а.
4. Не смешивать transport layer и application logic.
5. Делать controller тонким.
---

<!-- COURSE_NAV_START -->
## Навигация

- [К Началу Урока](./START_HERE.md)
- [Предыдущий Документ](./refactoring-from-naive-to-professional.md)
- [Следующий Документ](./comparison.md)
<!-- COURSE_NAV_END -->
