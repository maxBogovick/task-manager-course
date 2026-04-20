package com.taskmanager.executor.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.executor.ExecutionContext;
import com.taskmanager.executor.ExecutionResult;
import com.taskmanager.executor.TaskExecutor;
import com.taskmanager.executor.config.HttpRequestConfig;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class HttpRequestExecutor implements TaskExecutor {

    private final ObjectMapper objectMapper;

    @Override
    public String getType() {
        return "HTTP_REQUEST";
    }

    @Override
    public Optional<Class<?>> getConfigClass() {
        return Optional.of(HttpRequestConfig.class);
    }

    @Override
    public ExecutionResult execute(ExecutionContext context) throws Exception {
        HttpRequestConfig cfg;
        try {
            cfg = objectMapper.convertValue(context.config(), HttpRequestConfig.class);
        } catch (IllegalArgumentException e) {
            return ExecutionResult.fail("Invalid config: " + e.getMessage());
        }

        if (cfg.url() == null || cfg.url().isBlank()) {
            return ExecutionResult.fail("Config 'url' is required");
        }

        String method = cfg.method() != null ? cfg.method().toUpperCase() : "GET";
        int timeout = cfg.timeoutSeconds() != null ? cfg.timeoutSeconds() : 30;

        log.info("[Exec#{}] HTTP {} {}", context.executionId(), method, cfg.url());

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(cfg.url()))
                .timeout(Duration.ofSeconds(timeout));

        Map<String, String> headers = cfg.headers();
        if (headers != null) {
            headers.forEach(builder::header);
        }

        HttpRequest.BodyPublisher bodyPublisher = cfg.body() != null
                ? HttpRequest.BodyPublishers.ofString(cfg.body())
                : HttpRequest.BodyPublishers.noBody();

        builder.method(method, bodyPublisher);

        if (context.isCancelled()) {
            return ExecutionResult.fail("Cancelled before request sent");
        }

        try (HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(timeout))
                .build()) {

            HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            String output = String.format("HTTP %d\n\n%s", response.statusCode(), response.body());

            return response.statusCode() >= 200 && response.statusCode() < 400
                    ? ExecutionResult.ok(output)
                    : ExecutionResult.fail(output, "HTTP " + response.statusCode());
        }
    }
}
