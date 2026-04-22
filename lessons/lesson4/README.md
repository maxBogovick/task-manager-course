# Урок 4. Обновление И Удаление: `PUT`, `DELETE` И `204 No Content`

## Навигация По Уроку

Начинай отсюда:

[START_HERE.md](./START_HERE.md)

---

## Зачем нужен этот урок

После `POST` и `GET` backend еще не выглядит законченным.

Для нормального CRUD API нужно уметь:

- обновлять существующий ресурс
- удалять ресурс

В проекте `TaskManager` это выражено так:

- `PUT /api/tasks/{id}`
- `DELETE /api/tasks/{id}`

---

## Главный итог урока

К концу урока ты должен уметь:

1. объяснить, чем `PUT` отличается от `POST`
2. объяснить, зачем id передается через path
3. объяснить, что означает удаление ресурса через `DELETE`
4. объяснить, почему для удаления часто возвращают `204 No Content`
5. показать путь:
   `HTTP -> controller -> request DTO -> service -> response или пустой ответ`
6. написать update и delete endpoint в профессиональном стиле
7. показать наивную реализацию и объяснить, почему она плоха

---

## Конечная цель урока

Мы хотим, чтобы работали два endpoint-а:

### Обновление задачи

`PUT /api/tasks/{id}`

### Удаление задачи

`DELETE /api/tasks/{id}`

---

## Полный flow урока

### Обновление

```text
PUT /api/tasks/{id}
        ->
controller
        ->
@PathVariable id + @RequestBody request DTO
        ->
service.update(id, request)
        ->
response DTO
        ->
JSON
```

### Удаление

```text
DELETE /api/tasks/{id}
        ->
controller
        ->
@PathVariable id
        ->
service.delete(id)
        ->
HTTP 204 No Content
```

---

## Разбираемые файлы проекта

1. [TaskDefinitionController.java](../../src/main/java/com/taskmanager/controller/TaskDefinitionController.java)
2. [TaskDefinitionRequest.java](../../src/main/java/com/taskmanager/dto/TaskDefinitionRequest.java)
3. [TaskDefinitionResponse.java](../../src/main/java/com/taskmanager/dto/TaskDefinitionResponse.java)

---

## Целевой код урока

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

## Что такое `PUT` простыми словами

`POST` обычно используется, когда мы создаем новый ресурс.

`PUT` обычно используется, когда мы хотим обновить уже существующий ресурс.

В этом уроке клиент говорит серверу:

“Вот id уже существующей задачи и новые данные для нее. Обнови ее.”

---

## Что такое `DELETE` простыми словами

`DELETE` означает:

“Найди ресурс по id и удали его.”

Это отдельный use case.
Он не должен маскироваться под `POST` или `GET`.

---

## Почему id идет в path

Потому что мы работаем с конкретным ресурсом.

Когда ты пишешь:

`PUT /api/tasks/5`

ты говоришь:

“Измени задачу с id = 5”.

То же самое для удаления:

`DELETE /api/tasks/5`

---

## Наивная реализация

Новичок может написать так:

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

Такой код может работать, но как учебный эталон он слабый.

---

## Почему наивная реализация плоха

1. Update request не имеет явного входного контракта.
2. Update response не имеет явного выходного контракта.
3. Delete возвращает строку вместо нормального HTTP-semantic ответа.
4. Controller делает слишком много.
5. Студент не учится правильному CRUD-flow.

---

## Профессиональная реализация

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

### Что здесь хорошо

- id передается явно через path
- update принимает типизированный request DTO
- update возвращает типизированный response DTO
- delete не возвращает фальшивое сообщение, а использует нормальный HTTP-ответ
- controller тонкий и делегирует use case в service

---

## Что делает Spring за нас в этом уроке

Spring:

1. принимает `PUT` и `DELETE` запросы
2. находит нужный controller-метод
3. читает path variable
4. для `PUT` читает request body
5. преобразует JSON в request DTO
6. вызывает service
7. формирует response DTO или пустой ответ
8. отправляет HTTP status клиенту

---

## Что важно увидеть в структуре решения

Сначала важно увидеть простую идею:

- controller принимает HTTP
- service делает update/delete use case
- request DTO описывает вход обновления
- response DTO описывает результат обновления
- delete может завершаться без body

Только после этого можно говорить инженерными терминами.

---

## Какие инженерные идеи здесь важны

### Один класс не должен одновременно принимать HTTP и реализовывать всю логику

- controller отвечает за web-вход
- service отвечает за update/delete сценарий

Это потом можно связать с идеей `SRP`.

### Разные заботы не должны лежать в одной куче

- path id
- request body
- update logic
- delete logic
- response format

лучше разделять, а не смешивать.

Это потом можно связать с идеей `Separation of Concerns`.

### Контракт API лучше делать явным

Для update лучше использовать DTO, а не `Map`.

Для delete лучше возвращать корректный HTTP status, а не случайную строку.

---

## Лучшие практики этого урока

1. Использовать `PUT` для обновления существующего ресурса.
2. Использовать `DELETE` для удаления ресурса.
3. Для update принимать request DTO, а не `Map`.
4. Для delete возвращать `204 No Content`, когда body не нужен.
5. Делать controller тонким и делегирующим.

---

## Что ты должен уметь после урока

1. Объяснить разницу между create, update и delete.
2. Объяснить, почему update работает с id в path.
3. Объяснить, зачем delete может возвращать `204 No Content`.
4. Показать профессиональную форму update/delete endpoint-а.
5. Объяснить, почему строковый ответ `"Deleted"` хуже, чем корректный HTTP status.
---

<!-- COURSE_NAV_START -->
## Навигация

- [К Началу Урока](./START_HERE.md)
- [Предыдущий Документ](./START_HERE.md)
- [Следующий Документ](./step-by-step-build.md)
<!-- COURSE_NAV_END -->
