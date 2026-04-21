# Урок 3. Пошаговая Сборка Чтения Ресурсов

## Что мы строим

В этом уроке строим два сценария:

1. `GET /api/tasks/{id}`
2. `GET /api/tasks`

И третий как развитие второго:

3. `GET /api/tasks?type=SHELL_COMMAND`

---

## Шаг 1. Делаем endpoint для одного ресурса

```java
@GetMapping("/{id}")
public ResponseEntity<TaskDefinitionResponse> getById(@PathVariable Long id) {
    return ResponseEntity.ok(definitionService.getById(id));
}
```

### Что здесь важно

- `{id}` в path становится параметром метода
- `@PathVariable` связывает URL и Java-параметр
- controller не ищет задачу сам, а делегирует это service

---

## Шаг 2. Делаем endpoint для списка

```java
@GetMapping
public ResponseEntity<PagedResponse<TaskDefinitionResponse>> getAll(Pageable pageable) {
    return ResponseEntity.ok(definitionService.getAll(pageable));
}
```

### Что здесь важно

- route без `/{id}` означает коллекцию ресурсов
- ответ — это уже не один объект, а список
- появляется идея страницы данных

---

## Шаг 3. Добавляем фильтрацию через query param

```java
@GetMapping
public ResponseEntity<PagedResponse<TaskDefinitionResponse>> getAll(
        @RequestParam(required = false) String type,
        Pageable pageable) {

    if (type != null && !type.isBlank()) {
        return ResponseEntity.ok(definitionService.getByType(type, pageable));
    }
    return ResponseEntity.ok(definitionService.getAll(pageable));
}
```

### Что здесь важно

- `type` берется из query string
- это не часть идентификатора ресурса
- это фильтр

---

## Шаг 4. Добавляем дефолты пагинации

В реальном проекте:

```java
@PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
Pageable pageable
```

### Зачем это нужно

Чтобы API не отдавал бесконтрольно слишком много данных и имел предсказуемое поведение по умолчанию.

---

## Шаг 5. Проверяем вызовы

### Получить одну задачу

```bash
curl http://localhost:8080/api/tasks/1
```

### Получить список

```bash
curl http://localhost:8080/api/tasks
```

### Получить список только одного типа

```bash
curl "http://localhost:8080/api/tasks?type=SHELL_COMMAND"
```

---

## Что делает Spring за нас

1. парсит path variable
2. парсит query params
3. маппит их в параметры Java-метода
4. вызывает нужный controller-метод
5. превращает результат в JSON

---

## Что нужно вынести из этого урока

`GET by id` и `GET list` — это похожие, но не одинаковые сценарии.

У них:

- разные URL
- разные входные параметры
- разные формы ответа

Это нужно увидеть и зафиксировать как можно раньше.
---

<!-- COURSE_NAV_START -->
## Навигация

- [К Началу Урока](./START_HERE.md)
- [Предыдущий Документ](./README.md)
- [Следующий Документ](./refactoring-from-naive-to-professional.md)
<!-- COURSE_NAV_END -->
