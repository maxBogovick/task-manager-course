# Solution Notes Stage 01

## Что реализовано

- `TaskDefinitionService` заполняет основной CRUD-срез этапа;
- `create` сохраняет сущность и возвращает DTO;
- `getById` выбрасывает `TaskNotFoundException`, если записи нет;
- `getAll` возвращает список всех задач;
- tests покрывают happy path и базовые ошибки.

## Почему решение простое

Это решение специально не вводит:

- полный CRUD;
- pagination;
- вторую сущность;
- более сложную error model;
- отдельные policy objects или паттерны следующего этапа.

## Зачем это важно

Эталонное решение должно закрывать ровно Stage 01 и не забирать на себя логику следующих этапов.

## Навигация

- Старт: [Stage 01](./README.md)
- Назад: [CHECKLIST.md](./CHECKLIST.md)
- Далее: [Stage 02](../stage-02/README.md)
