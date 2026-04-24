# Lab 02 - Debug Config Validation

## Цель

Научиться искать дефект в validation flow.

## Симптом

API должен отклонять task definition с `taskType = SHELL_COMMAND` и пустым config `{}`.

## Файлы для чтения

- `TaskDefinitionService`
- `ConfigValidator`
- `TaskExecutorRegistry`
- `ShellCommandExecutor`
- `ShellCommandConfig`
- `GlobalExceptionHandler`
- `TaskManagerApplicationTests`

## Задание

1. Найди тест, который описывает этот симптом.
2. Пройди цепочку validation.
3. Объясни, как `ConfigValidator` узнает config class для task type.
4. Определи, где должен возникнуть `IllegalArgumentException`.
5. Проверь, кто превращает exception в HTTP 400.
6. Напиши, какой минимальный фикс понадобился бы, если пустой config начал приниматься.

## Ограничения

- не добавлять hardcoded mapping в `ConfigValidator`;
- не менять controller ради validation;
- не менять JSON вручную строковыми проверками.

## Подсказки

### Уровень 1

Ищи тест `shouldRejectInvalidConfig`.

### Уровень 2

Typed config class приходит из executor registry.

### Уровень 3

Если validation не срабатывает, проверь annotations внутри config record и `getConfigClass()` у executor.

## Эталонное рассуждение

`TaskDefinitionService.create` вызывает `configValidator.validate(request.taskType(), request.config())`. Валидатор берет config class через `TaskExecutorRegistry.getConfigClass`. Для `SHELL_COMMAND` executor должен вернуть `ShellCommandConfig.class`. Jackson парсит JSON в record, затем Jakarta Validator проверяет annotations. Пустой `{}` должен нарушить constraint на command. `GlobalExceptionHandler` должен вернуть BAD_REQUEST.

Минимальный фикс при регрессии: восстановить constraint в `ShellCommandConfig` или вернуть config class из `ShellCommandExecutor`, в зависимости от причины. Добавлять `if (taskType.equals("SHELL_COMMAND"))` в validator нельзя, потому что это ломает plugin design.

## Проверка

```bash
mvn test -Dtest="TaskManagerApplicationTests#shouldRejectInvalidConfig"
```

