# Lesson 1. Start Here

## Как пользоваться этим модулем

Этот урок собран как последовательный учебный пакет.

Не читай файлы в случайном порядке.
Иди по маршруту ниже: он построен от общей цели к пониманию, затем к сравнению подходов, практике и проверке.

Если ты скачал папку `lesson1`, начинай именно с этого файла.

---

## Конечная цель урока

К концу урока ты должен:

1. понять, что такое `Spring Boot`, `Spring Web`, controller, bean, DI, `record`, Lombok
2. увидеть разницу между наивным и профессиональным backend-кодом
3. понять, какие паттерны, принципы проектирования и best practices стоят за этой структурой
4. уметь собрать и объяснить endpoint:

`GET /api/orchestrator/status`

с JSON-ответом:

```json
{
  "runningTasks": 0,
  "status": "ACTIVE"
}
```

---

## Рекомендуемый порядок чтения

### Шаг 1. Основной материал урока

Начни с главного файла:

[README.md](./README.md)

Что ты получишь:

- общую цель урока
- главный результат
- базовую теорию
- разбор ключевых аннотаций
- отличие новичкового и профессионального подхода

---

### Шаг 2. Пошаговая сборка endpoint-а

После основного материала переходи сюда:

[step-by-step-build.md](./step-by-step-build.md)

Что ты получишь:

- последовательную сборку решения
- понимание flow `HTTP -> controller -> service -> DTO -> JSON`
- привязку теории к конкретным шагам

---

### Шаг 3. Как мыслить инженерно: от плохого к хорошему

Теперь смотри, как наивный код превращается в профессиональный:

[refactoring-from-naive-to-professional.md](./refactoring-from-naive-to-professional.md)

Что ты получишь:

- наивную реализацию
- ее минусы
- пошаговое улучшение
- связь между рефакторингом и архитектурой

---

### Шаг 4. Паттерны, принципы и лучшие практики

После рефакторинга переходи к теоретическому закреплению:

[patterns-principles-best-practices.md](./patterns-principles-best-practices.md)

Что ты получишь:

- Layered Architecture
- SRP
- Separation of Concerns
- базовое понимание снижения связанности через DI
- thin controller
- constructor injection
- DTO как явный контракт

---

### Шаг 5. Сравнение трех подходов

Теперь важно сравнить решения:

[comparison.md](./comparison.md)

Что ты получишь:

- новичковый подход
- профессиональный Spring Boot подход
- чистую Java без фреймворка

---

### Шаг 6. Версия без фреймворка

После сравнения открой:

[pure-java-version.md](./pure-java-version.md)

Что ты получишь:

- понимание, что именно Spring делает за тебя
- сравнение manual HTTP/server кода и Spring MVC
- разрушение ощущения “магии”

---

### Шаг 7. Справочник по аннотациям

Если хочешь быстро закрепить синтаксис и смысл аннотаций, открой:

[annotation-reference.md](./annotation-reference.md)

Что ты получишь:

- быстрый возврат к `@SpringBootApplication`
- `@RestController`
- `@RequestMapping`
- `@GetMapping`
- `@Service`
- `@RequiredArgsConstructor`
- `@Slf4j`
- `ResponseEntity`
- `record`

---

### Шаг 8. Практика

Теперь переходи к практике:

[practice.md](./practice.md)

Что ты получишь:

- пошаговые действия
- мини-упражнения
- антипример и исправление
- домашнее задание

---

### Шаг 9. Типичные ошибки

Перед финальной проверкой обязательно прочитай:

[mistakes.md](./mistakes.md)

Что ты получишь:

- список типичных ошибок новичка
- ловушки мышления
- понимание, где чаще всего ломается архитектурная дисциплина

---

### Шаг 10. Словарь терминов

Если по ходу чтения у тебя путаются термины, используй:

[glossary.md](./glossary.md)

Что ты получишь:

- короткие определения без перегруза
- единый словарь первого урока

---

### Шаг 11. Проверка понимания

После прохождения материала ответь на вопросы здесь:

[quiz.md](./quiz.md)

Что ты получишь:

- проверку не только синтаксиса, но и понимания причин
- вопросы по архитектуре
- сравнение подходов
- вопросы по pure Java версии

---

### Шаг 12. Финальная самопроверка

В конце открой:

[self-check.md](./self-check.md)

Что ты получишь:

- короткий финальный лист
- понимание, усвоен ли урок

---

## Короткий маршрут

Если нужен самый правильный и короткий путь, иди так:

1. [README.md](./README.md)
2. [step-by-step-build.md](./step-by-step-build.md)
3. [refactoring-from-naive-to-professional.md](./refactoring-from-naive-to-professional.md)
4. [patterns-principles-best-practices.md](./patterns-principles-best-practices.md)
5. [comparison.md](./comparison.md)
6. [pure-java-version.md](./pure-java-version.md)
7. [practice.md](./practice.md)
8. [quiz.md](./quiz.md)
9. [self-check.md](./self-check.md)

---

## Полный список файлов урока

### Основной маршрут

- [README.md](./README.md)
- [step-by-step-build.md](./step-by-step-build.md)
- [refactoring-from-naive-to-professional.md](./refactoring-from-naive-to-professional.md)
- [patterns-principles-best-practices.md](./patterns-principles-best-practices.md)
- [comparison.md](./comparison.md)
- [pure-java-version.md](./pure-java-version.md)

### Закрепление

- [annotation-reference.md](./annotation-reference.md)
- [practice.md](./practice.md)
- [mistakes.md](./mistakes.md)
- [glossary.md](./glossary.md)
- [quiz.md](./quiz.md)
- [self-check.md](./self-check.md)

---

## Что открыть, если...

### ...ты хочешь понять урок с нуля

Открывай:

[README.md](./README.md)

### ...ты хочешь увидеть сборку шаг за шагом

Открывай:

[step-by-step-build.md](./step-by-step-build.md)

### ...ты хочешь понять, почему наивный код плох

Открывай:

[refactoring-from-naive-to-professional.md](./refactoring-from-naive-to-professional.md)

### ...ты хочешь понять паттерны и принципы

Открывай:

[patterns-principles-best-practices.md](./patterns-principles-best-practices.md)

### ...ты хочешь понять, что делает Spring за тебя

Открывай:

[pure-java-version.md](./pure-java-version.md)

### ...ты хочешь проверить себя

Открывай:

[quiz.md](./quiz.md)

и затем:

[self-check.md](./self-check.md)

---

## Итог

Этот модуль нужно проходить как маршрут, а не как набор отдельных файлов.

Если ты идешь по порядку из этого файла, переходы будут логичными:

- сначала цель
- потом код
- потом наивная и улучшенная версии
- потом паттерны и принципы
- потом сравнение со Spring-less вариантом
- потом практика
- потом проверка понимания
---

<!-- COURSE_NAV_START -->
## Навигация

- [К Началу Урока](./START_HERE.md)
- [Предыдущий Документ](./teacher-notes.md)
- [Следующий Документ](./README.md)
<!-- COURSE_NAV_END -->
