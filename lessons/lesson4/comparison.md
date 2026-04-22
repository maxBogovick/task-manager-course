# Урок 4. Сравнение Подходов

## Задача

Сделать:

- `PUT /api/tasks/{id}`
- `DELETE /api/tasks/{id}`

---

## Новичковый подход

- update через `Map<String, Object>`
- delete возвращает строку `"Deleted task ..."`
- логика прямо в controller

Плюс:

- быстро

Минусы:

- слабые контракты
- плохой HTTP-semantic результат
- лишняя логика в controller

---

## Профессиональный Spring подход

- update через request DTO и response DTO
- delete через `204 No Content`
- controller делегирует в service

Плюсы:

- явный контракт
- чище HTTP-поведение
- лучше масштабируется

---

## Чистая Java версия

Подробно:

[pure-java-version.md](./pure-java-version.md)

Показывает, сколько ручной web-обвязки пришлось бы писать без Spring.
---

<!-- COURSE_NAV_START -->
## Навигация

- [К Началу Урока](./START_HERE.md)
- [Предыдущий Документ](./patterns-principles-best-practices.md)
- [Следующий Документ](./pure-java-version.md)
<!-- COURSE_NAV_END -->
