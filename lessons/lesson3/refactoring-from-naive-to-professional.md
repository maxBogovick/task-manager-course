# Урок 3. Рефакторинг: От Наивного Чтения К Профессиональному API

## Наивный вариант

```java
@RestController
public class TaskController {

    @GetMapping("/api/tasks/{id}")
    public Map<String, Object> getById(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("name", "Task " + id);
        result.put("taskType", "SHELL_COMMAND");
        return result;
    }

    @GetMapping("/api/tasks")
    public List<Map<String, Object>> getAll(@RequestParam(required = false) String type) {
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("id", 1L);
        item.put("name", "Task 1");
        item.put("taskType", type != null ? type : "SHELL_COMMAND");
        result.add(item);
        return result;
    }
}
```

---

## Проблема 1. Контракт ответа неявный

И один ресурс, и список ресурсов описаны через `Map`.

### Почему это плохо

- не видно структуру данных заранее
- легко ошибиться в ключах
- API трудно читать

### Улучшаем

Используем `TaskDefinitionResponse`.

---

## Проблема 2. Список ресурсов не имеет нормальной модели

`List<Map<String, Object>>` слишком бедная форма.

### Почему это плохо

- некуда положить метаданные страницы
- неудобно расширять
- сложно стандартизировать API

### Улучшаем

Используем `PagedResponse<TaskDefinitionResponse>`.

---

## Проблема 3. Controller делает лишнее

Он сам формирует тестовые данные вместо делегирования use case.

### Улучшаем

Controller должен только принимать:

- `id`
- `type`
- `pageable`

а затем вызывать service.

---

## Профессиональный вариант

```java
@GetMapping("/{id}")
public ResponseEntity<TaskDefinitionResponse> getById(@PathVariable Long id) {
    return ResponseEntity.ok(definitionService.getById(id));
}
```

```java
@GetMapping
public ResponseEntity<PagedResponse<TaskDefinitionResponse>> getAll(
        @RequestParam(required = false) String type,
        @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

    if (type != null && !type.isBlank()) {
        return ResponseEntity.ok(definitionService.getByType(type, pageable));
    }
    return ResponseEntity.ok(definitionService.getAll(pageable));
}
```

---

## Что улучшилось

1. Контракты ответа стали типизированными.
2. Список теперь подготовлен к пагинации.
3. Controller остался тонким.
4. Разница между `path` и `query` стала явной.
5. API стало ближе к реальному production style.
---

<!-- COURSE_NAV_START -->
## Навигация

- [К Началу Урока](./START_HERE.md)
- [Предыдущий Документ](./step-by-step-build.md)
- [Следующий Документ](./patterns-principles-best-practices.md)
<!-- COURSE_NAV_END -->
