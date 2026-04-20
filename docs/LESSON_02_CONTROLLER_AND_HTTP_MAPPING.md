# Урок 2. Controller И HTTP-Маппинг: Как URL Попадает В Java-Метод

## Цель урока

В этом уроке у нас одна четкая цель:

**понять, как Spring связывает HTTP-запрос с методом controller и как из Java-объекта получается HTTP-ответ.**

К концу урока ты должен понимать и уметь объяснить:

- зачем в приложении нужен controller
- что делают `@RestController`, `@RequestMapping`, `@GetMapping`
- как URL `GET /api/orchestrator/status` попадает в конкретный Java-метод
- зачем нужен `ResponseEntity`
- как выглядел бы тот же HTTP-слой без Spring MVC

Главный практический результат урока:

ты сможешь самостоятельно написать простой controller с одним GET endpoint и объяснить каждую аннотацию в нем.

---

## Что должно получиться в конце

Мы хотим получить очень конкретную возможность:

1. отправить HTTP GET запрос на URL
2. попасть в нужный Java-метод
3. получить HTTP-ответ со статусом `200 OK`
4. получить JSON с данными

В этом уроке наша целевая возможность такая:

`GET /api/orchestrator/status`

Ответ:

```json
{
  "runningTasks": 0,
  "status": "ACTIVE"
}
```

Зачем тебе это нужно:

потому что любой API начинается с умения принять запрос и правильно направить его в код.

Если ты не понимаешь controller и HTTP-маппинг, ты не сможешь строить:

- REST API
- CRUD endpoints
- запросы с параметрами
- POST / PUT / DELETE операции

---

## На чем мы сосредоточимся

В прошлом уроке мы смотрели на endpoint целиком:

`controller -> service -> DTO`

Теперь мы сознательно сужаем тему.

В этом уроке мы разбираем только HTTP-слой:

- как Spring видит controller
- как Spring понимает URL
- как выбирается нужный метод
- как формируется ответ

Мы специально **не** уходим сейчас в:

- базу данных
- repository
- entity
- validation
- exception handling

Одна тема урока:

**как работает controller и почему Spring позволяет не писать HTTP-инфраструктуру вручную.**

---

## Разбираемые файлы

1. [TaskExecutionController.java](/Users/maxim/Projects/Java/TaskManager/src/main/java/com/taskmanager/controller/TaskExecutionController.java)
2. [TaskRunService.java](/Users/maxim/Projects/Java/TaskManager/src/main/java/com/taskmanager/service/TaskRunService.java)
3. [OrchestratorStatusResponse.java](/Users/maxim/Projects/Java/TaskManager/src/main/java/com/taskmanager/dto/OrchestratorStatusResponse.java)

---

## Наш целевой код

В этом уроке нас интересует вот этот фрагмент:

```java
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

Именно этот кусок кода отвечает за то, что запрос на:

`GET /api/orchestrator/status`

попадает в метод:

`getOrchestratorStatus()`

---

## Полный flow этого урока

Вот путь, который проходит запрос:

```text
Клиент отправляет GET /api/orchestrator/status
        ->
Spring MVC ищет подходящий controller
        ->
Spring MVC находит метод getOrchestratorStatus()
        ->
метод вызывает taskRunService.getOrchestratorStatus()
        ->
получает DTO OrchestratorStatusResponse
        ->
оборачивает DTO в ResponseEntity.ok(...)
        ->
Spring превращает DTO в JSON
        ->
клиент получает HTTP 200 + JSON
```

Твоя задача после урока:

уметь восстановить этот путь по памяти и объяснить его своими словами.

---

## Шаг 1. Что такое controller

Controller — это класс, который работает на границе между HTTP-миром и Java-кодом приложения.

Проще говоря:

- снаружи приходит web-запрос
- controller принимает его
- controller вызывает нужную логику приложения
- controller возвращает ответ клиенту

### Что controller делает в нашем примере

Он:

1. принимает GET-запрос
2. понимает, что этот запрос относится к URL `/api/orchestrator/status`
3. вызывает `taskRunService.getOrchestratorStatus()`
4. возвращает результат обратно клиенту

### Что controller не должен делать

Controller не должен:

- хранить всю бизнес-логику внутри себя
- обращаться ко всему подряд напрямую
- превращаться в большой “комбайн”

Хороший controller обычно:

- небольшой
- читаемый
- делегирует работу service-слою

---

## Шаг 2. Разбираем `@RestController`

Класс начинается так:

```java
@RestController
public class TaskExecutionController
```

### Что делает `@RestController`

Эта аннотация сообщает Spring:

“этот класс является web-controller, а его методы могут быть HTTP endpoint-ами”.

### Что Spring делает благодаря этой аннотации

Spring:

- создает bean этого класса
- включает его в web-слой
- ищет внутри него методы с mapping-аннотациями
- обрабатывает возвращаемые значения как HTTP-ответ

### Почему без нее было бы сложнее

Без `@RestController` пришлось бы вручную:

- поднимать HTTP-сервер
- сопоставлять URL с кодом
- писать обработку HTTP-запросов
- сериализовать Java-объекты в JSON

### Как выглядел бы код без Spring

```java
server.createContext("/api/orchestrator/status", exchange -> {
    if ("GET".equals(exchange.getRequestMethod())) {
        OrchestratorStatusResponse response = service.getOrchestratorStatus();
        String json = objectMapper.writeValueAsString(response);

        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, json.getBytes().length);
        exchange.getResponseBody().write(json.getBytes());
        exchange.close();
    }
});
```

Здесь сразу видно проблему:

вместо чистого метода мы уже пишем низкоуровневую HTTP-инфраструктуру.

---

## Шаг 3. Разбираем `@RequestMapping("/api")`

В controller есть такая аннотация:

```java
@RequestMapping("/api")
```

### Что она делает

Она задает общий префикс URL для всех методов этого controller.

Это значит:

если внутри класса есть метод с `@GetMapping("/orchestrator/status")`,
то итоговый URL будет таким:

`/api` + `/orchestrator/status` = `/api/orchestrator/status`

### Зачем это удобно

Потому что не нужно повторять `/api` в каждом методе.

Это делает код:

- короче
- чище
- единообразнее

### Как было бы без нее

Пришлось бы писать полный путь в каждом методе:

```java
@GetMapping("/api/orchestrator/status")
public ResponseEntity<OrchestratorStatusResponse> getOrchestratorStatus() {
    ...
}
```

Если методов становится много, дублирование быстро растет.

---

## Шаг 4. Разбираем `@GetMapping("/orchestrator/status")`

Ключевой фрагмент:

```java
@GetMapping("/orchestrator/status")
public ResponseEntity<OrchestratorStatusResponse> getOrchestratorStatus() {
    return ResponseEntity.ok(taskRunService.getOrchestratorStatus());
}
```

### Что делает `@GetMapping`

Она говорит Spring:

“если придет GET-запрос на этот путь, вызови именно этот метод”.

### Что значит GET

GET обычно используется, когда мы хотим:

- получить данные
- ничего не создавать
- ничего не изменять

В нашем случае это как раз соответствует задаче:

мы просто хотим узнать текущий статус оркестратора.

### Как Spring выбирает метод

Spring смотрит:

1. HTTP-метод запроса
2. путь запроса
3. mapping-аннотации в controller-ах

И потом вызывает подходящий Java-метод.

### Почему это удобно

Потому что вместо ручного `if/else` по URL и HTTP-методу ты просто пишешь понятную декларацию рядом с методом.

---

## Шаг 5. Разбираем сигнатуру метода

Метод выглядит так:

```java
public ResponseEntity<OrchestratorStatusResponse> getOrchestratorStatus()
```

Разберем по частям.

### `public`

Метод должен быть доступен Spring для вызова.

### `ResponseEntity<OrchestratorStatusResponse>`

Это означает:

- внутри ответа лежит объект `OrchestratorStatusResponse`
- вместе с телом ответа мы явно формируем HTTP-ответ

### `getOrchestratorStatus()`

Имя метода может быть почти любым, но хорошее имя помогает понять смысл endpoint-а.

Здесь имя читается очень естественно:

получить статус оркестратора.

---

## Шаг 6. Что такое `ResponseEntity`

Внутри метода мы видим:

```java
return ResponseEntity.ok(taskRunService.getOrchestratorStatus());
```

### Что такое `ResponseEntity`

Это объект, который позволяет явно управлять HTTP-ответом.

С его помощью можно задавать:

- HTTP-статус
- тело ответа
- заголовки

### Что делает `ResponseEntity.ok(...)`

Это короткая запись для:

- вернуть HTTP-статус `200 OK`
- положить переданный объект в тело ответа

### Почему это полезно

Потому что endpoint работает не просто с Java-объектом, а с HTTP-ответом.

`ResponseEntity` делает это явным.

### Какой ответ у нас получится

Если service вернул:

```java
new OrchestratorStatusResponse(0, "ACTIVE")
```

то клиент получит:

- HTTP status: `200 OK`
- body:

```json
{
  "runningTasks": 0,
  "status": "ACTIVE"
}
```

---

## Шаг 7. Почему controller вызывает service

Внутри метода:

```java
taskRunService.getOrchestratorStatus()
```

### Почему не написать все прямо здесь

Можно было бы теоретически сделать так:

```java
@GetMapping("/orchestrator/status")
public ResponseEntity<OrchestratorStatusResponse> getOrchestratorStatus() {
    int runningTasks = 0;
    return ResponseEntity.ok(new OrchestratorStatusResponse(runningTasks, "ACTIVE"));
}
```

Но это плохое направление.

Почему:

- controller начинает хранить прикладную логику
- логику тяжелее переиспользовать
- controller становится толще
- код сложнее тестировать и поддерживать

Правильнее так:

- controller принимает HTTP-запрос
- service делает прикладную работу
- controller возвращает результат

---

## Как выглядит service в нашем примере

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

Тебе сейчас важно зафиксировать одну мысль:

controller не должен знать, как именно считается статус.

Controller должен знать только одно:

кому передать работу.

---

## Как Spring находит нужный метод

Когда приложение запускается, Spring сканирует controller-ы и строит карту маршрутов.

Упрощенно это выглядит так:

- найден controller `TaskExecutionController`
- у него есть базовый путь `/api`
- у метода `getOrchestratorStatus()` есть путь `/orchestrator/status`
- у метода указан HTTP-тип `GET`

Значит, Spring регистрирует маршрут:

`GET /api/orchestrator/status -> TaskExecutionController#getOrchestratorStatus`

Это очень важная мысль.

Ты не пишешь вручную “таблицу маршрутизации”.
Spring строит ее на основе аннотаций.

---

## Как выглядел бы тот же controller без Spring MVC

Ниже упрощенный вариант, чтобы было видно, что именно Spring снимает с разработчика.

```java
public class ManualHttpHandler {

    private final TaskRunService taskRunService;
    private final ObjectMapper objectMapper;

    public ManualHttpHandler(TaskRunService taskRunService, ObjectMapper objectMapper) {
        this.taskRunService = taskRunService;
        this.objectMapper = objectMapper;
    }

    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        if ("GET".equals(method) && "/api/orchestrator/status".equals(path)) {
            OrchestratorStatusResponse response = taskRunService.getOrchestratorStatus();
            String json = objectMapper.writeValueAsString(response);

            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, json.getBytes().length);
            exchange.getResponseBody().write(json.getBytes());
            exchange.close();
            return;
        }

        exchange.sendResponseHeaders(404, -1);
        exchange.close();
    }
}
```

### Что здесь приходится делать вручную

- читать путь запроса
- читать HTTP-метод
- сравнивать их руками
- вызывать нужную логику вручную
- сериализовать DTO в JSON
- выставлять заголовки
- выставлять HTTP-статус
- обрабатывать неизвестные пути

### Что решает Spring MVC

Spring MVC убирает этот инфраструктурный шум и позволяет описывать поведение гораздо чище:

```java
@GetMapping("/orchestrator/status")
public ResponseEntity<OrchestratorStatusResponse> getOrchestratorStatus() {
    return ResponseEntity.ok(taskRunService.getOrchestratorStatus());
}
```

---

## Разбор аннотаций урока

## 1. `@RestController`

### Что делает

Помечает класс как web-controller, методы которого могут обрабатывать HTTP-запросы и возвращать данные клиенту.

### Что Spring делает за нас

- регистрирует controller
- включает его в обработку web-запросов
- преобразует возвращаемые объекты в HTTP-ответ

### Что было бы без нее

Пришлось бы вручную писать HTTP-обработчики.

---

## 2. `@RequestMapping("/api")`

### Что делает

Задает общий базовый путь для методов controller-а.

### Что Spring делает за нас

Spring объединяет базовый путь класса и путь метода в один итоговый URL.

### Что было бы без нее

Путь пришлось бы дублировать в каждом методе.

---

## 3. `@GetMapping("/orchestrator/status")`

### Что делает

Привязывает именно GET-запрос именно к этому пути и именно к этому методу.

### Что Spring делает за нас

Spring сам выбирает нужный Java-метод по HTTP-методу и URL.

### Что было бы без нее

Нужно было бы вручную писать route matching.

---

## 4. `@RequiredArgsConstructor`

### Что делает

Генерирует конструктор для `final` полей.

### Зачем это здесь

Чтобы Spring мог передать зависимости в controller через конструктор.

### Что было бы без нее

Нужно было бы писать конструктор вручную.

### Важная граница

Эта аннотация не делает класс controller-ом и не делает его bean-ом.
Она только убирает бойлерплейт конструктора.

---

## Что Spring делает за нас именно в этом controller

Когда приходит запрос `GET /api/orchestrator/status`, Spring:

1. принимает HTTP-запрос
2. определяет URL и тип запроса
3. находит нужный controller
4. находит нужный метод
5. вызывает этот метод
6. получает `ResponseEntity`
7. сериализует DTO в JSON
8. отправляет HTTP-ответ клиенту

---

## Минимальный рабочий пример, который ты должен уметь повторить

```java
public record PingResponse(String message) {}
```

```java
@Service
public class PingService {
    public PingResponse getPing() {
        return new PingResponse("pong");
    }
}
```

```java
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PingController {

    private final PingService pingService;

    @GetMapping("/ping")
    public ResponseEntity<PingResponse> getPing() {
        return ResponseEntity.ok(pingService.getPing());
    }
}
```

После такого кода ты должен понимать:

- какой URL заработает
- какой метод будет вызван
- какой JSON получит клиент

---

## Практика

## Практика 1. Найди маршрут

Ответь на вопросы:

1. Какой полный URL формируется из `@RequestMapping("/api")` и `@GetMapping("/orchestrator/status")`?
2. Почему этот endpoint вызывается именно через GET?

---

## Практика 2. Найди точку входа HTTP-слоя

Открой `TaskExecutionController` и ответь:

1. Какая аннотация делает класс controller-ом?
2. Какая аннотация связывает URL с методом?
3. Какой метод вызывается для `/api/orchestrator/status`?

---

## Практика 3. Объясни `ResponseEntity`

Посмотри на строку:

```java
return ResponseEntity.ok(taskRunService.getOrchestratorStatus());
```

И письменно объясни:

1. Какой HTTP-статус вернется клиенту?
2. Что попадет в тело ответа?
3. Почему это не просто “вернуть Java-объект”?

---

## Практика 4. Перепиши без `@RequestMapping`

Перепиши учебный пример так, чтобы в controller не было `@RequestMapping("/api")`, а полный путь был записан прямо в `@GetMapping`.

После этого письменно ответь:

1. Что стало более неудобным?
2. Почему базовый путь на уровне класса обычно полезен?

---

## Практика 5. Напиши свой controller

Напиши минимальный controller:

- URL: `GET /api/hello`
- ответ:

```json
{
  "message": "hello"
}
```

Требования:

- отдельный DTO
- отдельный service
- `@RestController`
- `@RequestMapping("/api")`
- `@GetMapping("/hello")`
- `ResponseEntity.ok(...)`

---

## Вопросы на понимание

1. Что такое controller простыми словами?
2. Что делает `@RestController`?
3. Что делает `@RequestMapping("/api")`?
4. Что делает `@GetMapping("/orchestrator/status")`?
5. Как Spring понимает, какой Java-метод вызвать для конкретного URL?
6. Зачем нужен `ResponseEntity`?
7. Почему controller не должен хранить прикладную логику внутри себя?
8. Что именно Spring MVC снимает с разработчика по сравнению с ручной HTTP-обработкой?

---

## Частые ошибки новичков

### Ошибка 1

Думать, что controller “сам ходит в интернет”.

Почему это неверно:

controller не отправляет HTTP-запросы наружу.
Он принимает входящий запрос внутри твоего приложения.

### Ошибка 2

Думать, что `@GetMapping` просто “подписывает метод”.

Почему это неверно:

эта аннотация участвует в реальной маршрутизации HTTP-запросов.

### Ошибка 3

Думать, что `ResponseEntity` нужен всегда только ради красоты.

Почему это неверно:

он нужен, когда ты хочешь явно работать с HTTP-ответом: статусом, телом и заголовками.

### Ошибка 4

Думать, что controller должен сам вычислять все данные.

Почему это неверно:

controller должен быть тонким и передавать прикладную работу дальше.

---

## Вернемся к цели урока

В начале урока цель была такой:

понять, как Spring связывает HTTP-запрос с методом controller и как из Java-объекта получается HTTP-ответ.

Теперь ты должен видеть этот путь полностью:

- URL приходит в приложение
- Spring по аннотациям находит нужный controller-метод
- method вызывает service
- service возвращает DTO
- controller возвращает `ResponseEntity`
- Spring превращает DTO в JSON и отправляет клиенту

---

## Краткий итог урока

Зафиксируй главное:

1. Controller — это входная точка HTTP-слоя.
2. `@RequestMapping` и `@GetMapping` связывают URL с Java-методом.
3. `ResponseEntity` делает HTTP-ответ явным.
4. Spring MVC избавляет тебя от ручной маршрутизации, сериализации и низкоуровневой HTTP-обработки.

---

## Домашнее задание

1. Своими словами описать путь запроса `GET /api/orchestrator/status`.
2. Написать учебный `HelloController` с `GET /api/hello`.
3. Написать тот же сценарий без Spring MVC и сравнить, какой код получился длиннее и сложнее.
4. Объяснить письменно разницу между `@RequestMapping` и `@GetMapping`.

---

## Критерий успешности урока

Урок считается усвоенным, если ты можешь без подсказки:

- объяснить, что делает controller
- объяснить, что делают `@RestController`, `@RequestMapping`, `@GetMapping`
- показать, как URL превращается в вызов Java-метода
- объяснить, зачем нужен `ResponseEntity`
- написать простой GET endpoint по образцу урока
