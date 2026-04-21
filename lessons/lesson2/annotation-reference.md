# Урок 2. Справочник По Аннотациям И Понятиям

## `@PostMapping`

Связывает HTTP `POST` запрос с методом controller.

В этом уроке:

```java
@PostMapping
public ResponseEntity<TaskDefinitionResponse> create(...) { ... }
```

---

## `@RequestBody`

Говорит Spring:

“Возьми JSON из тела HTTP-запроса и преобразуй его в Java-объект.”

Без этой аннотации параметр метода не будет интерпретирован как request body в нужном смысле.

---

## `@Valid`

Запускает Bean Validation для объекта запроса.

В этом уроке это ранняя точка подключения валидации.

---

## `HttpStatus.CREATED`

Это `201 Created`.

Правильный статус для успешного создания нового ресурса.

---

## Request DTO

Отдельный объект, который описывает входной контракт API.

В этом уроке:

[TaskDefinitionRequest.java](../../src/main/java/com/taskmanager/dto/TaskDefinitionRequest.java)

---

## Response DTO

Отдельный объект, который описывает выходной контракт API.

В этом уроке:

[TaskDefinitionResponse.java](../../src/main/java/com/taskmanager/dto/TaskDefinitionResponse.java)
---

<!-- COURSE_NAV_START -->
## Навигация

- [К Началу Урока](./START_HERE.md)
- [Предыдущий Документ](./pure-java-version.md)
- [Следующий Документ](./practice.md)
<!-- COURSE_NAV_END -->
