# Lesson 4. Start Here

## Как пользоваться этим модулем

После `lesson3`, где ты научился читать один ресурс и список ресурсов, следующий логичный шаг:

научиться обновлять и удалять ресурс.

В этом уроке будет два сценария:

1. `PUT /api/tasks/{id}`
2. `DELETE /api/tasks/{id}`

---

## Конечная цель урока

К концу урока ты должен уметь объяснить и собрать:

1. `PUT /api/tasks/{id}` для обновления задачи
2. `DELETE /api/tasks/{id}` для удаления задачи

И понимать:

- чем update отличается от create
- зачем id передается в path
- почему `PUT` и `DELETE` не равны `POST`
- зачем для delete часто возвращают `204 No Content`
- почему controller не должен сам обновлять и удалять данные вручную

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

- `@PutMapping`
- `@DeleteMapping`
- обновление существующего ресурса
- удаление ресурса
- `204 No Content`
- различие между create, update и delete use case
---

<!-- COURSE_NAV_START -->
## Навигация

- [К Началу Урока](./START_HERE.md)
- [Предыдущий Документ](./self-check.md)
- [Следующий Документ](./README.md)
<!-- COURSE_NAV_END -->
