# Урок 4. Паттерны, Принципы И Best Practices

## Наивная реализация

```java
@RestController
public class TaskController {

    @PutMapping("/api/tasks/{id}")
    public Map<String, Object> update(@PathVariable Long id, @RequestBody Map<String, Object> body) { ... }

    @DeleteMapping("/api/tasks/{id}")
    public String delete(@PathVariable Long id) { ... }
}
```

---

## Почему она плоха

1. Update не имеет сильного контракта.
2. Delete выражен строкой, а не HTTP-semantic ответом.
3. Controller берет на себя лишние роли.
4. Код плохо масштабируется.

---

## Что сначала нужно увидеть в коде

Сначала нужно увидеть не термин, а смысл:

- update лучше описывать через request и response DTO
- delete лучше выражать через корректный HTTP status
- controller должен делегировать use case

Только после этого можно вводить инженерные названия.

### Когда web-слой и use case разнесены

Это можно назвать:

`Layered Architecture`

### Когда один класс не должен делать все сразу

Это можно связать с идеей:

`SRP`

### Когда формат HTTP-ответа и прикладная логика не смешиваются

Это можно связать с идеей:

`Separation of Concerns`

---

## Какие рабочие практики студент должен вынести

1. Для update использовать DTO, а не `Map`.
2. Для delete использовать корректный HTTP status.
3. Не возвращать строковые “сообщения успеха”, если лучше подходит HTTP-semantic ответ.
4. Оставлять controller тонким.
5. Разделять create, update и delete как разные use case.
---

<!-- COURSE_NAV_START -->
## Навигация

- [К Началу Урока](./START_HERE.md)
- [Предыдущий Документ](./refactoring-from-naive-to-professional.md)
- [Следующий Документ](./comparison.md)
<!-- COURSE_NAV_END -->
