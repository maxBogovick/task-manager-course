# Lesson 3. Start Here

## Как пользоваться этим модулем

Если `lesson1` научил тебя первому `GET` endpoint-у, а `lesson2` первому `POST` endpoint-у, то `lesson3` делает следующий шаг:

теперь мы учимся читать ресурсы из API правильно.

Здесь будут два типовых сценария:

1. получить один ресурс по id
2. получить список ресурсов

Именно с этого начинается нормальное понимание чтения данных в REST API.

---

## Конечная цель урока

К концу урока ты должен уметь объяснить и собрать:

1. `GET /api/tasks/{id}`
2. `GET /api/tasks`
3. `GET /api/tasks?type=SHELL_COMMAND`

И понимать:

- что такое `@PathVariable`
- что такое `@RequestParam`
- чем отличается получение одного ресурса от получения коллекции
- зачем нужна пагинация
- почему список лучше возвращать в отдельной модели, а не сырым `List<Map<String, Object>>`

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

## Что нового по сравнению с прошлым уроком

В этом уроке появятся:

- `@PathVariable`
- `@RequestParam`
- `GET` одного ресурса
- `GET` списка ресурсов
- фильтрация через query params
- идея пагинации
- различие между single-resource response и collection response
---

<!-- COURSE_NAV_START -->
## Навигация

- [К Началу Урока](./START_HERE.md)
- [Предыдущий Документ](./self-check.md)
- [Следующий Документ](./README.md)
<!-- COURSE_NAV_END -->
