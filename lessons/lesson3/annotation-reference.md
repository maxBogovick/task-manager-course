# Урок 3. Справочник По Аннотациям И Понятиям

## `@PathVariable`

Берет значение из path URL.

Пример:

```java
@GetMapping("/{id}")
public ResponseEntity<TaskDefinitionResponse> getById(@PathVariable Long id) { ... }
```

---

## `@RequestParam`

Берет значение из query string.

Пример:

```java
@RequestParam(required = false) String type
```

---

## `@PageableDefault`

Задает параметры пагинации по умолчанию.

В этом уроке важно не столько запомнить синтаксис, сколько понять идею:

списки ресурсов должны иметь ограничение размера и предсказуемый порядок.

---

## `Pageable`

Spring abstraction для страницы, размера страницы и сортировки.

---

## `PagedResponse<T>`

Типизированная модель страницы ответа.

Она лучше, чем просто `List<T>`, если API должен быть пригоден для роста.
---

<!-- COURSE_NAV_START -->
## Навигация

- [К Началу Урока](./START_HERE.md)
- [Предыдущий Документ](./pure-java-version.md)
- [Следующий Документ](./practice.md)
<!-- COURSE_NAV_END -->
