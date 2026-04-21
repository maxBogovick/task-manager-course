# Урок 1 Без Фреймворка: Тот Же Результат На Чистой Java

## Зачем нужен этот материал

Этот файл нужен не для того, чтобы заменить Spring Boot.

Он нужен для другого:

показать студенту, **какую работу фреймворк берет на себя**.

Очень важно, чтобы студент не думал так:

“Spring просто магически делает backend.”

Правильная мысль должна быть такой:

“То, что я делаю через Spring, можно сделать и вручную, но тогда мне придется писать намного больше инфраструктурного кода.”

---

## Та же конечная цель

Мы хотим получить тот же результат, что и в основном уроке:

`GET /api/orchestrator/status`

Ответ:

```json
{
  "runningTasks": 0,
  "status": "ACTIVE"
}
```

Но теперь мы не используем:

- Spring Boot
- Spring Web
- `@RestController`
- `@Service`
- DI container
- автосериализацию JSON
- Lombok

---

## Что придется делать вручную

На чистой Java нам придется самим:

1. поднять HTTP-сервер
2. обработать входящий запрос
3. проверить HTTP-метод
4. сопоставить URL с нужной логикой
5. вручную создать объекты
6. вручную передать зависимости
7. вручную сформировать JSON
8. вручную выставить HTTP-заголовки и статус

Именно здесь становится видно, сколько инфраструктуры скрывает Spring.

---

## Полный flow без фреймворка

Вариант без Spring будет выглядеть так:

```text
main()
        ->
создаем service вручную
        ->
поднимаем HttpServer вручную
        ->
регистрируем обработчик URL вручную
        ->
проверяем GET вручную
        ->
вызываем service вручную
        ->
собираем JSON вручную
        ->
отправляем HTTP response вручную
```

---

## Пример на чистой Java

Ниже учебный пример на стандартном JDK HTTP-сервере.

```java
package com.taskmanager.manual;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class ManualTaskManagerApplication {

    public static void main(String[] args) throws IOException {
        ManualTaskRunService taskRunService = new ManualTaskRunService(new ManualTaskOrchestrator());

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/orchestrator/status", new OrchestratorStatusHandler(taskRunService));
        server.start();

        System.out.println("Manual server started on http://localhost:8080");
    }

    static class OrchestratorStatusHandler implements HttpHandler {

        private final ManualTaskRunService taskRunService;

        public OrchestratorStatusHandler(ManualTaskRunService taskRunService) {
            this.taskRunService = taskRunService;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            ManualOrchestratorStatusResponse response = taskRunService.getOrchestratorStatus();

            String json = "{"
                    + "\"runningTasks\": " + response.getRunningTasks() + ", "
                    + "\"status\": \"" + response.getStatus() + "\""
                    + "}";

            byte[] body = json.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, body.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(body);
            }
        }
    }
}
```

```java
package com.taskmanager.manual;

public class ManualTaskRunService {

    private final ManualTaskOrchestrator orchestrator;

    public ManualTaskRunService(ManualTaskOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    public ManualOrchestratorStatusResponse getOrchestratorStatus() {
        return new ManualOrchestratorStatusResponse(orchestrator.getRunningCount(), "ACTIVE");
    }
}
```

```java
package com.taskmanager.manual;

public class ManualTaskOrchestrator {

    public int getRunningCount() {
        return 0;
    }
}
```

```java
package com.taskmanager.manual;

public class ManualOrchestratorStatusResponse {

    private final int runningTasks;
    private final String status;

    public ManualOrchestratorStatusResponse(int runningTasks, String status) {
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

---

## Что здесь делает разработчик вручную

Разберем по пунктам.

### 1. Сам поднимает HTTP-сервер

В Spring Boot это делает инфраструктура приложения.

Здесь нужно вручную:

```java
HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
```

### 2. Сам регистрирует маршрут

Вместо `@GetMapping`:

```java
server.createContext("/api/orchestrator/status", new OrchestratorStatusHandler(taskRunService));
```

### 3. Сам проверяет HTTP-метод

Вместо автоматического связывания `GET`:

```java
if (!"GET".equals(exchange.getRequestMethod())) {
    exchange.sendResponseHeaders(405, -1);
    return;
}
```

### 4. Сам создает и связывает зависимости

Вместо bean container и DI:

```java
ManualTaskRunService taskRunService = new ManualTaskRunService(new ManualTaskOrchestrator());
```

### 5. Сам превращает объект в JSON

Вместо автоматической сериализации:

```java
String json = "{"
        + "\"runningTasks\": " + response.getRunningTasks() + ", "
        + "\"status\": \"" + response.getStatus() + "\""
        + "}";
```

### 6. Сам формирует HTTP-ответ

Вместо `ResponseEntity.ok(...)`:

```java
exchange.getResponseHeaders().add("Content-Type", "application/json");
exchange.sendResponseHeaders(200, body.length);
```

---

## Где здесь уже видны проблемы ручного подхода

Даже на таком маленьком примере появляются слабые места.

### 1. Много инфраструктурного шума

Код уже занят не только бизнес-идеей, но и низкоуровневой HTTP-обвязкой.

### 2. JSON собирается руками

Это хрупко:

- легко ошибиться в кавычках
- легко забыть поле
- легко сломать формат

### 3. Wiring зашит в `main`

Пока классов мало, это терпимо.
Но при росте проекта wiring становится отдельной сложной задачей.

### 4. Нет удобной декларативности

В Spring достаточно посмотреть на аннотации.
Здесь надо руками читать весь flow и связывать детали самостоятельно.

---

## Как тот же результат выглядит в Spring

Теперь сравним с вариантом на Spring.

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

    private final TaskOrchestrator orchestrator;

    public OrchestratorStatusResponse getOrchestratorStatus() {
        return new OrchestratorStatusResponse(orchestrator.getRunningCount(), "ACTIVE");
    }
}
```

```java
public record OrchestratorStatusResponse(int runningTasks, String status) {}
```

---

## Что стало короче и чище со Spring

С использованием Spring:

1. не нужно поднимать сервер вручную
2. не нужно вручную регистрировать URL
3. не нужно вручную проверять HTTP-метод для этого handler-а
4. не нужно вручную связывать зависимости
5. не нужно вручную собирать JSON
6. не нужно вручную формировать большую часть HTTP-ответа

То есть фокус смещается на смысл:

- какой endpoint нужен
- какой service нужен
- какой DTO вернуть

---

## Что важно объяснить студенту после сравнения

Очень важная мысль:

`Spring` полезен не потому, что “без него нельзя”.

Без него можно.

Но без него ты быстро начинаешь тратить слишком много сил на инфраструктуру вместо прикладной логики.

Фреймворк полезен тогда, когда он:

- убирает рутину
- стандартизирует структуру
- помогает масштабировать проект

---

## Где чистая Java все еще полезна методически

Этот материал особенно полезен, чтобы студент понял:

### 1. Что делает `@GetMapping`

Она заменяет ручное сопоставление:

- URL
- HTTP-метода
- Java-метода

### 2. Что делает `@RestController`

Она убирает низкоуровневую HTTP-рутину и делает класс частью web-слоя.

### 3. Что делает DI

Он заменяет ручное создание графа объектов.

### 4. Зачем нужен DTO

Чтобы ответ был выражен явно и типобезопасно, а не собирался строками.

---

## Что можно дать студенту как упражнение

Сделай два варианта одного и того же endpoint-а:

1. на чистой Java
2. на Spring Boot

После этого письменно ответь:

1. Где больше инфраструктурного кода?
2. Где выше риск ошибок?
3. Где проще читать назначение endpoint-а?
4. Где легче масштабировать проект?
5. Почему профессиональные backend-проекты обычно не строят на вручную собранном HTTP-слое?

---

## Итог

Материал без фреймворка нужен для одной важной цели:

показать, что Spring не создает “магию”, а убирает повторяющуюся инфраструктурную работу.

Когда студент это видит, он начинает изучать фреймворк осмысленно, а не как набор непонятных аннотаций.
---

<!-- COURSE_NAV_START -->
## Навигация

- [К Началу Урока](./START_HERE.md)
- [Предыдущий Документ](./comparison.md)
- [Следующий Документ](./annotation-reference.md)
<!-- COURSE_NAV_END -->
