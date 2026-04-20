package com.taskmanager.service;

import com.taskmanager.dto.*;
import com.taskmanager.entity.TaskDefinition;
import com.taskmanager.exception.TaskNotFoundException;
import com.taskmanager.mapper.TaskMapper;
import com.taskmanager.repository.TaskDefinitionRepository;
import com.taskmanager.repository.TaskExecutionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class TaskDefinitionService {

    private final TaskDefinitionRepository definitionRepo;
    private final TaskExecutionRepository executionRepo;
    private final TaskMapper mapper;
    private final ConfigValidator configValidator;

    @Transactional
    public TaskDefinitionResponse create(TaskDefinitionRequest request) {
        validateCronExpression(request.cronExpression());
        configValidator.validate(request.taskType(), request.config());
        TaskDefinition def = mapper.toEntity(request);
        TaskDefinition saved = definitionRepo.save(def);
        log.info("Created task definition '{}' (id={}, type={})", saved.getName(), saved.getId(), saved.getTaskType());
        return mapper.toDefinitionResponse(saved, null);
    }

    public TaskDefinitionResponse getById(Long id) {
        TaskDefinition def = getEntityById(id);
        String lastStatus = resolveLastStatus(def.getId());
        return mapper.toDefinitionResponse(def, lastStatus);
    }

    @Transactional
    public TaskDefinitionResponse update(Long id, TaskDefinitionRequest request) {
        validateCronExpression(request.cronExpression());
        configValidator.validate(request.taskType(), request.config());
        TaskDefinition def = getEntityById(id);
        mapper.updateEntity(request, def);
        definitionRepo.save(def);
        return mapper.toDefinitionResponse(def, resolveLastStatus(id));
    }

    @Transactional
    public void delete(Long id) {
        TaskDefinition def = getEntityById(id);
        definitionRepo.delete(def);
        log.info("Deleted task definition id={}", id);
    }

    @Transactional
    public TaskDefinitionResponse toggleEnabled(Long id, boolean enabled) {
        TaskDefinition def = getEntityById(id);
        def.setEnabled(enabled);
        definitionRepo.save(def);
        return mapper.toDefinitionResponse(def, resolveLastStatus(id));
    }

    @Transactional
    public void updateLastFiredAt(Long id, LocalDateTime firedAt) {
        definitionRepo.findById(id).ifPresent(def -> {
            def.setLastFiredAt(firedAt);
            definitionRepo.save(def);
        });
    }

    public PagedResponse<TaskDefinitionResponse> getAll(Pageable pageable) {
        return toPagedResponse(definitionRepo.findAll(pageable));
    }

    public PagedResponse<TaskDefinitionResponse> getByUserId(Long userId, Pageable pageable) {
        return toPagedResponse(definitionRepo.findAllByUserId(userId, pageable));
    }

    public PagedResponse<TaskDefinitionResponse> getByType(String taskType, Pageable pageable) {
        return toPagedResponse(definitionRepo.findAllByTaskType(taskType, pageable));
    }

    /** Returns the entity — use only in service layer, never expose to controllers. */
    public TaskDefinition getEntityById(Long id) {
        return definitionRepo.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    // ── Helpers ──────────────────────────────────────────────────────────

    private void validateCronExpression(String cronExpression) {
        if (cronExpression == null || cronExpression.isBlank()) return;
        try {
            CronExpression.parse(cronExpression);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid cron expression '" + cronExpression + "': " + e.getMessage());
        }
    }

    private String resolveLastStatus(Long definitionId) {
        var latest = executionRepo.findLatestByDefinitionId(definitionId);
        return latest != null ? latest.getStatus().name() : null;
    }

    private PagedResponse<TaskDefinitionResponse> toPagedResponse(Page<TaskDefinition> page) {
        var content = page.getContent().stream()
                .map(def -> mapper.toDefinitionResponse(def, resolveLastStatus(def.getId())))
                .toList();

        return new PagedResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}
