# Урок 3. Паттерны, Принципы И Best Practices

## Наивная реализация

```java
@RestController
public class TaskController {

    @GetMapping("/api/tasks/{id}")
    public Map<String, Object> getById(@PathVariable Long id) { ... }

    @GetMapping("/api/tasks")
    public List<Map<String, Object>> getAll(@RequestParam(required = false) String type) { ... }
}
```

---

## Почему она плоха

1. Смешивает HTTP и use case.
2. Не имеет сильного контракта ответа.
3. Плохо готова к росту.
4. Не показывает правильную форму collection response.

---

## Улучшенный вариант

Использует:

- `TaskDefinitionResponse`
- `PagedResponse<TaskDefinitionResponse>`
- тонкий controller
- query param только как фильтр

---

## Какой шаблон здесь используется

### Layered Architecture

Снова:

- controller принимает web-вход
- service реализует сценарий чтения
- DTO описывает ответ

### Collection Response Pattern

Список данных возвращается не как сырой массив неструктурированных объектов, а как отдельная модель списка/страницы.

Это важный практический шаблон API-дизайна.

---

## Какие принципы видны

### SRP

Controller не должен знать, как строить данные.

### Separation of Concerns

Параметры URL, фильтрация и формат ответа разделены.

### Explicit Contracts

Один ресурс и список ресурсов имеют явные формы ответа.

---

## Best practices

1. `@PathVariable` использовать для идентификатора ресурса.
2. `@RequestParam` использовать для фильтрации и настройки выборки.
3. Для списка возвращать отдельную модель с метаданными.
4. Не смешивать `GET by id` и `GET list` как будто это один и тот же сценарий.
---

<!-- COURSE_NAV_START -->
## Навигация

- [К Началу Урока](./START_HERE.md)
- [Предыдущий Документ](./refactoring-from-naive-to-professional.md)
- [Следующий Документ](./comparison.md)
<!-- COURSE_NAV_END -->
