# Урок 4. Рефакторинг: От Наивного Update/Delete К Профессиональному

## Наивная версия

```java
@RestController
public class TaskController {

    @PutMapping("/api/tasks/{id}")
    public Map<String, Object> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("name", body.get("name"));
        result.put("taskType", body.get("taskType"));
        return result;
    }

    @DeleteMapping("/api/tasks/{id}")
    public String delete(@PathVariable Long id) {
        return "Deleted task " + id;
    }
}
```

---

## Проблема 1. Update работает с неявным контрактом

`Map<String, Object>` не показывает, какие поля реально ожидаются.

### Улучшаем

Используем `TaskDefinitionRequest`.

---

## Проблема 2. Ответ update тоже неявный

Ответ через `Map` плохо документирует API.

### Улучшаем

Используем `TaskDefinitionResponse`.

---

## Проблема 3. Delete возвращает строку вместо нормального HTTP-ответа

Строка `"Deleted task 1"` — это слабый способ выразить результат удаления.

### Улучшаем

Возвращаем:

```java
ResponseEntity.noContent().build()
```

Так API говорит точнее:

операция успешна, но body не нужен.

---

## Проблема 4. Controller делает лишнее

Он не должен сам реализовывать update/delete use case.

### Улучшаем

Выносим use case в service и оставляем controller тонким.

---

## Профессиональная версия

```java
@PutMapping("/{id}")
public ResponseEntity<TaskDefinitionResponse> update(
        @PathVariable Long id,
        @Valid @RequestBody TaskDefinitionRequest request) {
    return ResponseEntity.ok(definitionService.update(id, request));
}
```

```java
@DeleteMapping("/{id}")
public ResponseEntity<Void> delete(@PathVariable Long id) {
    definitionService.delete(id);
    return ResponseEntity.noContent().build();
}
```

---

## Что улучшилось

1. Update получил явный входной контракт.
2. Update получил явный выходной контракт.
3. Delete стал выражать результат через нормальный HTTP status.
4. Controller перестал быть перегруженным.
5. CRUD API стало ближе к реальной production-форме.
---

<!-- COURSE_NAV_START -->
## Навигация

- [К Началу Урока](./START_HERE.md)
- [Предыдущий Документ](./step-by-step-build.md)
- [Следующий Документ](./patterns-principles-best-practices.md)
<!-- COURSE_NAV_END -->
