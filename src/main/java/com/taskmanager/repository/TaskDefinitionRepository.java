package com.taskmanager.repository;

import com.taskmanager.entity.TaskDefinition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskDefinitionRepository extends JpaRepository<TaskDefinition, Long> {

    Page<TaskDefinition> findAllByUserId(Long userId, Pageable pageable);

    Page<TaskDefinition> findAllByTaskType(String taskType, Pageable pageable);

    Page<TaskDefinition> findAllByEnabled(Boolean enabled, Pageable pageable);

    /** All enabled definitions that have a cron expression — for the scheduler. */
    List<TaskDefinition> findAllByEnabledTrueAndCronExpressionIsNotNull();
}
