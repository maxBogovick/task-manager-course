# Практика К Уроку 2

## Цель практики

Самостоятельно построить `POST /api/tasks` и объяснить его flow.

---

## Что нужно сделать

1. Создать request DTO.
2. Создать response DTO.
3. Создать service с методом `create(...)`.
4. Создать controller с `@PostMapping`.
5. Принять JSON через `@RequestBody`.
6. Вернуть `201 Created`.

---

## Практический вызов

Проверь endpoint так:

```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
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
  }'
```

---

## Обязательные вопросы после практики

1. Почему request DTO лучше `Map<String, Object>`?
2. Почему response DTO не должен совпадать с request DTO автоматически?
3. Почему controller не должен сам собирать response?
4. Почему для создания ресурса нужен `201 Created`?
---

<!-- COURSE_NAV_START -->
## Навигация

- [К Началу Урока](./START_HERE.md)
- [Предыдущий Документ](./annotation-reference.md)
- [Следующий Документ](./mistakes.md)
<!-- COURSE_NAV_END -->
