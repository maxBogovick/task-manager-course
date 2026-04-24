package com.taskmanager.repository;

import com.taskmanager.entity.TaskExecution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskExecutionRepository extends JpaRepository<TaskExecution, Long> {

    List<TaskExecution> findAllByTaskDefinitionId(Long taskDefinitionId);
}

