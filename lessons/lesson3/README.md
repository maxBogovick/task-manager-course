# Урок 3. Чтение Данных: `@PathVariable`, `@RequestParam` И Список Ресурсов

## Навигация По Уроку

Начинай отсюда:

[START_HERE.md](./START_HERE.md)

---

## Зачем нужен этот урок

После первого `GET` endpoint-а и первого `POST` endpoint-а студенту нужен следующий естественный шаг:

научиться правильно читать данные из API.

В реальном backend почти всегда есть как минимум два сценария чтения:

1. получить один ресурс по id
2. получить список ресурсов

В проекте `TaskManager` это выражено так:

- `GET /api/tasks/{id}`
- `GET /api/tasks`

А также:

- `GET /api/tasks?type=SHELL_COMMAND`

---

## Главный итог урока

К концу урока ты должен уметь:

1. объяснить, что делает `@PathVariable`
2. объяснить, что делает `@RequestParam`
3. показать разницу между URL path и query parameters
4. объяснить, чем чтение одного ресурса отличается от чтения списка
5. объяснить, зачем нужна пагинация
6. написать controller-методы для этих сценариев в профессиональном стиле
7. показать наивную реализацию и объяснить, почему она плоха
8. объяснить, какие паттерны, принципы и best practices здесь используются

---

## Конечная цель урока

Мы хотим, чтобы к концу урока студент понимал и мог реализовать:

### 1. Получение одной задачи по id

`GET /api/tasks/1`

### 2. Получение списка задач

`GET /api/tasks`

### 3. Получение задач с фильтром по типу

`GET /api/tasks?type=SHELL_COMMAND`

---

## Полный flow урока

### Сценарий 1. Получение одной задачи

```text
GET /api/tasks/{id}
        ->
controller
        ->
@PathVariable id
        ->
service.getById(id)
        ->
TaskDefinitionResponse
        ->
JSON
```

### Сценарий 2. Получение списка задач

```text
GET /api/tasks?type=...
        ->
controller
        ->
@RequestParam type
        ->
service.getAll(...) или service.getByType(...)
        ->
PagedResponse<TaskDefinitionResponse>
        ->
JSON
```

---

## Разбираемые файлы проекта

1. [TaskDefinitionController.java](../../src/main/java/com/taskmanager/controller/TaskDefinitionController.java)
2. [TaskDefinitionResponse.java](../../src/main/java/com/taskmanager/dto/TaskDefinitionResponse.java)
3. [PagedResponse.java](../../src/main/java/com/taskmanager/dto/PagedResponse.java)

---

## Целевой код урока

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

## Что нового появилось в этом уроке

### `@PathVariable`

Позволяет взять значение прямо из URL path.

Например:

`/api/tasks/42`

значение `42` станет параметром метода.

### `@RequestParam`

Позволяет взять значение из query parameters.

Например:

`/api/tasks?type=SHELL_COMMAND`

значение `SHELL_COMMAND` попадет в параметр `type`.

---

## Что такое path variable простыми словами

Path variable — это часть URL, которая идентифицирует конкретный ресурс.

Пример:

`GET /api/tasks/10`

Здесь `10` — это id конкретной задачи.

Это не “дополнительная настройка запроса”.
Это часть адреса ресурса.

---

## Что такое query parameter простыми словами

Query parameter — это дополнительный параметр запроса.

Пример:

`GET /api/tasks?type=SHELL_COMMAND`

Здесь `type` не определяет одну конкретную задачу, а фильтрует набор данных.

Это очень важное различие:

- path обычно указывает на конкретный ресурс
- query params обычно уточняют выборку, фильтрацию, сортировку, пагинацию

---

## Почему в этом уроке появляется пагинация

Даже если пока не разбирать ее глубоко, студент должен рано увидеть важную идею:

списки ресурсов нельзя бесконтрольно отдавать без ограничений.

Если данных станет много, нужны:

- page
- size
- сортировка

В проекте это выражено через `Pageable` и `PagedResponse`.

---

## Наивная реализация

Новичок может написать так:

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

Такой код может работать, но как учебный эталон он слабый.

---

## Почему наивная реализация плоха

1. Контракт ответа неявный.
2. Controller делает слишком много.
3. Нет нормальной модели списка.
4. Не видна идея пагинации.
5. Фильтрация смешана с HTTP-слоем.

---

## Профессиональная реализация

Профессиональный подход в этом уроке такой:

- controller принимает path/query параметры
- controller делегирует в service
- response DTO описывает один ресурс
- отдельная модель описывает страницу списка

То есть снова:

```text
controller -> service -> DTO/paged DTO -> JSON
```

---

## Какие идеи здесь важно сначала увидеть

Сначала не термин, а смысл:

Controller остается HTTP-слоем, а не становится местом хранения логики выборки.

Только после этого можно сказать, что здесь снова сохраняется:

### `Layered Architecture`

### Один класс не должен брать на себя слишком много

- controller отвечает за web-вход
- service отвечает за use case чтения
- DTO отвечает за контракт ответа

Только после этого можно назвать идею:

`SRP`

### Разные заботы не должны смешиваться

Path params, query params, логика выборки и формат ответа не смешиваются в одну кучу.

Только после этого можно назвать идею:

`Separation of Concerns`

### Контракт API лучше делать явным

Список лучше возвращать не как `List<Map<String, Object>>`, а как отдельную типизированную модель.

---

## Лучшие практики урока

1. Использовать `@PathVariable` для идентификатора ресурса.
2. Использовать `@RequestParam` для фильтрации.
3. Разделять получение одного ресурса и списка ресурсов.
4. Делать список типизированным и пригодным для пагинации.
5. Не собирать ответы вручную через `Map`.

---

## Что ты должен уметь после урока

1. Объяснить разницу между `/api/tasks/1` и `/api/tasks?type=SHELL_COMMAND`.
2. Объяснить, когда нужен `@PathVariable`, а когда `@RequestParam`.
3. Объяснить, зачем список задач должен иметь отдельную модель ответа.
4. Объяснить, зачем нужна пагинация.
5. Написать профессиональный `GET by id` и `GET list` endpoint.
---

<!-- COURSE_NAV_START -->
## Навигация

- [К Началу Урока](./START_HERE.md)
- [Предыдущий Документ](./START_HERE.md)
- [Следующий Документ](./step-by-step-build.md)
<!-- COURSE_NAV_END -->
