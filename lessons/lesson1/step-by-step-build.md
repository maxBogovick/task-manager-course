# Урок 1. Пошаговая Сборка С Нуля

## Что мы хотим получить в конце

К концу этой пошаговой сборки у тебя должен заработать endpoint:

`GET /api/orchestrator/status`

с ответом:

```json
{
  "runningTasks": 0,
  "status": "ACTIVE"
}
```

Это не абстрактная теория.
Это конкретный рабочий результат, к которому мы идем шаг за шагом.

---

## Полный путь, который мы строим

С самого начала держи в голове всю цепочку:

```text
HTTP GET /api/orchestrator/status
        ->
controller
        ->
service
        ->
response DTO
        ->
JSON
```

Если в какой-то момент ты теряешь одно из звеньев, значит нужно остановиться и восстановить картину.

---

## Шаг 0. Точка старта

В Spring Boot приложении есть главный класс запуска.

Пример:

```java
package com.taskmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TaskManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskManagerApplication.class, args);
    }
}
```

### Что здесь важно понять

`main()` запускает приложение.

Но в Spring Boot запуск означает не просто “выполнить Java-код”.
Во время старта Spring:

- поднимает приложение
- создает контейнер объектов
- ищет controller-ы и service-ы
- подготавливает web-слой

---

## Шаг 1. Сначала делаем response DTO

Начинать удобно с ответа, который мы хотим вернуть.

Создай:

```java
package com.taskmanager.dto;

public record OrchestratorStatusResponse(int runningTasks, String status) {}
```

### Почему начинаем с DTO

Потому что студенту важно сначала увидеть форму результата.

Мы заранее фиксируем контракт:

- есть поле `runningTasks`
- есть поле `status`

### Почему `record`

Потому что это простой объект данных.
Он не хранит сложное поведение.
Он просто описывает структуру ответа.

---

## Шаг 2. Делаем service

Теперь нужен класс, который будет возвращать этот DTO.

```java
package com.taskmanager.service;

import com.taskmanager.dto.OrchestratorStatusResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskRunService {

    public OrchestratorStatusResponse getOrchestratorStatus() {
        return new OrchestratorStatusResponse(0, "ACTIVE");
    }
}
```

### Что здесь происходит

Пока service возвращает простой учебный ответ.

На первом шаге это нормально.

Наша задача сейчас не построить весь оркестратор, а понять форму правильного backend-кода:

- есть service
- в нем лежит use case
- controller позже будет делегировать работу сюда

### Почему не класть это сразу в controller

Потому что controller должен принимать HTTP-запрос, а не становиться местом хранения всей логики.

---

## Шаг 3. Делаем controller

Теперь создаем HTTP-вход в приложение.

```java
package com.taskmanager.controller;

import com.taskmanager.dto.OrchestratorStatusResponse;
import com.taskmanager.service.TaskRunService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class TaskExecutionController {

    private final TaskRunService taskRunService;

    @GetMapping("/orchestrator/status")
    public ResponseEntity<OrchestratorStatusResponse> getOrchestratorStatus() {
        return ResponseEntity.ok(taskRunService.getOrchestratorStatus());
    }
}
```

### Что здесь важно понять

Этот класс:

- принимает HTTP-запрос
- не создает service через `new`
- не собирает JSON вручную
- не считает данные сам
- делегирует работу service-слою

Именно так и должен выглядеть первый профессиональный endpoint.

---

## Шаг 4. Что делает Spring после этого

Когда приложение стартует, Spring:

1. видит `@RestController`
2. регистрирует controller как bean
3. видит `@Service`
4. регистрирует service как bean
5. создает `TaskRunService`
6. создает `TaskExecutionController`
7. передает `TaskRunService` в controller через конструктор
8. связывает URL `GET /api/orchestrator/status` с методом `getOrchestratorStatus()`

Это очень важный момент.

Если студент не понимает эти 8 шагов, значит он пока видит Spring как магию.

---

## Шаг 5. Проверяем результат

После запуска приложения вызови:

```bash
curl http://localhost:8080/api/orchestrator/status
```

Ожидаемая идея ответа:

```json
{
  "runningTasks": 0,
  "status": "ACTIVE"
}
```

### Что произошло внутри

1. HTTP GET запрос пришел на сервер
2. Spring MVC нашел нужный controller-метод
3. вызвал `getOrchestratorStatus()`
4. controller вызвал `taskRunService.getOrchestratorStatus()`
5. service вернул `OrchestratorStatusResponse`
6. Spring превратил DTO в JSON
7. клиент получил `200 OK`

---

## Шаг 6. Теперь связываем это с реальным проектом

В настоящем проекте service уже не возвращает хардкод.
Он обращается к оркестратору.

Смотри:

```java
public OrchestratorStatusResponse getOrchestratorStatus() {
    return new OrchestratorStatusResponse(orchestrator.getRunningCount(), "ACTIVE");
}
```

Это хороший момент для студента:

архитектурная форма та же самая, просто внутри use case появляется настоящая логика.

---

## Как это сделал бы новичок

Новичковый вариант:

```java
@RestController
public class StatusController {

    @GetMapping("/api/orchestrator/status")
    public ResponseEntity<String> getStatus() {
        return ResponseEntity.ok("{\"runningTasks\":0,\"status\":\"ACTIVE\"}");
    }
}
```

Или:

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

### Почему это плохой учебный старт

Потому что студент не учится:

- выделять service-слой
- строить явный DTO-контракт
- думать о росте проекта
- использовать DI правильно

---

## Как это должен делать профессионал

Профессиональный подход:

1. controller принимает запрос
2. service реализует use case
3. DTO описывает контракт ответа
4. Spring управляет объектами
5. зависимости приходят через constructor injection

Это и есть первая здоровая архитектурная дисциплина.

---

## Что здесь происходит без Lombok

Без Lombok нужно было бы писать конструктор руками:

```java
@RestController
@RequestMapping("/api")
public class TaskExecutionController {

    private final TaskRunService taskRunService;

    public TaskExecutionController(TaskRunService taskRunService) {
        this.taskRunService = taskRunService;
    }

    @GetMapping("/orchestrator/status")
    public ResponseEntity<OrchestratorStatusResponse> getOrchestratorStatus() {
        return ResponseEntity.ok(taskRunService.getOrchestratorStatus());
    }
}
```

Архитектурно это нормально.
Lombok здесь только уменьшает механический шум.

---

## Что здесь происходит без `record`

Без `record` DTO пришлось бы писать так:

```java
public class OrchestratorStatusResponse {

    private final int runningTasks;
    private final String status;

    public OrchestratorStatusResponse(int runningTasks, String status) {
        this.runningTasks = runningTasks;
        this.status = status;
    }

    public int getRunningTasks() {
        return runningTasks;
    }

    public String getStatus() {
        return status;
    }
}
```

Для простого DTO это избыточно.

---

## Что здесь происходит без Spring

Без Spring нужно было бы вручную:

- поднять HTTP-сервер
- связать URL с кодом
- создать service
- передать его в handler
- собрать JSON
- вернуть HTTP-ответ

Подробный вариант смотри в:

[pure-java-version.md](./pure-java-version.md)

---

## Мини-итог

После этой пошаговой сборки ты должен увидеть простую, но фундаментальную вещь:

даже самый первый endpoint лучше сразу строить как маленький, но правильно разделенный backend-flow.

Именно это закладывает правильную привычку на все следующие уроки.
---

<!-- COURSE_NAV_START -->
## Навигация

- [К Началу Урока](./START_HERE.md)
- [Предыдущий Документ](./README.md)
- [Следующий Документ](./refactoring-from-naive-to-professional.md)
<!-- COURSE_NAV_END -->
