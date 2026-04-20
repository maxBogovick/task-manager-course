package com.taskmanager.executor.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.executor.ExecutionContext;
import com.taskmanager.executor.ExecutionResult;
import com.taskmanager.executor.TaskExecutor;
import com.taskmanager.executor.config.FileOrganizerConfig;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileOrganizerExecutor implements TaskExecutor {

    private static final Map<String, String> DEFAULT_RULES = Map.ofEntries(
            Map.entry("jpg", "Images"), Map.entry("jpeg", "Images"),
            Map.entry("png", "Images"), Map.entry("gif", "Images"),
            Map.entry("webp", "Images"), Map.entry("svg", "Images"),
            Map.entry("mp4", "Videos"), Map.entry("mkv", "Videos"),
            Map.entry("avi", "Videos"), Map.entry("mov", "Videos"),
            Map.entry("mp3", "Music"), Map.entry("flac", "Music"),
            Map.entry("wav", "Music"), Map.entry("aac", "Music"),
            Map.entry("pdf", "Documents"), Map.entry("doc", "Documents"),
            Map.entry("docx", "Documents"), Map.entry("xls", "Documents"),
            Map.entry("xlsx", "Documents"), Map.entry("pptx", "Documents"),
            Map.entry("txt", "Documents"), Map.entry("csv", "Documents"),
            Map.entry("zip", "Archives"), Map.entry("tar", "Archives"),
            Map.entry("gz", "Archives"), Map.entry("rar", "Archives"),
            Map.entry("7z", "Archives"),
            Map.entry("exe", "Programs"), Map.entry("dmg", "Programs"),
            Map.entry("pkg", "Programs"), Map.entry("deb", "Programs"),
            Map.entry("apk", "Programs")
    );

    private final ObjectMapper objectMapper;

    @Override
    public String getType() {
        return "FILE_ORGANIZER";
    }

    @Override
    public Optional<Class<?>> getConfigClass() {
        return Optional.of(FileOrganizerConfig.class);
    }

    @Override
    public ExecutionResult execute(ExecutionContext context) throws Exception {
        FileOrganizerConfig cfg;
        try {
            cfg = objectMapper.convertValue(context.config(), FileOrganizerConfig.class);
        } catch (IllegalArgumentException e) {
            return ExecutionResult.fail("Invalid config: " + e.getMessage());
        }

        if (cfg.sourceDir() == null || cfg.sourceDir().isBlank()) {
            return ExecutionResult.fail("Config 'sourceDir' is required");
        }

        Path source = Path.of(cfg.sourceDir());
        if (!Files.isDirectory(source)) {
            return ExecutionResult.fail("Source directory does not exist: " + cfg.sourceDir());
        }

        boolean dryRun = Boolean.TRUE.equals(cfg.dryRun());

        Map<String, String> rules = new HashMap<>(DEFAULT_RULES);
        if (cfg.rules() != null) {
            cfg.rules().forEach((k, v) -> rules.put(k.toLowerCase(), v));
        }

        log.info("[Exec#{}] Organizing files in: {} (dryRun={})", context.executionId(), cfg.sourceDir(), dryRun);

        StringBuilder report = new StringBuilder();
        int movedCount = 0;
        int skippedCount = 0;

        List<Path> files;
        try (var stream = Files.list(source)) {
            files = stream.filter(Files::isRegularFile).toList();
        }

        for (Path file : files) {
            if (context.isCancelled()) {
                report.append("\n⚠ Cancelled after moving ").append(movedCount).append(" files");
                return ExecutionResult.fail(report.toString(), "Cancelled by user");
            }

            String fileName = file.getFileName().toString();
            String ext = getExtension(fileName).toLowerCase();

            if (ext.isEmpty()) { skippedCount++; continue; }

            String targetFolder = rules.getOrDefault(ext, "Other");
            Path targetDir = source.resolve(targetFolder);

            if (dryRun) {
                report.append(String.format("  [DRY] %s → %s/%s\n", fileName, targetFolder, fileName));
            } else {
                Files.createDirectories(targetDir);
                Path target = targetDir.resolve(fileName);
                if (Files.exists(target)) {
                    String base = fileName.substring(0, fileName.lastIndexOf('.'));
                    target = targetDir.resolve(base + "_" + System.currentTimeMillis() + "." + ext);
                }
                try {
                    Files.move(file, target, StandardCopyOption.ATOMIC_MOVE);
                    report.append(String.format("  ✓ %s → %s/%s\n", fileName, targetFolder, target.getFileName()));
                } catch (IOException e) {
                    report.append(String.format("  ✗ %s — %s\n", fileName, e.getMessage()));
                    skippedCount++;
                    continue;
                }
            }
            movedCount++;
        }

        String summary = String.format("%s%d files organized, %d skipped%s",
                dryRun ? "[DRY RUN] " : "", movedCount, skippedCount,
                report.isEmpty() ? "" : "\n\n" + report);

        return ExecutionResult.ok(summary);
    }

    private String getExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot > 0 ? fileName.substring(dot + 1) : "";
    }
}
