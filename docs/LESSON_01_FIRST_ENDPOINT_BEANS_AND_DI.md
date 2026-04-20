# Урок 1. Первый Рабочий Endpoint: Beans, DI И Полный Flow Запроса

## Цель урока

В этом уроке у нас есть одна четкая цель:

**сделать и полностью понять первый рабочий endpoint, который по HTTP-запросу возвращает JSON-ответ.**

В конце урока ты должен прийти к такому результату:

ты понимаешь, как в Spring Boot можно вызвать URL, попасть в Java-код, получить данные из service и вернуть клиенту готовый JSON.

На практике мы хотим уметь делать вот такие вещи:

- открыть URL
- получить предсказуемый ответ от приложения
- понимать, какой класс за что отвечает
- не писать все в одном огромном классе

После этого урока ты должен уметь:

- объяснить, что такое endpoint
- показать полный путь одного HTTP-запроса в проекте
- объяснить, что такое `bean` в Spring
- объяснить, зачем нужен Dependency Injection
- объяснить, почему `TaskExecutionController` и `TaskRunService` не создаются через `new`
- показать, как выглядел бы тот же код без Spring beans и без DI

Главный практический результат урока:

ты сможешь самостоятельно собрать первый простой endpoint по образцу проекта и объяснить, как он работает по шагам.

## Что должно получиться в конце

К концу урока у тебя должна быть в голове очень конкретная картина.

Мы хотим, чтобы приложение умело делать следующее:

1. кто-то открывает URL `GET /api/orchestrator/status`
2. приложение принимает этот запрос
3. Java-код вычисляет ответ
4. клиент получает JSON

То есть мы хотим не просто “изучить немного Spring”, а получить работающую возможность:

**запросить состояние части системы через HTTP и получить структурированный ответ.**

Это полезно потому, что почти любое backend-приложение должно уметь:

- принимать запросы извне
- передавать работу внутрь приложения
- возвращать данные в понятном формате

Если ты понял этот урок, значит ты уже понял первый кирпич любого Spring Boot API.

---

## Что именно мы хотим реализовать в этом уроке

В этом уроке мы хотим получить вот такую возможность:

вызвать URL

`GET /api/orchestrator/status`

и в ответ получить JSON примерно такого вида:

```json
{
  "runningTasks": 0,
  "status": "ACTIVE"
}
```

Зачем нам это нужно:

потому что это самый первый полноценный пример backend-функциональности.

Когда ты умеешь сделать такой endpoint, ты уже понимаешь основу веб-приложения:

- HTTP-вход
- controller
- service
- DTO
- bean
- dependency injection

То есть этот endpoint нужен нам не сам по себе, а как первый понятный учебный пример.

На нем мы учимся:

- принимать запрос
- передавать управление по слоям
- возвращать структурированный ответ
- понимать, как Spring создает и связывает объекты

Если говорить совсем прикладно, такой endpoint полезен еще и в реальной жизни:

- он позволяет быстро проверить, что сервер вообще отвечает
- он показывает состояние части системы
- он помогает начать строить API с самого простого, но уже рабочего сценария

---

## Почему именно этот endpoint выбран первым

Потому что для самого первого урока он хорош по трем причинам:

1. Он реально рабочий.
2. У него короткий flow.
3. На нем можно спокойно объяснить, как объекты живут в Spring, не проваливаясь сразу в JPA, БД, транзакции и сложную бизнес-логику.

На этом уроке мы сознательно **не** разбираем:

- базу данных
- repository
- entity
- validation
- exception handling
- scheduler
- orchestrator internals

Все это будет позже отдельными уроками.

В этом уроке у нас одна тема:

**как Spring проводит один HTTP-запрос через beans и DI**.

---

## Где это находится в архитектуре проекта

Для этого урока нам нужен только этот кусок системы:

```text
HTTP GET /api/orchestrator/status
        ->
TaskExecutionController
        ->
TaskRunService
        ->
TaskOrchestrator
        ->
OrchestratorStatusResponse
        ->
JSON response
```

Это и есть первый полный flow, который тебе нужно научиться видеть, читать и затем воспроизводить самому.

---

## Новые термины урока

### Endpoint

Конкретный HTTP-адрес, на который клиент отправляет запрос.

Пример:

`GET /api/orchestrator/status`

### Controller

Класс, который принимает HTTP-запрос и передает работу дальше в приложение.

### Service

Класс, в котором лежит прикладная логика use case.

### DTO

Объект, который используется для передачи данных наружу или внутрь API.

### Bean

Объект, который создает и хранит Spring, а не разработчик вручную через `new`.

### Dependency Injection

Подход, при котором объект не создает свои зависимости сам, а получает их извне.

---

## Разбираемые файлы

1. [TaskExecutionController.java](/Users/maxim/Projects/Java/TaskManager/src/main/java/com/taskmanager/controller/TaskExecutionController.java)
2. [TaskRunService.java](/Users/maxim/Projects/Java/TaskManager/src/main/java/com/taskmanager/service/TaskRunService.java)
3. [TaskOrchestrator.java](/Users/maxim/Projects/Java/TaskManager/src/main/java/com/taskmanager/orchestrator/TaskOrchestrator.java:1)
4. [OrchestratorStatusResponse.java](/Users/maxim/Projects/Java/TaskManager/src/main/java/com/taskmanager/dto/OrchestratorStatusResponse.java)

---

## Шаг 1. Смотрим на endpoint целиком

Нас интересует вот этот метод:

```java
@GetMapping("/orchestrator/status")
public ResponseEntity<OrchestratorStatusResponse> getOrchestratorStatus() {
    return ResponseEntity.ok(taskRunService.getOrchestratorStatus());
}
```

Этот код находится в `TaskExecutionController`.

Что здесь происходит:

1. Приходит HTTP GET запрос на `/api/orchestrator/status`
2. Spring находит подходящий controller-метод
3. Вызывает `getOrchestratorStatus()`
4. Controller не считает статус сам, а делегирует работу в `taskRunService`
5. Service возвращает `OrchestratorStatusResponse`
6. Spring превращает его в JSON
7. Клиент получает HTTP-ответ

Эту мысль нужно хорошо зафиксировать:

controller не должен хранить внутри себя всю логику.
Он принимает запрос и передает его дальше.

---

## Шаг 2. Разбираем controller

Фрагмент класса:

```java
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class TaskExecutionController {

    private final TaskRunService taskRunService;
    private final TaskExecutionService executionService;

    @GetMapping("/orchestrator/status")
    public ResponseEntity<OrchestratorStatusResponse> getOrchestratorStatus() {
        return ResponseEntity.ok(taskRunService.getOrchestratorStatus());
    }
}
```

### Зачем нужен этот класс

Этот класс отвечает за HTTP-слой.

Именно он принимает входящий web-запрос и преобразует его в вызов Java-метода.

### Почему мы не пишем всю логику прямо в `main`

Потому что HTTP-обработка должна быть отделена от прикладной логики.

Если бы вся логика лежала в одном месте:

- код быстро разрастался бы
- было бы трудно тестировать
- было бы трудно переиспользовать логику
- один класс начал бы отвечать сразу за transport и за бизнес-логику

### Что делает поле `taskRunService`

Это зависимость controller.

Controller сам не знает, как получать статус оркестратора.
Он знает только, **кому передать эту работу**.

---

## Шаг 3. Разбираем service

Фрагмент класса:

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class TaskRunService {

    private final TaskOrchestrator orchestrator;
    private final TaskDefinitionService definitionService;
    private final TaskMapper mapper;

    public OrchestratorStatusResponse getOrchestratorStatus() {
        return new OrchestratorStatusResponse(orchestrator.getRunningCount(), "ACTIVE");
    }
}
```

### Зачем нужен этот класс

`TaskRunService` собирает use case уровня приложения.

В рамках нашего endpoint его задача простая:

- взять данные у `TaskOrchestrator`
- упаковать их в DTO ответа

### Почему мы не положили этот код прямо в controller

Потому что controller должен быть тонким.

Если controller начнет:

- обращаться к другим сервисам
- собирать DTO
- принимать прикладные решения

то он станет жирным и неудобным.

Нормальная граница такая:

- controller принимает HTTP
- service выполняет use case

### Что важно заметить уже сейчас

`TaskRunService` тоже не создает `TaskOrchestrator` через `new`.

Значит, кто-то должен передать `TaskOrchestrator` в этот service.

Вот здесь и начинается главное понятие урока:

**bean** и **Dependency Injection**.

---

## Шаг 4. Разбираем DTO ответа

Код:

```java
public record OrchestratorStatusResponse(int runningTasks, String status) {}
```

### Зачем нужен этот класс

Он описывает форму данных, которые endpoint возвращает клиенту.

Для этого endpoint ответ содержит:

- количество running tasks
- строковый статус

### Почему не вернуть просто `Map`

Потому что DTO дает:

- четкую структуру
- понятные поля
- типизацию
- более читаемый контракт API

### Какой JSON получится

Пример ответа:

```json
{
  "runningTasks": 0,
  "status": "ACTIVE"
}
```

---

## Главная тема урока: что такое bean

### Сначала без Spring

Представим обычный Java-код без Spring.

```java
public class TaskOrchestrator {
    public int getRunningCount() {
        return 0;
    }
}

public class TaskRunService {
    private final TaskOrchestrator orchestrator;

    public TaskRunService(TaskOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    public OrchestratorStatusResponse getOrchestratorStatus() {
        return new OrchestratorStatusResponse(orchestrator.getRunningCount(), "ACTIVE");
    }
}

public class TaskExecutionController {
    private final TaskRunService taskRunService;

    public TaskExecutionController(TaskRunService taskRunService) {
        this.taskRunService = taskRunService;
    }

    public OrchestratorStatusResponse getOrchestratorStatus() {
        return taskRunService.getOrchestratorStatus();
    }
}
```

Это пока просто обычные Java-объекты.

Они еще не beans.

### Что надо сделать, чтобы это реально заработало без Spring

Нужно вручную собрать весь граф зависимостей:

```java
public class ManualApplication {
    public static void main(String[] args) {
        TaskOrchestrator orchestrator = new TaskOrchestrator();
        TaskRunService taskRunService = new TaskRunService(orchestrator);
        TaskExecutionController controller = new TaskExecutionController(taskRunService);

        OrchestratorStatusResponse response = controller.getOrchestratorStatus();
        System.out.println(response);
    }
}
```

### Что здесь программист делает руками

Он сам:

- создает `TaskOrchestrator`
- передает его в `TaskRunService`
- создает `TaskExecutionController`
- передает в него `TaskRunService`

То есть он вручную собирает цепочку зависимостей.

---

## Что такое bean

**Bean** — это объект, который создается, хранится и связывается контейнером Spring.

В этом уроке примеры beans:

- `TaskExecutionController`
- `TaskRunService`
- `TaskOrchestrator`

Почему они beans:

потому что Spring создает эти объекты сам и затем связывает их между собой.

### В чем отличие от обычного объекта

Обычный объект:

- вы создали его сами через `new`
- вы сами решаете, когда и как его передавать дальше

Bean:

- его создает Spring
- зависимости в него подставляет Spring
- жизненным циклом управляет Spring

---

## Как этот же код выглядит со Spring

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
@Service
public class TaskOrchestrator {
    public int getRunningCount() {
        return 0;
    }
}
```

### Что здесь произошло

Мы **не создаем** объекты вручную.

Мы только:

- помечаем классы специальными аннотациями
- описываем зависимости полями

А Spring дальше:

- создает эти объекты
- понимает, что controller зависит от service
- понимает, что service зависит от orchestrator
- сам связывает их между собой

---

## Что такое Dependency Injection

Dependency Injection означает:

объект получает зависимость извне, а не создает ее сам.

### Хороший вариант

```java
public class TaskRunService {
    private final TaskOrchestrator orchestrator;

    public TaskRunService(TaskOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }
}
```

Здесь `TaskRunService` не делает так:

```java
private final TaskOrchestrator orchestrator = new TaskOrchestrator();
```

Он получает зависимость снаружи.

### Почему это важно

Потому что тогда:

- код слабее связан
- зависимость можно заменить
- код легче тестировать
- объект не берет на себя лишнюю ответственность

---

## Антипример: как получилось бы хуже

Вот плохой вариант:

```java
public class TaskRunService {
    private final TaskOrchestrator orchestrator = new TaskOrchestrator();

    public OrchestratorStatusResponse getOrchestratorStatus() {
        return new OrchestratorStatusResponse(orchestrator.getRunningCount(), "ACTIVE");
    }
}
```

### Почему это плохо

`TaskRunService` теперь:

- сам решает, какой именно orchestrator использовать
- сам создает зависимость
- становится жестко связанным с конкретной реализацией

Если потом потребуется:

- подменить зависимость
- протестировать отдельно service
- изменить способ создания orchestrator

код станет неудобным.

---

## Разбор аннотаций этого урока

## 1. `@RestController`

### Что делает

Говорит Spring:

“этот класс является web-controller, его методы должны принимать HTTP-запросы и возвращать данные в HTTP-ответ”.

### Где используется

В `TaskExecutionController`.

### Что Spring делает за нас

- создает bean этого класса
- включает его в web-слой
- позволяет методам быть endpoint-ами
- сериализует возвращаемые данные в JSON

### Как было бы без нее

Пришлось бы руками:

- поднимать HTTP-сервер
- сопоставлять URL и методы
- разбирать HTTP-запрос
- формировать HTTP-ответ
- сериализовать Java-объект в JSON

### Мини-антипример без Spring

```java
server.createContext("/api/orchestrator/status", exchange -> {
    if ("GET".equals(exchange.getRequestMethod())) {
        OrchestratorStatusResponse response = controller.getOrchestratorStatus();
        String json = objectMapper.writeValueAsString(response);
        exchange.sendResponseHeaders(200, json.getBytes().length);
        exchange.getResponseBody().write(json.getBytes());
        exchange.close();
    }
});
```

Это намного более шумный код.

---

## 2. `@Service`

### Что делает

Помечает класс как сервисный Spring bean.

### Где используется

В `TaskRunService`.

### Что Spring делает за нас

- создает объект этого класса
- хранит его в контейнере
- может внедрять его в другие beans

### Как было бы без нее

Нужно было бы вручную создавать объект и вручную прокидывать его в controller.

### Важная мысль

Аннотация `@Service` сама по себе не добавляет бизнес-логику.

Она лишь говорит Spring:

“этот объект тоже должен жить в контейнере как управляемый компонент”.

---

## 3. `@RequiredArgsConstructor`

### Что делает

Это аннотация Lombok.
Она генерирует конструктор для всех `final` полей.

В нашем случае Lombok фактически делает примерно такой код:

```java
public TaskExecutionController(TaskRunService taskRunService,
                               TaskExecutionService executionService) {
    this.taskRunService = taskRunService;
    this.executionService = executionService;
}
```

И в `TaskRunService` аналогично.

### Почему это важно для DI

Spring внедряет зависимости через конструктор.

То есть:

- у класса есть конструктор
- Spring видит, какие зависимости нужны
- Spring ищет подходящие beans
- Spring передает их в конструктор

### Что было бы без этой аннотации

Нужно было бы вручную писать конструкторы.

Это не критично, но бойлерплейта было бы больше.

### Важная граница

`@RequiredArgsConstructor` не делает класс bean.
Она только помогает короче записать конструктор.

Bean-ом класс становится из-за Spring-аннотаций вроде `@RestController` и `@Service`.

---

## Что Spring делает за нас в этом конкретном endpoint

Для запроса `GET /api/orchestrator/status` Spring берет на себя:

1. принимает HTTP-запрос
2. находит нужный controller-метод
3. создает `TaskExecutionController` как bean
4. создает `TaskRunService` как bean
5. создает `TaskOrchestrator` как bean
6. связывает эти объекты между собой
7. вызывает нужный Java-метод
8. превращает `OrchestratorStatusResponse` в JSON
9. возвращает HTTP 200 клиенту

---

## Полный flow запроса по шагам

### Шаг 1

Клиент отправляет:

```http
GET /api/orchestrator/status
```

### Шаг 2

Spring перенаправляет запрос в:

```java
TaskExecutionController#getOrchestratorStatus()
```

### Шаг 3

Controller вызывает:

```java
taskRunService.getOrchestratorStatus()
```

### Шаг 4

Service вызывает:

```java
orchestrator.getRunningCount()
```

### Шаг 5

Service собирает DTO:

```java
new OrchestratorStatusResponse(orchestrator.getRunningCount(), "ACTIVE")
```

### Шаг 6

DTO возвращается в controller.

### Шаг 7

Controller делает:

```java
ResponseEntity.ok(...)
```

### Шаг 8

Spring превращает DTO в JSON и отправляет клиенту.

---

## Минимальный рабочий пример, который ты должен уметь повторить

Ниже упрощенная учебная версия по образцу проекта.

### DTO

```java
public record StatusResponse(int runningTasks, String status) {}
```

### Service

```java
@Service
public class StatusService {

    public StatusResponse getStatus() {
        return new StatusResponse(0, "ACTIVE");
    }
}
```

### Controller

```java
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StatusController {

    private final StatusService statusService;

    @GetMapping("/status")
    public ResponseEntity<StatusResponse> getStatus() {
        return ResponseEntity.ok(statusService.getStatus());
    }
}
```

### Что это тебе дает

Уже на первом уроке ты получаешь реальный минимальный vertical slice:

- endpoint
- bean
- service
- DTO
- DI

---

## Как выглядел бы тот же минимальный пример без Spring beans

```java
public record StatusResponse(int runningTasks, String status) {}

public class StatusService {
    public StatusResponse getStatus() {
        return new StatusResponse(0, "ACTIVE");
    }
}

public class StatusController {
    private final StatusService statusService;

    public StatusController(StatusService statusService) {
        this.statusService = statusService;
    }

    public StatusResponse getStatus() {
        return statusService.getStatus();
    }
}

public class ManualApp {
    public static void main(String[] args) {
        StatusService statusService = new StatusService();
        StatusController statusController = new StatusController(statusService);

        StatusResponse response = statusController.getStatus();
        System.out.println(response);
    }
}
```

### Что здесь приходится делать руками

- создавать сервис
- создавать controller
- передавать зависимости
- самим решать, кто и когда что создает

### Что здесь еще не решено

Такой код вообще не обрабатывает HTTP.

Чтобы сделать реальный endpoint, без Spring нужно было бы еще руками добавить:

- HTTP-сервер
- routing
- JSON serialization
- response handling

---

## Практика

## Практика 1. Найди полный путь запроса

Задание:

Пройди endpoint `GET /api/orchestrator/status` от начала до конца и ответь:

1. В каком controller-методе он начинается?
2. В какой service-метод controller передает работу?
3. Какой DTO возвращается в ответ?

---

## Практика 2. Найди beans этого урока

Задание:

Назови три объекта из этого урока, которые Spring создает как beans.

Ожидаемый ответ:

- `TaskExecutionController`
- `TaskRunService`
- `TaskOrchestrator`

После ответа ты должен уметь пояснить:

почему это beans, а не просто “какие-то классы”.

---

## Практика 3. Объясни DI на коде

Задание:

Посмотри на поле:

```java
private final TaskRunService taskRunService;
```

И ответь:

1. Кто создает `TaskRunService`?
2. Кто передает его в controller?
3. Почему controller не делает `new TaskRunService()`?

---

## Практика 4. Перепиши в плохой вариант

Задание:

Возьми `TaskRunService` и напиши учебную плохую версию, где зависимость создается внутри класса:

```java
private final TaskOrchestrator orchestrator = new TaskOrchestrator();
```

После этого письменно объясни:

1. Почему такой код хуже?
2. Что он ломает с точки зрения гибкости и тестируемости?

---

## Практика 5. Собери мини-аналог

Задание:

По образцу урока написать три маленьких класса:

- `PingResponse`
- `PingService`
- `PingController`

И реализовать endpoint:

`GET /api/ping`

с ответом:

```json
{
  "message": "pong"
}
```

Зачем это задание нужно:

чтобы ты сам руками повторил первый рабочий vertical slice и не просто прочитал материал, а собрал такой же кусок приложения сам.

---

## Вопросы на понимание

1. Что такое endpoint простыми словами?
2. Какой полный путь проходит запрос `GET /api/orchestrator/status`?
3. Зачем нужен controller в этом flow?
4. Зачем нужен service в этом flow?
5. Что такое DTO и зачем он нужен здесь?
6. Что такое bean?
7. Чем bean отличается от обычного Java-объекта?
8. Что такое Dependency Injection?
9. Почему `new TaskOrchestrator()` внутри `TaskRunService` хуже, чем внедрение зависимости?
10. Что конкретно Spring делает за нас в этом endpoint?

---

## Частые ошибки новичков

### Ошибка 1

Думать, что bean — это “какой-то особый класс”.

Почему это неверно:

bean — это не особый синтаксис класса.
Это обычный объект, просто его жизненным циклом управляет Spring.

### Ошибка 2

Думать, что `@Service` добавляет логику автоматически.

Почему это неверно:

логика все равно пишется разработчиком.
`@Service` только делает объект управляемым Spring.

### Ошибка 3

Думать, что controller должен сам делать всю работу.

Почему это неверно:

controller отвечает за HTTP-вход, а не за всю прикладную логику.

### Ошибка 4

Думать, что DI — это “когда просто есть поле”.

Почему это неверно:

DI — это не наличие поля.
DI — это способ получения зависимости извне, вместо самостоятельного создания объекта внутри класса.

---

## Вернемся к цели урока

В начале урока цель была такой:

сделать и полностью понять первый рабочий endpoint, который по HTTP-запросу возвращает JSON-ответ.

Если ты прошел материал внимательно, то теперь должен видеть, как именно мы к этой цели пришли:

- выбрали конкретный URL
- нашли controller, который принимает запрос
- нашли service, который выполняет прикладную работу
- увидели DTO, который уходит в ответ
- поняли, что Spring сам создает и связывает объекты через beans и DI

## Краткий итог урока

Зафиксируй четыре главные мысли:

1. Первый endpoint надо понимать как полный flow, а не как один отдельный метод.
2. `TaskExecutionController` и `TaskRunService` являются beans, потому что Spring их создает и связывает.
3. Dependency Injection нужен, чтобы классы не создавали свои зависимости сами.
4. Даже самый простой endpoint уже показывает основу архитектуры Spring-приложения:
   controller -> service -> DTO -> response.

---

## Домашнее задание

1. Выписать полный flow endpoint `GET /api/orchestrator/status`.
2. Своими словами объяснить, что такое bean.
3. Написать короткий пример:
   один service и один controller со Spring.
4. Написать тот же пример без Spring и вручную собрать зависимости.
5. Письменно ответить:
   почему ручная сборка зависимостей хуже, чем DI.

---

## Критерий успешности урока

Урок считается усвоенным, если ты можешь без подсказки:

- показать controller-метод endpoint-а
- показать service-метод, в который идет делегирование
- объяснить, что такое bean на примере `TaskExecutionController`
- объяснить, что такое DI на примере `TaskRunService`
- написать вручную аналогичный код без Spring beans
- объяснить, почему вариант без DI хуже
