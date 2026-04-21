# Практика К Уроку 3

## Цель практики

Самостоятельно реализовать:

1. `GET /api/tasks/{id}`
2. `GET /api/tasks`
3. `GET /api/tasks?type=SHELL_COMMAND`

---

## Что нужно сделать

1. Сделать метод получения одной задачи по id.
2. Сделать метод получения списка задач.
3. Добавить query param `type`.
4. Вернуть типизированный response, а не `Map`.
5. Понять, где и зачем появляется пагинация.

---

## Проверочные вызовы

```bash
curl http://localhost:8080/api/tasks/1
```

```bash
curl http://localhost:8080/api/tasks
```

```bash
curl "http://localhost:8080/api/tasks?type=SHELL_COMMAND"
```

---

## Обязательные вопросы после практики

1. Почему `id` должен идти в path, а не в query param?
2. Почему фильтр `type` лучше передавать через query param?
3. Почему список задач нельзя проектировать так же, как один объект?
4. Зачем API нужна пагинация?
---

<!-- COURSE_NAV_START -->
## Навигация

- [К Началу Урока](./START_HERE.md)
- [Предыдущий Документ](./annotation-reference.md)
- [Следующий Документ](./mistakes.md)
<!-- COURSE_NAV_END -->
