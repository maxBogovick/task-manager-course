# Урок 3. Сравнение Подходов

## Задача

Сделать:

- `GET /api/tasks/{id}`
- `GET /api/tasks`
- `GET /api/tasks?type=SHELL_COMMAND`

---

## Новичковый подход

- `Map<String, Object>` для одного ресурса
- `List<Map<String, Object>>` для списка
- логика прямо в controller

Плюс:

- быстро

Минусы:

- слабый контракт
- плохая расширяемость
- нет нормальной модели списка

---

## Профессиональный Spring подход

- `@PathVariable` для id
- `@RequestParam` для фильтра
- response DTO для одного ресурса
- paged response для списка

Плюсы:

- явный API контракт
- хороший рост
- читаемая архитектура

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
