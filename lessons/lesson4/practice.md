# Практика К Уроку 4

## Цель практики

Самостоятельно реализовать:

1. `PUT /api/tasks/{id}`
2. `DELETE /api/tasks/{id}`

---

## Что нужно сделать

1. Сделать update endpoint с `@PutMapping`.
2. Передавать id через `@PathVariable`.
3. Принимать новые данные через `@RequestBody`.
4. Вернуть типизированный response DTO.
5. Сделать delete endpoint с `@DeleteMapping`.
6. Вернуть `204 No Content`.

---

## Проверочные вызовы

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

```bash
curl -X DELETE http://localhost:8080/api/tasks/1
```

---

## Обязательные вопросы после практики

1. Почему update — это не create?
2. Почему delete не должен возвращать случайную строку?
3. Почему `204 No Content` лучше подходит для delete?
4. Почему controller не должен сам обновлять объект вручную?
---

<!-- COURSE_NAV_START -->
## Навигация

- [К Началу Урока](./START_HERE.md)
- [Предыдущий Документ](./annotation-reference.md)
- [Следующий Документ](./mistakes.md)
<!-- COURSE_NAV_END -->
