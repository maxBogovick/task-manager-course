# Урок 1. Сравнение Подходов

## Зачем нужен этот файл

В первом уроке студенту полезно увидеть не один код, а сразу три способа решения одной задачи:

1. как часто делает новичок
2. как делается профессионально в Spring Boot
3. как это выглядит на чистой Java без фреймворка

Так лучше видно не только “как писать”, но и **почему именно так писать лучше**.

---

## Одна и та же задача

Целевая задача во всех трех вариантах одинаковая:

`GET /api/orchestrator/status`

Ответ:

```json
{
  "runningTasks": 0,
  "status": "ACTIVE"
}
```

---

## Вариант 1. Как часто делает новичок

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

### Почему этот вариант кажется удобным

- мало кода
- быстро написать
- сразу работает

### Почему это слабый подход

- нет отдельного service
- нет явного DTO-контракта
- controller берет на себя лишнюю роль
- студент не учится правильной структуре

---

## Вариант 2. Профессиональный Spring Boot подход

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

### Сильные стороны

- явное разделение слоев
- понятный DTO-контракт
- controller не перегружен
- проект хорошо масштабируется
- Spring убирает инфраструктурный шум

---

## Вариант 3. Чистая Java без Spring

Подробный пример смотри в:

[pure-java-version.md](./pure-java-version.md)

Здесь логика та же, но все приходится делать вручную:

- поднимать HTTP-сервер
- регистрировать маршрут
- создавать service вручную
- проверять HTTP-метод вручную
- собирать JSON вручную

### Сильная сторона этого варианта

Он хорошо показывает, что именно автоматизирует Spring.

### Слабая сторона

Слишком много инфраструктурного кода даже для простой задачи.

---

## Сравнение по критериям

### Читаемость назначения

- новичковый вариант: средняя
- Spring-подход: высокая
- чистая Java: средняя, потому что много шума

### Скорость первого грязного результата

- новичковый вариант: высокая
- Spring-подход: высокая
- чистая Java: ниже

### Масштабируемость проекта

- новичковый вариант: низкая
- Spring-подход: высокая
- чистая Java: зависит от дисциплины, но требует намного больше ручной работы

### Типобезопасность контракта

- новичковый вариант: низкая
- Spring-подход: высокая
- чистая Java: может быть высокой, если делать DTO нормально

### Количество инфраструктурного кода

- новичковый вариант: мало, но за счет плохой архитектуры
- Spring-подход: мало, но за счет фреймворка
- чистая Java: много

---

## Главный вывод для студента

Первый урок должен научить важному различию:

есть код, который просто быстро заработал,
и есть код, который не только работает, но и правильно устроен.

Профессиональный Spring Boot вариант хорош тем, что он одновременно:

- короткий
- понятный
- расширяемый
- архитектурно здоровый

Именно поэтому на нем и надо учиться строить backend с нуля.
---

<!-- COURSE_NAV_START -->
## Навигация

- [К Началу Урока](./START_HERE.md)
- [Предыдущий Документ](./patterns-principles-best-practices.md)
- [Следующий Документ](./pure-java-version.md)
<!-- COURSE_NAV_END -->
