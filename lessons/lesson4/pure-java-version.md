# Урок 4 Без Фреймворка: Update И Delete На Чистой Java

## Зачем нужен этот файл

Он показывает, как те же два сценария выглядели бы без Spring:

- `PUT /api/tasks/{id}`
- `DELETE /api/tasks/{id}`

---

## Что пришлось бы делать вручную

Для `PUT`:

- разбирать URL
- вытаскивать id
- читать request body
- парсить JSON
- создавать request object вручную
- собирать response JSON вручную

Для `DELETE`:

- разбирать URL
- вытаскивать id
- вручную отправлять `204 No Content`

---

## Главный вывод

Spring автоматизирует:

- route matching
- path parsing
- body mapping
- response serialization
- status handling

Поэтому Spring-версия чище и лучше подходит для реального backend-кода.
---

<!-- COURSE_NAV_START -->
## Навигация

- [К Началу Урока](./START_HERE.md)
- [Предыдущий Документ](./comparison.md)
- [Следующий Документ](./annotation-reference.md)
<!-- COURSE_NAV_END -->
