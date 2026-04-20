package com.taskmanager.executor.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.executor.ExecutionContext;
import com.taskmanager.executor.ExecutionResult;
import com.taskmanager.executor.TaskExecutor;
import com.taskmanager.executor.config.ShellCommandConfig;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class ShellCommandExecutor implements TaskExecutor {

    private final ObjectMapper objectMapper;

    @Override
    public String getType() {
        return "SHELL_COMMAND";
    }

    @Override
    public Optional<Class<?>> getConfigClass() {
        return Optional.of(ShellCommandConfig.class);
    }

    @Override
    public ExecutionResult execute(ExecutionContext context) throws Exception {
        ShellCommandConfig cfg;
        try {
            cfg = objectMapper.convertValue(context.config(), ShellCommandConfig.class);
        } catch (IllegalArgumentException e) {
            return ExecutionResult.fail("Invalid config: " + e.getMessage());
        }

        if (cfg.command() == null || cfg.command().isBlank()) {
            return ExecutionResult.fail("Config 'command' is required");
        }

        String workDir = cfg.workDir() != null ? cfg.workDir() : System.getProperty("user.home");
        int timeout = cfg.timeoutSeconds() != null ? cfg.timeoutSeconds() : 300;

        log.info("[Exec#{}] Running shell: {}", context.executionId(), cfg.command());

        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
        ProcessBuilder pb = isWindows
                ? new ProcessBuilder("cmd.exe", "/c", cfg.command())
                : new ProcessBuilder("sh", "-c", cfg.command());

        pb.directory(Path.of(workDir).toFile());
        pb.redirectErrorStream(true);

        Process process = pb.start();

        StringBuilder output = new StringBuilder();
        try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (context.isCancelled()) {
                    process.destroyForcibly();
                    return ExecutionResult.fail(output.toString(), "Cancelled by user");
                }
                output.append(line).append("\n");
            }
        }

        boolean finished = process.waitFor(timeout, TimeUnit.SECONDS);
        if (!finished) {
            process.destroyForcibly();
            return ExecutionResult.fail(output.toString(), "Process timed out after " + timeout + "s");
        }

        int exitCode = process.exitValue();
        return exitCode == 0
                ? ExecutionResult.ok(output.toString().trim())
                : ExecutionResult.fail(output.toString().trim(), "Exit code: " + exitCode);
    }
}
