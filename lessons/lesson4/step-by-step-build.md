# Урок 4. Пошаговая Сборка Update И Delete Endpoint-ов

## Что мы строим

В этом уроке строим два endpoint-а:

1. `PUT /api/tasks/{id}`
2. `DELETE /api/tasks/{id}`

---

## Шаг 1. Делаем update endpoint

```java
@PutMapping("/{id}")
public ResponseEntity<TaskDefinitionResponse> update(
        @PathVariable Long id,
        @RequestBody TaskDefinitionRequest request) {
    return ResponseEntity.ok(definitionService.update(id, request));
}
```

### Что здесь важно

- id приходит из path
- новые данные приходят из request body
- controller ничего не обновляет сам
- service выполняет use case

---

## Шаг 2. Добавляем `@Valid`

В реальном проекте update выглядит так:

```java
@PutMapping("/{id}")
public ResponseEntity<TaskDefinitionResponse> update(
        @PathVariable Long id,
        @Valid @RequestBody TaskDefinitionRequest request) {
    return ResponseEntity.ok(definitionService.update(id, request));
}
```

Это значит:

request DTO проверяется перед передачей в business flow.

---

## Шаг 3. Делаем delete endpoint

```java
@DeleteMapping("/{id}")
public ResponseEntity<Void> delete(@PathVariable Long id) {
    definitionService.delete(id);
    return ResponseEntity.noContent().build();
}
```

### Что здесь важно

- удаление работает с конкретным id
- body в ответе не нужен
- `204 No Content` сообщает, что операция успешна, но возвращать нечего

---

## Шаг 4. Проверяем вызовы

### Обновить задачу

```bash
curl -X PUT http://localhost:8080/api/tasks/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated task name",
    "description": "Updated description",
    "taskType": "SHELL_COMMAND",
    "config": "{\"command\": \"echo updated\"}",
    "cronExpression": null,
    "enabled": true,
    "maxRetries": 3,
    "retryDelaySeconds": 10,
    "timeoutSeconds": 120,
    "userId": 1
  }'
```

### Удалить задачу

```bash
curl -X DELETE http://localhost:8080/api/tasks/1
```

---

## Что делает Spring за нас

1. разбирает path variable
2. для `PUT` читает request body
3. маппит JSON в DTO
4. вызывает нужный controller-метод
5. сериализует response DTO
6. для delete корректно отдает пустой response с нужным статусом

---

## Что нужно вынести из урока

Update и delete — это не “еще два метода”.

Это два отдельных use case:

- обновить существующий ресурс
- удалить существующий ресурс

И у каждого свой правильный HTTP-способ выражения.
---

<!-- COURSE_NAV_START -->
## Навигация

- [К Началу Урока](./START_HERE.md)
- [Предыдущий Документ](./README.md)
- [Следующий Документ](./refactoring-from-naive-to-professional.md)
<!-- COURSE_NAV_END -->
