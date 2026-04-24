# Lab 08 - Add Executor

## Цель

Научиться расширять систему через существующий plugin mechanism без изменения registry и validator.

## Задача

Добавить учебный executor type, например `ECHO_TEXT`, который возвращает текст из config.

## Файлы для чтения

- `TaskExecutor`
- `TaskExecutorRegistry`
- `ConfigValidator`
- существующие classes в `executor/impl`
- существующие config records в `executor/config`
- `TaskManagerApplicationTests#shouldListAvailableExecutorTypes`

## Задание

1. Создай config record для нового executor.
2. Создай `@Component` executor.
3. Реализуй `getType()`.
4. Реализуй `getConfigClass()`.
5. Реализуй `execute`.
6. Добавь тест на список типов.
7. Добавь тест на invalid config.
8. Добавь тест на успешный запуск.

## Ограничения

- не менять `TaskExecutorRegistry`;
- не менять `ConfigValidator`;
- не добавлять manual switch по task type;
- executor должен быть thread-safe.

## Подсказки

### Уровень 1

Скопируй форму, а не код, у `ShellCommandExecutor`.

### Уровень 2

Если type не появился в `/api/tasks/types`, проверь `@Component`.

### Уровень 3

Если invalid config принимается, проверь `getConfigClass()` и validation annotations.

## Эталонное рассуждение

Система задумана OCP-compliant: registry получает все `TaskExecutor` implementations через Spring DI. Поэтому новый executor должен добавиться без правок registry. Config validation тоже должна заработать через `getConfigClass()`. Любой switch в validator или controller будет нарушением цели lab.

## Проверка

```bash
mvn test
```

