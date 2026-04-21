# Lesson 2. Start Here

## Как пользоваться этим модулем

Этот урок нужно проходить по порядку.

Если ты уже прошел `lesson1`, здесь будет следующий естественный шаг:

теперь мы не просто читаем данные через `GET`, а создаем новый объект через `POST`.

Начинай именно с этого файла и переходи по ссылкам в указанной последовательности.

---

## Конечная цель урока

К концу урока ты должен уметь объяснить и собрать endpoint:

`POST /api/tasks`

который принимает JSON-запрос, попадает в controller, преобразуется в request DTO, передается в service и возвращает `201 Created` с response DTO.

Пример идеи запроса:

```json
{
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
}
```

Пример идеи ответа:

```json
{
  "id": 1,
  "name": "List home directory",
  "description": "Runs echo hello world",
  "taskType": "SHELL_COMMAND",
  "enabled": true
}
```

---

## Рекомендуемый порядок

1. [README.md](./README.md)
2. [step-by-step-build.md](./step-by-step-build.md)
3. [refactoring-from-naive-to-professional.md](./refactoring-from-naive-to-professional.md)
4. [patterns-principles-best-practices.md](./patterns-principles-best-practices.md)
5. [comparison.md](./comparison.md)
6. [pure-java-version.md](./pure-java-version.md)
7. [annotation-reference.md](./annotation-reference.md)
8. [practice.md](./practice.md)
9. [mistakes.md](./mistakes.md)
10. [glossary.md](./glossary.md)
11. [quiz.md](./quiz.md)
12. [self-check.md](./self-check.md)

---

## Что нового по сравнению с lesson1

В этом уроке появятся новые важные элементы:

- `POST` вместо `GET`
- request body
- `@PostMapping`
- `@RequestBody`
- request DTO
- response DTO
- `201 Created`
- первый настоящий create use case

---

## Что ты должен вынести из урока

Главная мысль:

backend не только отдает JSON по `GET`, но и умеет принимать структурированные данные от клиента, преобразовывать их в Java-объекты и обрабатывать через правильную архитектуру.
---

<!-- COURSE_NAV_START -->
## Навигация

- [К Началу Урока](./START_HERE.md)
- [Предыдущий Документ](./self-check.md)
- [Следующий Документ](./README.md)
<!-- COURSE_NAV_END -->
