package com.taskmanager.executor.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.executor.ExecutionContext;
import com.taskmanager.executor.ExecutionResult;
import com.taskmanager.executor.TaskExecutor;
import com.taskmanager.executor.config.TelegramMessageConfig;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramMessageExecutor implements TaskExecutor {

    private static final String TELEGRAM_API = "https://api.telegram.org/bot%s/sendMessage";

    private final ObjectMapper objectMapper;

    @Override
    public String getType() {
        return "TELEGRAM_MESSAGE";
    }

    @Override
    public Optional<Class<?>> getConfigClass() {
        return Optional.of(TelegramMessageConfig.class);
    }

    @Override
    public ExecutionResult execute(ExecutionContext context) throws Exception {
        TelegramMessageConfig cfg;
        try {
            cfg = objectMapper.convertValue(context.config(), TelegramMessageConfig.class);
        } catch (IllegalArgumentException e) {
            return ExecutionResult.fail("Invalid config: " + e.getMessage());
        }

        if (cfg.botToken() == null || cfg.botToken().isBlank()) return ExecutionResult.fail("Config 'botToken' is required");
        if (cfg.chatId() == null   || cfg.chatId().isBlank())   return ExecutionResult.fail("Config 'chatId' is required");
        if (cfg.message() == null  || cfg.message().isBlank())  return ExecutionResult.fail("Config 'message' is required");

        log.info("[Exec#{}] Sending Telegram message to chat {}", context.executionId(), cfg.chatId());

        if (context.isCancelled()) return ExecutionResult.fail("Cancelled before sending");

        StringBuilder body = new StringBuilder()
                .append("chat_id=").append(URLEncoder.encode(cfg.chatId(), StandardCharsets.UTF_8))
                .append("&text=").append(URLEncoder.encode(cfg.message(), StandardCharsets.UTF_8));

        if (cfg.parseMode() != null && !cfg.parseMode().isBlank()) {
            body.append("&parse_mode=").append(URLEncoder.encode(cfg.parseMode(), StandardCharsets.UTF_8));
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format(TELEGRAM_API, cfg.botToken())))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .timeout(Duration.ofSeconds(15))
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        try (HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build()) {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200
                    ? ExecutionResult.ok("Message sent to chat " + cfg.chatId() + "\nResponse: " + response.body())
                    : ExecutionResult.fail(response.body(), "Telegram API returned HTTP " + response.statusCode());
        }
    }
}
