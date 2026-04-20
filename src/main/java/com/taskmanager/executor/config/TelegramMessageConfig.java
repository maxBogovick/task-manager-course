package com.taskmanager.executor.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TelegramMessageConfig(
        @NotBlank String botToken,
        @NotBlank String chatId,
        @NotBlank String message,
        String parseMode
) {}
