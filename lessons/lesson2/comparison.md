# Урок 2. Сравнение Подходов

## Одна задача

Сделать:

`POST /api/tasks`

---

## Новичковый вариант

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

Плюс:

- быстро

Минусы:

- неявные контракты
- слабая типизация
- толстый controller

---

## Профессиональный Spring вариант

```java
@PostMapping
public ResponseEntity<TaskDefinitionResponse> create(
        @Valid @RequestBody TaskDefinitionRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(definitionService.create(request));
}
```

Плюсы:

- явные контракты
- нормальная архитектура
- правильный HTTP status

---

## Чистая Java версия

Подробно:

[pure-java-version.md](./pure-java-version.md)

Плюс:

- хорошо показывает, что делает Spring

Минус:

- много инфраструктурного шума
---

<!-- COURSE_NAV_START -->
## Навигация

- [К Началу Урока](./START_HERE.md)
- [Предыдущий Документ](./patterns-principles-best-practices.md)
- [Следующий Документ](./pure-java-version.md)
<!-- COURSE_NAV_END -->
