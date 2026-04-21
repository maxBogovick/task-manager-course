# Урок 2 Без Фреймворка: POST Endpoint На Чистой Java

## Зачем нужен этот файл

Этот материал показывает, как тот же `POST /api/tasks` выглядел бы без Spring.

Так лучше видно, какую работу Spring берет на себя:

- чтение request body
- преобразование JSON в Java-объект
- вызов нужного handler-а
- формирование HTTP-response

---

## Та же цель

Нам нужен:

`POST /api/tasks`

с JSON body и ответом `201 Created`.

---

## Упрощенный пример на чистой Java

```java
public class ManualTaskServer {

    public static void main(String[] args) throws IOException {
        ManualTaskDefinitionService service = new ManualTaskDefinitionService();

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/tasks", exchange -> {
            if (!"POST".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            // В учебном примере здесь подразумевается ручной JSON parsing.
            // В реальности пришлось бы подключать библиотеку или писать парсер.

            ManualTaskDefinitionResponse response =
                    service.create(new ManualTaskDefinitionRequest("Task", "SHELL_COMMAND"));

            String json = "{\"id\":1,\"name\":\"" + response.getName()
                    + "\",\"taskType\":\"" + response.getTaskType() + "\"}";

            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(201, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });

        server.start();
    }
}
```

---

## Что здесь приходится делать вручную

1. Проверять HTTP method.
2. Читать body из input stream.
3. Самому думать о JSON parsing.
4. Самому создавать request object.
5. Самому собирать JSON ответа.
6. Самому выставлять status code `201`.

---

## Что делает Spring вместо этого

С `@PostMapping` и `@RequestBody` Spring:

- сам читает body
- сам маппит JSON в DTO
- сам вызывает нужный controller-метод
- сам сериализует response DTO обратно в JSON

Поэтому Spring-версия короче, чище и лучше подходит для роста проекта.
---

<!-- COURSE_NAV_START -->
## Навигация

- [К Началу Урока](./START_HERE.md)
- [Предыдущий Документ](./comparison.md)
- [Следующий Документ](./annotation-reference.md)
<!-- COURSE_NAV_END -->
