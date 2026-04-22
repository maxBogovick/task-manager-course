# Урок 1. Рефакторинг: От Наивной Реализации К Профессиональной

## Зачем нужен этот файл

Этот материал нужен, чтобы студент не видел профессиональную архитектуру как “готовую правильную форму с неба”.

Здесь мы делаем то, что очень важно для инженерного мышления:

1. берем наивную реализацию
2. смотрим, где она ломается
3. постепенно улучшаем ее
4. на каждом шаге фиксируем, какой принцип или практика появились

---

## Исходная наивная версия

Начнем с такого кода:

```java
@RestController
public class StatusController {

    @GetMapping("/api/orchestrator/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> result = new HashMap<>();
        result.put("runningTasks", 0);
        result.put("status", "ACTIVE");
        return result;
    }
}
```

Этот код решает задачу.

Но он пока слишком простой и слишком хрупкий как основа для реального проекта.

---

## Проблема 1. Контракт ответа скрыт внутри `Map`

Сейчас структура ответа выражена слабо.

Мы не видим отдельной модели данных.

### Почему это плохо

- ключи строковые
- легко ошибиться
- API-контракт не выражен явно
- код плохо документирует сам себя

### Первый шаг улучшения

Выносим response DTO:

```java
public record OrchestratorStatusResponse(int runningTasks, String status) {}
```

И переписываем controller:

```java
@RestController
public class StatusController {

    @GetMapping("/api/orchestrator/status")
    public OrchestratorStatusResponse getStatus() {
        return new OrchestratorStatusResponse(0, "ACTIVE");
    }
}
```

### Что улучшилось

- появился явный контракт
- код стал читаемее
- улучшилась типобезопасность

### Какой принцип здесь усилился

Явное моделирование данных и explicit contract design.

---

## Проблема 2. Controller все еще делает слишком много

Даже после DTO controller по-прежнему:

- принимает HTTP
- сам решает use case
- сам формирует бизнес-результат

### Почему это плохо

Пока логика маленькая, это почти незаметно.
Но как только логика вырастет, controller начнет раздуваться.

### Второй шаг улучшения

Выносим use case в service:

```java
@Service
public class TaskRunService {

    public OrchestratorStatusResponse getOrchestratorStatus() {
        return new OrchestratorStatusResponse(0, "ACTIVE");
    }
}
```

```java
@RestController
public class StatusController {

    private final TaskRunService taskRunService;

    public StatusController(TaskRunService taskRunService) {
        this.taskRunService = taskRunService;
    }

    @GetMapping("/api/orchestrator/status")
    public OrchestratorStatusResponse getStatus() {
        return taskRunService.getOrchestratorStatus();
    }
}
```

### Что улучшилось

- controller стал тоньше
- появилась граница между web-слоем и прикладной логикой
- use case теперь живет в service

Теперь можно назвать то, что произошло.

Когда мы перестаем складывать HTTP и прикладную логику в один класс и разводим роли по слоям, такой подход обычно называют:

- `Layered Architecture`

Когда один класс перестает делать сразу слишком много вещей, а роли разделяются яснее, это связано с идеями:

- `SRP`
- `Separation of Concerns`

---

## Проблема 3. HTTP-ответ еще не выражен явно

Технически можно возвращать DTO напрямую.

Но в учебном материале полезно сразу показать, что backend возвращает именно HTTP-response.

### Третий шаг улучшения

Добавляем `ResponseEntity`:

```java
@RestController
public class StatusController {

    private final TaskRunService taskRunService;

    public StatusController(TaskRunService taskRunService) {
        this.taskRunService = taskRunService;
    }

    @GetMapping("/api/orchestrator/status")
    public ResponseEntity<OrchestratorStatusResponse> getStatus() {
        return ResponseEntity.ok(taskRunService.getOrchestratorStatus());
    }
}
```

### Что улучшилось

- студент видит связь с HTTP-status code
- endpoint теперь мыслится не как “метод, который вернул объект”, а как часть HTTP-контракта

### Какая best practice здесь появилась

Явное управление HTTP-ответом через `ResponseEntity`.

---

## Проблема 4. Зависимости подключаются шумно

Конструктор руками — это нормальный вариант.

Но в маленьких service/controller классах он создает лишний бойлерплейт.

### Четвертый шаг улучшения

Добавляем Lombok:

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
@RequiredArgsConstructor
public class TaskRunService {

    public OrchestratorStatusResponse getOrchestratorStatus() {
        return new OrchestratorStatusResponse(0, "ACTIVE");
    }
}
```

### Что улучшилось

- меньше механического кода
- легче держать фокус на структуре

### Важно

Lombok ничего не меняет в архитектурной идее.

Он только убирает бойлерплейт.

---

## Финальная версия

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

---

## Что именно мы получили после рефакторинга

### Было

- один controller
- `Map`
- скрытый контракт
- смешанные роли

### Стало

- отдельный controller
- отдельный service
- отдельный DTO
- явный HTTP-ответ
- cleaner wiring

---

## Какие идеи студент должен увидеть именно здесь

Сначала не название, а смысл:

- роли в коде лучше разделять по слоям
- один класс не должен брать на себя все обязанности
- зависимость лучше получать извне, а не создавать внутри
- контракт ответа лучше делать явным, а не собирать через `Map`

И только после этого можно назвать эти идеи инженерными терминами.

### Архитектурный стиль

- `Layered Architecture`

### Принципы и инженерные идеи

- `SRP`
- `Separation of Concerns`
- снижение связанности через DI
- явный контракт API

### Лучшие практики

- thin controller
- constructor injection
- DTO вместо `Map`
- `record` для простых response-моделей
- `ResponseEntity` для явного HTTP-ответа

---

## Главный вывод

Профессиональная версия не появляется “потому что так принято”.

Она появляется как ответ на проблемы наивной реализации.

Именно так и нужно показывать студенту паттерны, принципы и best practices:

не как список терминов,
а как улучшение кода после обнаружения конкретных проблем.
---

<!-- COURSE_NAV_START -->
## Навигация

- [К Началу Урока](./START_HERE.md)
- [Предыдущий Документ](./step-by-step-build.md)
- [Следующий Документ](./patterns-principles-best-practices.md)
<!-- COURSE_NAV_END -->
