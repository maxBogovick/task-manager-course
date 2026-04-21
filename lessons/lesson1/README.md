# Урок 1. Первый Рабочий Endpoint: Spring Boot, Controller, Bean, Record, Lombok

## Навигация По Уроку

Начинай отсюда:

[START_HERE.md](./START_HERE.md)

Если хочешь пройти урок в правильном порядке с кликабельными переходами между файлами, используй именно этот маршрут.

## Зачем нужен этот урок

В первом уроке студент не должен просто “посмотреть на код”.

Он должен **собрать и понять первый законченный кусок backend-приложения**:

- приложение запускается
- принимает HTTP-запрос
- попадает в Java-метод
- вызывает отдельный service
- возвращает JSON-ответ

Это первая точка, в которой студент начинает видеть не набор файлов, а систему.

---

## Главный итог урока

К концу урока студент должен уметь:

1. объяснить, что такое `Spring Boot`
2. объяснить, что такое `Spring Web`
3. объяснить, что такое `controller`
4. объяснить, что такое `bean`
5. объяснить, зачем нужен `Dependency Injection`
6. объяснить, зачем в проекте используются `record` и `Lombok`
7. показать полный путь запроса:
   `HTTP -> controller -> service -> response DTO -> JSON`
8. написать первый endpoint в профессиональном стиле, а не “все в одном классе”
9. показать, как такой код выглядел бы без Spring Boot, без Spring MVC, без DI и без Lombok
10. объяснить, почему профессиональный вариант лучше для роста проекта

---

## Конечная цель урока

В уроке должна быть одна конкретная цель.

Мы хотим, чтобы к концу урока у студента работал endpoint:

`GET /api/orchestrator/status`

И чтобы он возвращал JSON:

```json
{
  "runningTasks": 0,
  "status": "ACTIVE"
}
```

Это и есть финальный результат урока.

Не “изучить немного Spring”.
Не “познакомиться с аннотациями”.

А получить **конкретную работающую backend-возможность** и полностью понять, как она устроена.

---

## Почему именно эта цель выбрана для первого урока

Потому что этот endpoint:

- достаточно маленький для новичка
- уже похож на реальную backend-функцию
- проходит через несколько важных слоев
- не требует пока объяснять JPA, транзакции, PostgreSQL и сложную бизнес-логику

На нем удобно впервые показать:

- `Spring Boot`
- `Spring Web`
- `@RestController`
- `@RequestMapping`
- `@GetMapping`
- `ResponseEntity`
- `@Service`
- bean
- DI
- `record`
- `Lombok`
- разделение ответственности между слоями

---

## Что именно студент должен получить в голове

После урока студент должен мыслить так:

“Я понимаю, как снаружи приходит HTTP-запрос, как Spring находит нужный controller, как controller передает работу в service, как service собирает данные, как response DTO превращается в JSON, и почему код не должен быть свален в один класс.”

Если эта мысль не сформировалась, урок не достиг цели.

---

## Что именно мы строим на уроке

Мы строим минимальный, но профессионально оформленный flow:

```text
HTTP GET /api/orchestrator/status
        ->
TaskExecutionController
        ->
TaskRunService
        ->
OrchestratorStatusResponse
        ->
JSON
```

Разбираемые файлы проекта:

1. [TaskManagerApplication.java](../../src/main/java/com/taskmanager/TaskManagerApplication.java)
2. [TaskExecutionController.java](../../src/main/java/com/taskmanager/controller/TaskExecutionController.java)
3. [TaskRunService.java](../../src/main/java/com/taskmanager/service/TaskRunService.java)
4. [OrchestratorStatusResponse.java](../../src/main/java/com/taskmanager/dto/OrchestratorStatusResponse.java)

---

## Архитектурная карта этого урока

Важно сразу ограничить область.

В уроке 1 мы **не** объясняем:

- базу данных
- JPA
- Flyway
- scheduler
- executor registry
- retry logic
- timeout monitoring

В уроке 1 мы объясняем только фундамент:

- как поднимается Spring Boot приложение
- как HTTP-запрос попадает в controller
- почему controller не должен содержать всю логику
- как Spring создает объекты как beans
- как dependencies попадают в класс без `new`
- как Java `record` удобно использовать для response DTO
- как Lombok сокращает бойлерплейт

---

## Словарь новых терминов

### Spring Framework

Большой Java-фреймворк, который помогает строить приложения из управляемых объектов, связывать их друг с другом и добавлять инфраструктуру: web, data, security, scheduling и другое.

### Spring Boot

Надстройка над Spring, которая резко упрощает старт проекта:

- дает готовую конфигурацию
- автоматически подключает нужные механизмы
- упрощает запуск приложения
- уменьшает объем ручной настройки

### Spring Web

Модуль Spring, который позволяет принимать HTTP-запросы и связывать URL с Java-методами controller-классов.

### Controller

Класс web-слоя, который принимает HTTP-запрос и передает работу дальше в приложение.

### Service

Класс прикладного слоя, в котором лежит use case или прикладная логика.

### DTO

Объект для передачи данных между слоями или наружу через API.

### Bean

Объект, жизненным циклом которого управляет Spring container.

### Dependency Injection

Подход, при котором класс не создает свои зависимости сам, а получает их извне.

### Record

Компактный тип данных Java для неизменяемых объектов, хорошо подходящий для DTO.

### Lombok

Библиотека, которая через аннотации генерирует шаблонный код: конструкторы, `getter`, `toString`, `log` и т.д.

---

## Что такое Spring Boot простыми словами

Если писать backend на чистой Java вручную, то тебе придется самому:

- поднимать HTTP-сервер
- связывать URL с обработчиками
- создавать объекты приложения
- передавать зависимости между объектами
- настраивать JSON-сериализацию
- настраивать много инфраструктуры вокруг

`Spring Boot` берет значительную часть этой инфраструктурной рутины на себя.

Он позволяет тебе сосредоточиться на логике приложения:

- какие endpoint-ы нужны
- какие service-ы нужны
- какие данные вернуть

Для новичка правильная мысль такая:

`Spring Boot` не “делает магию”.
Он просто автоматизирует огромный пласт повторяющейся инфраструктурной работы.

---

## Что такое Spring Web простыми словами

`Spring Web` отвечает за HTTP-слой приложения.

Именно он позволяет писать код такого вида:

```java
@GetMapping("/orchestrator/status")
public ResponseEntity<OrchestratorStatusResponse> getOrchestratorStatus() {
    return ResponseEntity.ok(taskRunService.getOrchestratorStatus());
}
```

А не писать низкоуровневую обработку сокетов, HTTP-заголовков, тела ответа и JSON-сериализации вручную.

---

## Наш целевой профессиональный код

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

```java
@Service
@RequiredArgsConstructor
@Slf4j
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

Этот код нужно не просто показать.
Его нужно развернуть в полноценное объяснение:

- почему здесь есть слои
- почему нет `new TaskRunService()`
- почему response отделен от controller
- почему DTO сделан через `record`
- что именно здесь делает Spring

---

## Как это часто делают новички

Новичок очень часто пишет так:

```java
@RestController
public class StatusController {

    @GetMapping("/status")
    public Map<String, Object> status() {
        Map<String, Object> response = new HashMap<>();
        response.put("runningTasks", 0);
        response.put("status", "ACTIVE");
        return response;
    }
}
```

Или еще хуже:

```java
@RestController
public class StatusController {

    private final TaskOrchestrator orchestrator = new TaskOrchestrator();

    @GetMapping("/status")
    public Map<String, Object> status() {
        Map<String, Object> response = new HashMap<>();
        response.put("runningTasks", orchestrator.getRunningCount());
        response.put("status", "ACTIVE");
        return response;
    }
}
```

---

## Почему такой новичковый код плох

### 1. Смешаны роли

Controller и принимает HTTP-запрос, и сам решает бизнес-задачу, и формирует структуру ответа вручную.

### 2. Нет явной модели ответа

`Map<String, Object>` не дает нормального контракта API.

Проблемы:

- легко ошибиться в ключе
- сложно рефакторить
- нет типобезопасности
- код хуже читается

### 3. Зависимость создается через `new`

Когда controller сам создает зависимость:

- возрастает связанность
- ухудшается тестируемость
- ломается идея контейнера Spring
- усложняется замена реализации

### 4. Проект плохо масштабируется

Пока endpoint один, проблема не так заметна.
Но когда endpoint-ов десятки, такой стиль превращает проект в хаос.

---

## Как нужно делать профессионально

Профессиональный стиль в этом уроке такой:

- controller принимает HTTP и делегирует
- service содержит прикладной use case
- DTO явно описывает контракт ответа
- зависимости приходят через DI
- Spring управляет жизненным циклом объектов

То есть:

```text
controller -> service -> DTO
```

Это не “излишняя архитектура”.
Это минимальный здоровый уровень разделения ответственности даже для маленького backend.

---

## Как выглядел бы этот код без Spring Boot и Spring Web

Ниже не production-ready сервер, а учебный контрпример, чтобы студент увидел, сколько работы скрывает Spring.

```java
public class ManualHttpServer {

    public static void main(String[] args) throws Exception {
        TaskOrchestrator orchestrator = new TaskOrchestrator();
        TaskRunService taskRunService = new TaskRunService(orchestrator);

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/api/orchestrator/status", exchange -> {
            if (!"GET".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            OrchestratorStatusResponse response = taskRunService.getOrchestratorStatus();
            String json = "{\"runningTasks\": " + response.runningTasks()
                    + ", \"status\": \"" + response.status() + "\"}";

            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, json.getBytes().length);
            exchange.getResponseBody().write(json.getBytes());
            exchange.close();
        });

        server.start();
    }
}
```

Что здесь пришлось делать вручную:

- создавать зависимости руками
- создавать HTTP-сервер руками
- сопоставлять URL вручную
- проверять HTTP-метод вручную
- сериализовать JSON вручную
- формировать HTTP-ответ вручную

И это только для одного маленького endpoint-а.

Именно поэтому Spring Boot и Spring Web нужны не “ради моды”, а ради снижения инфраструктурного шума.

---

## Как выглядел бы код без DI

Антипример:

```java
public class TaskExecutionController {

    private final TaskRunService taskRunService = new TaskRunService(new TaskOrchestrator());

    public OrchestratorStatusResponse getOrchestratorStatus() {
        return taskRunService.getOrchestratorStatus();
    }
}
```

Почему это плохо:

- controller знает, как создавать service
- controller знает, как создавать orchestrator
- зависимости жестко зашиты
- неудобно тестировать
- невозможно нормально строить большое приложение

Профессиональный вариант:

```java
@RestController
@RequiredArgsConstructor
public class TaskExecutionController {

    private final TaskRunService taskRunService;
}
```

Теперь:

- controller знает только о своей зависимости
- объект создает Spring
- wiring вынесен из бизнес-кода
- класс проще тестировать и читать

---

## Паттерн программирования, который студент должен понять в уроке 1

В уроке 1 студент впервые знакомится с базовым слоистым шаблоном backend-приложения:

### Layered Architecture

Идея:

- web-слой принимает запрос
- прикладной слой выполняет use case
- DTO передает данные наружу

В контексте этого урока:

- `TaskExecutionController` — web-слой
- `TaskRunService` — application/service-слой
- `OrchestratorStatusResponse` — response DTO

### Почему это важно

Потому что новичок почти всегда тяготеет к “одному большому классу”.

Профессиональная разработка начинается в тот момент, когда человек понимает:

**не вся логика должна жить там, где пришел HTTP-запрос.**

---

## Полный разбор аннотаций этого урока

Ниже перечислены аннотации, которые студент видит в первом модуле и обязан понять.

### `@SpringBootApplication`

Где находится:

в [TaskManagerApplication.java](../../src/main/java/com/taskmanager/TaskManagerApplication.java)

Что делает технически:

- помечает главный класс приложения
- запускает автоконфигурацию Spring Boot
- включает component scanning
- включает базовую конфигурацию Spring

Кто обрабатывает:

Spring Boot на старте приложения.

Когда проявляется эффект:

при запуске `main()` и поднятии application context.

Почему нужна:

без нее пришлось бы вручную собирать конфигурацию приложения.

Что было бы без нее:

много ручной конфигурации и wiring-кода.

Типичная ошибка новичка:

видеть в ней “магическую обязательную аннотацию” и не понимать, что она фактически собирает несколько важных механизмов старта.

---

### `@RestController`

Что делает технически:

- говорит Spring, что класс является web-controller
- возвращаемые значения методов нужно трактовать как тело HTTP-ответа

Кто обрабатывает:

Spring MVC.

Когда проявляется эффект:

при старте приложения Spring находит этот класс, регистрирует его как bean и анализирует endpoint-методы.

Почему нужна:

чтобы не писать HTTP-обработку вручную.

Что было бы без нее:

Spring не стал бы воспринимать класс как REST-controller.

Что пришлось бы писать вручную:

- регистрацию handler-а
- сериализацию тела ответа
- низкоуровневый HTTP-код

Типичная ошибка новичка:

думать, что `@RestController` “делает endpoint”.
На самом деле endpoint появляется только вместе с mapping-аннотациями.

---

### `@RequestMapping("/api")`

Что делает технически:

задает общий URL-префикс для методов controller-класса.

Почему нужна:

чтобы не повторять `/api` в каждом методе.

Что было бы без нее:

пришлось бы дублировать общий префикс у каждого endpoint-а.

Чем код был бы хуже:

- больше повторения
- выше риск опечаток
- сложнее менять общий путь

Типичная ошибка новичка:

не понимать, что итоговый URL складывается из уровня класса и уровня метода.

---

### `@GetMapping("/orchestrator/status")`

Что делает технически:

связывает HTTP GET запрос на указанный путь с конкретным Java-методом.

Кто обрабатывает:

Spring MVC dispatcher.

Когда проявляется эффект:

при приходе HTTP GET запроса на соответствующий URL.

Почему нужна:

чтобы явно описать маршрут endpoint-а.

Что было бы без нее:

метод не был бы HTTP-обработчиком.

Типичная ошибка новичка:

путать `@GetMapping` с “обычным методом”.
Это не просто метод, а часть HTTP-контракта приложения.

---

### `@Service`

Что делает технически:

помечает класс как Spring bean сервисного слоя.

Почему нужна:

- Spring сможет создать объект сам
- класс войдет в dependency graph
- архитектурно видно, что это service-слой

Что было бы без нее:

если класс не зарегистрирован как bean другим способом, Spring не сможет внедрить его в controller.

Типичная ошибка новичка:

думать, что `@Service` нужна “просто для красоты”.
На самом деле это часть модели приложения и точки регистрации объекта в контейнере.

---

### `@RequiredArgsConstructor`

Что делает технически:

Lombok генерирует конструктор для всех `final`-полей.

Почему нужна:

чтобы использовать constructor injection без ручного бойлерплейта.

Что было бы без нее:

пришлось бы писать конструктор руками:

```java
public TaskExecutionController(TaskRunService taskRunService) {
    this.taskRunService = taskRunService;
}
```

Чем код без нее хуже:

не принципиально хуже архитектурно, но длиннее и шумнее.

Важно:

`@RequiredArgsConstructor` не заменяет DI.
Она только убирает ручное написание конструктора.

Типичная ошибка новичка:

думать, что зависимости “внедряет Lombok”.
Нет. Внедряет Spring. Lombok только генерирует конструктор.

---

### `@Slf4j`

Что делает технически:

Lombok генерирует логгер:

```java
private static final Logger log = LoggerFactory.getLogger(CurrentClass.class);
```

Почему нужна:

чтобы не писать шаблонное объявление логгера вручную.

Что было бы без нее:

пришлось бы объявить логгер руками.

Типичная ошибка новичка:

либо вообще не логировать, либо писать `System.out.println()` вместо нормального логирования.

---

## Что такое bean и почему это ключевая тема урока

`Bean` — это объект, которым управляет Spring.

Это значит, что Spring:

- создает объект
- хранит его в контейнере
- умеет передавать его другим объектам
- управляет его жизненным циклом

В уроке студент должен понять очень простую, но фундаментальную мысль:

**в Spring-приложении важные объекты обычно создаются не через `new`, а контейнером Spring.**

Примеры bean-ов в этом уроке:

- `TaskExecutionController`
- `TaskRunService`

---

## Что такое DI и зачем он нужен

`Dependency Injection` нужен, чтобы классы не создавали свои зависимости сами.

Плохая модель мышления:

“Если мне нужен service, я просто напишу `new TaskRunService()`”.

Правильная модель мышления:

“Мой класс должен объявить, что ему нужно, а создание и связывание объектов должна взять на себя инфраструктура”.

Преимущества:

- ниже связанность
- выше тестируемость
- проще замена реализаций
- проще рост проекта

---

## Почему `record` здесь лучше обычного класса

Наш DTO:

```java
public record OrchestratorStatusResponse(int runningTasks, String status) {}
```

Почему это хорошо:

- короткий код
- DTO неизменяемый
- автоматически есть конструктор
- автоматически есть `equals`, `hashCode`, `toString`
- хорошо выражает идею “это просто переносчик данных”

Как новичок часто делает:

```java
public class OrchestratorStatusResponse {
    private int runningTasks;
    private String status;

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

Для DTO это лишний шум.

Важно объяснить студенту:

`record` хорош там, где объект является в первую очередь **данными**, а не носителем сложного поведения.

---

## Зачем в проекте Lombok

Lombok нужен не для “магии”, а для сокращения рутинного кода.

В этом уроке он дает две вещи:

- `@RequiredArgsConstructor`
- `@Slf4j`

Важно донести:

- Lombok не заменяет понимание Java
- Lombok не заменяет понимание DI
- Lombok не заменяет архитектуру
- Lombok только убирает механический бойлерплейт

Если студент не понимает, какой код Lombok генерирует, значит тему он еще не понял.

---

## Почему controller не должен делать все сам

Непрофессиональный вариант:

```java
@RestController
public class TaskExecutionController {

    @GetMapping("/api/orchestrator/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        int runningTasks = 0;

        Map<String, Object> response = new HashMap<>();
        response.put("runningTasks", runningTasks);
        response.put("status", "ACTIVE");

        return ResponseEntity.ok(response);
    }
}
```

Проблема не в том, что код “не работает”.

Проблема в том, что он учит студента плохому мышлению:

- HTTP-слой знает лишнее
- нет прикладного слоя
- нет нормального DTO
- нет привычки к разделению ролей

Профессиональный вариант учит правильной дисциплине с самого начала.

---

## Что именно делает Spring за нас в этом уроке

Spring берет на себя:

1. запуск приложения
2. поиск controller-ов и service-ов
3. создание bean-ов
4. связывание зависимостей
5. регистрацию endpoint-ов
6. вызов Java-метода при HTTP-запросе
7. преобразование DTO в JSON
8. формирование HTTP-ответа

Если студент не видит этот список явно, у него возникает ощущение “магии”.

Ее нужно разрушать прямо в уроке.

---

## Пошаговый учебный сценарий урока

Ниже логика, в которой стоит вести студента.

### Шаг 1. Показываем конечный результат

Сначала студент должен увидеть цель:

- URL
- HTTP-метод
- JSON-ответ

### Шаг 2. Показываем полный flow

Потом:

```text
HTTP -> controller -> service -> DTO -> JSON
```

### Шаг 3. Разбираем Spring Boot

Объясняем, как вообще приложение стартует.

### Шаг 4. Разбираем controller

Объясняем `@RestController`, `@RequestMapping`, `@GetMapping`, `ResponseEntity`.

### Шаг 5. Разбираем service

Объясняем `@Service`, ответственность service-слоя, DI.

### Шаг 6. Разбираем DTO

Объясняем `record`.

### Шаг 7. Разбираем Lombok

Показываем, какой код сгенерировался бы вручную.

### Шаг 8. Показываем антипример

Один большой controller, `Map`, `new`, отсутствие слоев.

### Шаг 9. Сравниваем новичковый и профессиональный стиль

Это обязательная часть урока.

### Шаг 10. Практика

Студент сам создает или повторяет endpoint.

---

## Практический результат урока

К концу урока студент должен суметь сам написать:

1. класс приложения со стартом Spring Boot
2. controller с одним `GET` endpoint
3. service-слой
4. response DTO на `record`
5. constructor injection через Lombok

---

## Вопросы на понимание

1. Почему controller не должен содержать прикладную логику?
2. Почему `Map<String, Object>` хуже, чем отдельный response DTO?
3. Что такое bean?
4. Почему в Spring-проекте зависимости обычно не создают через `new`?
5. Что именно делает `@RestController`?
6. Что именно делает `@GetMapping`?
7. Что именно делает `@Service`?
8. Что именно делает `@RequiredArgsConstructor` и чего она не делает?
9. Почему `record` удобен для response DTO?
10. Как выглядел бы этот endpoint без Spring Web?

---

## Практика для студента

1. Поднять приложение.
2. Реализовать или повторить endpoint `GET /api/orchestrator/status`.
3. Вернуть DTO, а не `Map`.
4. Вынести логику получения статуса в service.
5. Убедиться, что ответ приходит в JSON.
6. Объяснить вслух полный flow запроса от URL до JSON-ответа.

---

## Типичные ошибки новичков, которые нужно прямо проговорить в уроке

1. Писать всю логику в controller.
2. Возвращать `Map` вместо DTO.
3. Создавать зависимости через `new`.
4. Не понимать, какой код генерирует Lombok.
5. Думать, что Spring “сам все делает”, не понимая механизма.
6. Путать `Spring`, `Spring Boot` и `Spring Web`.
7. Считать `record` просто “сокращенной записью класса”, не понимая идею неизменяемого DTO.

---

## Что вы еще не сказали, но это стоит добавить в урок

Ниже список тем, которые полезно явно включить в lesson 1, хотя вы их не перечислили напрямую.

### 1. `ResponseEntity`

Студенту важно понять, зачем мы возвращаем не просто DTO, а `ResponseEntity<DTO>`.

Нужно объяснить:

- как задается HTTP-статус
- почему это удобнее и явнее
- когда можно возвращать просто объект, а когда лучше `ResponseEntity`

### 2. Разница между `Spring Framework`, `Spring Boot` и `Spring Web`

Новички очень часто смешивают эти понятия.

Нужно развести:

- `Spring Framework` — основа
- `Spring Boot` — удобный старт и автоконфигурация
- `Spring Web` — HTTP-слой

### 3. Что такое JSON-сериализация

Даже если глубоко не разбирать Jackson, студент должен понимать:

- Java-объект не уходит по сети напрямую
- Spring превращает DTO в JSON
- это делает web-инфраструктура автоматически

### 4. Что такое HTTP endpoint

Нужно отдельно проговорить:

- URL
- HTTP method
- request
- response
- status code

Без этого часть студентов будет механически копировать `@GetMapping`, не понимая, что он связывает.

### 5. Почему `final` у зависимостей важно

Когда пишем:

```java
private final TaskRunService taskRunService;
```

стоит объяснить:

- зависимость обязательная
- после создания объекта она не меняется
- так код безопаснее и понятнее

### 6. Разница между “работает” и “спроектировано правильно”

Это одна из важнейших методических мыслей первого урока.

Нужно прямо сказать:

код может работать и при плохой архитектуре.
Но задача инженера не только “заставить работать”, а построить код, который можно развивать, читать, тестировать и не бояться менять.

### 7. Мини-блок про читаемость и рост проекта

Важно объяснить, что текущий стиль нужен не ради “академической красоты”, а потому что через 3 месяца в проекте будет:

- больше endpoint-ов
- больше сервисов
- больше DTO
- больше логики

И плохой стиль начнет дорого стоить.

---

## Итоговая формулировка результата урока для программы курса

Можно зафиксировать результат урока в такой форме:

**После урока студент с нуля понимает, как запускается Spring Boot приложение, как HTTP GET запрос попадает в `@RestController`, как controller через DI вызывает `@Service`, как `record` используется как response DTO, зачем в проекте нужен Lombok, и почему профессиональный backend-код строится через разделение слоев, а не через один большой controller.**

---

## Короткая формулировка результата урока

Если нужна короткая версия для оглавления курса:

**Урок 1: собрать первый endpoint и понять фундамент Spring Boot приложения: controller, bean, DI, record, Lombok и профессиональное разделение ответственности.**
---

<!-- COURSE_NAV_START -->
## Навигация

- [К Началу Урока](./START_HERE.md)
- [Предыдущий Документ](./START_HERE.md)
- [Следующий Документ](./step-by-step-build.md)
<!-- COURSE_NAV_END -->
