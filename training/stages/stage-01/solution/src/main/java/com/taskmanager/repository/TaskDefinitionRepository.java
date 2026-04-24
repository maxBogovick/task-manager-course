package com.taskmanager.repository;

import com.taskmanager.entity.TaskDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskDefinitionRepository extends JpaRepository<TaskDefinition, Long> {
}

