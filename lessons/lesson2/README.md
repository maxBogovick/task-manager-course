# Урок 2. Первый POST Endpoint: Request Body, DTO И Создание Задачи

## Навигация По Уроку

Начинай отсюда:

[START_HERE.md](./START_HERE.md)

---

## Зачем нужен этот урок

В `lesson1` ты увидел первый рабочий `GET` endpoint и понял базовый flow:

```text
HTTP -> controller -> service -> DTO -> JSON
```

Теперь нужно сделать следующий шаг.

Любой реальный backend не только отдает данные, но и принимает их от клиента.

Поэтому в этом уроке мы впервые строим **create endpoint**:

`POST /api/tasks`

Это уже намного ближе к реальному CRUD API.

---

## Главный итог урока

К концу урока ты должен уметь:

1. объяснить, чем `POST` отличается от `GET`
2. объяснить, что такое request body
3. объяснить, зачем нужен `@RequestBody`
4. объяснить, зачем request DTO и response DTO разделяются
5. объяснить, зачем возвращать `201 Created`
6. показать полный путь запроса:
   `HTTP POST -> controller -> request DTO -> service -> response DTO -> JSON`
7. написать create endpoint в профессиональном стиле
8. показать, как выглядел бы такой код наивно и почему он плох
9. показать, как это выглядело бы на чистой Java без Spring

---

## Конечная цель урока

Финальная цель очень конкретная.

Мы хотим, чтобы работал endpoint:

`POST /api/tasks`

с JSON-запросом:

```json
{
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
}
```

и чтобы в ответ приходил `201 Created` и response DTO.

---

## Почему это правильный следующий шаг

Потому что после первого `GET` endpoint-а студент должен научиться:

- принимать данные от клиента
- переводить JSON в Java-объект
- отделять входной контракт от выходного
- понимать разницу между request DTO и response DTO
- понимать, как строится create use case

Без этого невозможно перейти к полноценному CRUD API.

---

## Полный flow этого урока

```text
Клиент отправляет POST /api/tasks с JSON body
        ->
Spring MVC находит controller-метод
        ->
Spring читает JSON из request body
        ->
Spring преобразует JSON в TaskDefinitionRequest
        ->
controller вызывает service.create(request)
        ->
service создает результат
        ->
service возвращает TaskDefinitionResponse
        ->
controller возвращает HTTP 201 Created
        ->
Spring превращает response DTO в JSON
        ->
клиент получает HTTP-response
```

---

## Разбираемые файлы проекта

1. [TaskDefinitionController.java](../../src/main/java/com/taskmanager/controller/TaskDefinitionController.java)
2. [TaskDefinitionRequest.java](../../src/main/java/com/taskmanager/dto/TaskDefinitionRequest.java)
3. [TaskDefinitionResponse.java](../../src/main/java/com/taskmanager/dto/TaskDefinitionResponse.java)

---

## Целевой код урока

Ключевой фрагмент:

```java
@PostMapping
public ResponseEntity<TaskDefinitionResponse> create(@Valid @RequestBody TaskDefinitionRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(definitionService.create(request));
}
```

Именно этот кусок мы и должны полностью понять.

---

## Что нового появилось в этом уроке

По сравнению с `lesson1` добавляются новые понятия:

- `POST`
- request body
- `@PostMapping`
- `@RequestBody`
- request DTO
- response DTO
- `HttpStatus.CREATED`
- create use case

---

## Что такое `POST` простыми словами

`GET` обычно используется, когда мы хотим получить данные.

`POST` обычно используется, когда мы хотим отправить данные на сервер для создания новой записи или запуска действия.

В этом уроке клиент говорит серверу:

“Вот описание новой задачи. Создай ее.”

---

## Что такое request body

Когда клиент отправляет `POST` запрос, данные обычно идут не в URL, а в теле запроса.

Например:

```json
{
  "name": "List home directory",
  "taskType": "SHELL_COMMAND"
}
```

Это и есть request body.

Backend должен:

- прочитать это тело
- распарсить JSON
- превратить его в Java-объект

В Spring это делается очень удобно через `@RequestBody`.

---

## Почему request DTO и response DTO нужно разделять

Наивная мысль новичка:

“Зачем два класса? Можно один.”

Но это плохая привычка.

### Request DTO отвечает на вопрос:

что клиент прислал серверу?

### Response DTO отвечает на вопрос:

что сервер вернул клиенту?

Это разные роли.

Даже если часть полей похожа, семантически это разные контракты.

---

## Наивная реализация

Новичок часто пишет так:

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

Но он плох как архитектурный эталон.

---

## Почему наивная реализация плоха

### 1. Нет явного входного контракта

`Map<String, Object>` не говорит ясно, какие данные реально ожидаются.

### 2. Нет явного выходного контракта

Ответ снова собирается через `Map`.

### 3. Controller делает слишком много

Он:

- принимает HTTP
- разбирает данные
- решает use case
- собирает ответ

### 4. Студент не учится нормальному CRUD-flow

А именно его и нужно начать понимать на этом этапе.

---

## Профессиональная реализация

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

### Что здесь хорошо

- route описан явно
- request body выражен отдельным DTO
- response выражен отдельным DTO
- controller тонкий
- use case вынесен в service
- HTTP status задан явно

---

## Какие аннотации здесь особенно важны

### `@PostMapping`

Связывает HTTP `POST` запрос с методом controller.

### `@RequestBody`

Говорит Spring:

“Возьми JSON из тела запроса и преобразуй его в Java-объект.”

### `@Valid`

Говорит Spring:

“Перед выполнением метода проверь объект на правила валидации.”

Глубокую валидацию мы будем разбирать отдельно, но уже сейчас важно видеть, где она подключается.

---

## Что делает Spring за нас в этом уроке

Spring:

1. принимает HTTP POST запрос
2. находит нужный controller-метод
3. читает request body
4. преобразует JSON в `TaskDefinitionRequest`
5. запускает валидацию
6. вызывает service
7. превращает `TaskDefinitionResponse` в JSON
8. отправляет клиенту `201 Created`

Это огромный объем ручной работы, который без фреймворка пришлось бы писать самому.

---

## Что важно увидеть в структуре этого решения

Сначала студент должен увидеть простую идею:

- web-слой принимает запрос
- service-слой обрабатывает use case
- request DTO описывает вход
- response DTO описывает выход

Только после этого можно сказать, что такой способ организации кода обычно называют:

### `Layered Architecture`

---

## Какие инженерные идеи здесь важны

### 1. Один класс не должен делать все сразу

- controller отвечает за HTTP-вход
- service отвечает за use case
- request DTO отвечает за форму входных данных
- response DTO отвечает за форму выходных данных

Только после этого можно назвать термин:

`SRP`

### 2. Разные заботы лучше разделять

HTTP, входной контракт, прикладная логика и выходной контракт разделены.

Только после этого можно назвать термин:

`Separation of Concerns`

### 3. Контракт API лучше делать явным

Вместо неявных `Map` у нас явные DTO-контракты.

Только после этого можно говорить про explicit contracts.

---

## Лучшие практики этого урока

1. Использовать request DTO вместо `Map<String, Object>`.
2. Использовать response DTO вместо возврата сущности или сырого `Map`.
3. Делать controller тонким.
4. Явно выставлять `201 Created` для успешного create endpoint-а.
5. Разделять входную и выходную модели.

---

## Что ты должен уметь после урока

1. Объяснить, как JSON из request body становится Java-объектом.
2. Объяснить, зачем нужен `@RequestBody`.
3. Объяснить, зачем request DTO и response DTO разделены.
4. Объяснить, почему `POST /api/tasks` должен возвращать `201 Created`.
5. Объяснить, почему controller не должен работать с `Map<String, Object>`.
6. Построить первый create endpoint в профессиональном стиле.
---

<!-- COURSE_NAV_START -->
## Навигация

- [К Началу Урока](./START_HERE.md)
- [Предыдущий Документ](./START_HERE.md)
- [Следующий Документ](./step-by-step-build.md)
<!-- COURSE_NAV_END -->
