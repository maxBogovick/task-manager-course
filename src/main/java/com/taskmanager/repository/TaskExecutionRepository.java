package com.taskmanager.repository;

import com.taskmanager.entity.ExecutionStatus;
import com.taskmanager.entity.TaskExecution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskExecutionRepository extends JpaRepository<TaskExecution, Long> {

    Page<TaskExecution> findAllByTaskDefinitionId(Long taskDefinitionId, Pageable pageable);

    Page<TaskExecution> findAllByStatus(ExecutionStatus status, Pageable pageable);

    /** All currently RUNNING executions with their definitions — used for timeout monitoring. */
    @Query("""
        SELECT e FROM TaskExecution e
        JOIN FETCH e.taskDefinition
        WHERE e.status = 'RUNNING'
          AND e.startedAt IS NOT NULL
    """)
    List<TaskExecution> findAllRunning();

    /** Failed executions eligible for retry (attempt < maxRetries + 1). */
    @Query("""
        SELECT e FROM TaskExecution e
        JOIN FETCH e.taskDefinition d
        WHERE e.status = 'FAILED'
          AND e.attempt <= d.maxRetries
          AND e.finishedAt < :retryCutoff
    """)
    List<TaskExecution> findRetryableExecutions(@Param("retryCutoff") LocalDateTime retryCutoff);

    /** Latest execution for a given definition. */
    @Query("""
        SELECT e FROM TaskExecution e
        WHERE e.taskDefinition.id = :defId
        ORDER BY e.createdAt DESC
        LIMIT 1
    """)
    TaskExecution findLatestByDefinitionId(@Param("defId") Long defId);

    /** Count running executions for a definition (to prevent duplicate runs). */
    long countByTaskDefinitionIdAndStatus(Long taskDefinitionId, ExecutionStatus status);

    /** All executions with the given status — used for startup reconciliation. */
    List<TaskExecution> findAllByStatus(ExecutionStatus status);
}
