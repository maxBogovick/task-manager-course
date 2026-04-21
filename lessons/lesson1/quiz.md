# Урок 1. Проверка Понимания

## Как работать с этим файлом

Этот файл нужен не для зубрежки.

Его задача:

- проверить, понял ли ты смысл первого урока
- показать, где у тебя еще пробелы
- заставить тебя объяснять архитектуру словами, а не только узнавать код глазами

Если ты можешь ответить только по памяти на синтаксис, но не можешь объяснить “почему так”, значит урок понят не до конца.

---

## Часть 1. Короткие вопросы

Ответь на каждый вопрос 1-3 предложениями.

### 1. Что такое Spring Boot?

Что именно он упрощает по сравнению с ручной сборкой Java backend-приложения?

### 2. Что такое Spring Web?

Какую часть приложения он закрывает?

### 3. Что такое endpoint?

Из каких частей он состоит?

### 4. Что делает controller?

Что в нем должно быть, а чего в нем быть не должно?

### 5. Что делает service?

Почему service лучше выделять отдельно?

### 6. Что такое bean?

Чем bean отличается от объекта, созданного через `new`?

### 7. Что такое Dependency Injection?

Почему это полезно?

### 8. Почему `record` подходит для response DTO?

### 9. Что дает Lombok в первом уроке?

Что именно он генерирует?

### 10. Почему `Map<String, Object>` хуже, чем отдельный DTO?

---

## Часть 2. Вопросы на причинно-следственные связи

Здесь уже недостаточно дать определение.
Нужно показать понимание архитектуры.

### 1. Почему плохая идея помещать всю логику в controller?

### 2. Почему код может “работать”, но при этом быть плохо спроектированным?

### 3. Что именно Spring делает за нас в цепочке:

```text
HTTP -> controller -> service -> DTO -> JSON
```

### 4. Почему `@RequiredArgsConstructor` не равен Dependency Injection?

### 5. Что изменится, если убрать `@GetMapping`, но оставить обычный Java-метод?

### 6. Что изменится, если убрать `@Service` у service-класса?

### 7. Что изменится, если controller сам начнет создавать `new TaskRunService()`?

### 8. Почему `final` у зависимости — хороший стиль?

### 9. Почему профессиональный backend-код старается разделять роли классов?

### 10. Чем отличается:

- быстрое решение
- хорошее инженерное решение

---

## Часть 3. Разбор кода

Прочитай код и ответь на вопросы.

```java
@RestController
public class StatusController {

    @GetMapping("/api/orchestrator/status")
    public Map<String, Object> status() {
        Map<String, Object> result = new HashMap<>();
        result.put("runningTasks", 0);
        result.put("status", "ACTIVE");
        return result;
    }
}
```

### Вопросы

1. Этот код может работать?
2. Какие здесь есть архитектурные проблемы?
3. Чего здесь не хватает для профессионального варианта?
4. Что бы ты вынес в отдельный класс?
5. Чем заменил бы `Map<String, Object>`?

---

## Часть 4. Сравнение двух стилей

Сравни два варианта.

### Вариант A

```java
@RestController
public class StatusController {

    @GetMapping("/api/orchestrator/status")
    public Map<String, Object> status() {
        Map<String, Object> result = new HashMap<>();
        result.put("runningTasks", 0);
        result.put("status", "ACTIVE");
        return result;
    }
}
```

### Вариант B

```java
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TaskExecutionController {

    private final TaskRunService taskRunService;

    @GetMapping("/orchestrator/status")
    public ResponseEntity<OrchestratorStatusResponse> getOrchestratorStatus() {
        return ResponseEntity.ok(taskRunService.getOrchestratorStatus());
    }
}
```

```java
@Service
public class TaskRunService {

    public OrchestratorStatusResponse getOrchestratorStatus() {
        return new OrchestratorStatusResponse(0, "ACTIVE");
    }
}
```

```java
public record OrchestratorStatusResponse(int runningTasks, String status) {}
```

### Ответь

1. Какой вариант лучше для роста проекта?
2. В каком варианте роли классов разделены яснее?
3. В каком варианте контракт ответа выражен лучше?
4. В каком варианте код легче тестировать?
5. Почему второй вариант полезнее как учебный эталон?

---

## Часть 5. Вопросы по чистой Java версии

Ответь после чтения:

[pure-java-version.md](./pure-java-version.md)

### 1. Что пришлось делать вручную без Spring?

### 2. Что в чистой Java версии является аналогом `@GetMapping`?

### 3. Что в чистой Java версии является аналогом DI?

### 4. Почему ручная сборка JSON — хрупкое решение?

### 5. Какой главный методический вывод дает версия без фреймворка?

---

## Часть 6. Задание “объясни flow”

Объясни по шагам, что происходит, когда клиент вызывает:

`GET /api/orchestrator/status`

Твой ответ должен содержать:

1. HTTP-запрос
2. выбор controller-метода
3. вызов service
4. создание response DTO
5. превращение DTO в JSON
6. возврат HTTP-ответа

Если ты не можешь связно объяснить этот путь, первый урок еще не усвоен.

---

## Часть 7. Практические мини-задачи

### Задача 1

Перепиши endpoint так, чтобы он возвращал не статус оркестратора, а информацию о приложении:

```json
{
  "name": "TaskManager",
  "status": "UP"
}
```

Требования:

- controller
- service
- DTO через `record`

### Задача 2

Напиши плохой вариант этого endpoint-а:

- все в controller
- `Map<String, Object>`

Потом письменно объясни, почему он хуже.

### Задача 3

Напиши вручную конструктор для controller и service, а затем сравни код с версией на Lombok.

Ответь:

- что именно упростил Lombok
- чего Lombok не сделал

---

## Часть 8. Критерий зачета урока

Урок можно считать усвоенным, если ты можешь без подсказки:

1. объяснить, что такое Spring Boot
2. объяснить, что такое controller
3. объяснить, что такое bean
4. объяснить, зачем нужен DI
5. объяснить, почему DTO лучше `Map`
6. объяснить, почему service лучше выделять отдельно
7. показать разницу между Spring-версией и pure Java версией
8. самостоятельно восстановить flow первого endpoint-а
---

<!-- COURSE_NAV_START -->
## Навигация

- [К Началу Урока](./START_HERE.md)
- [Предыдущий Документ](./glossary.md)
- [Следующий Документ](./self-check.md)
<!-- COURSE_NAV_END -->
