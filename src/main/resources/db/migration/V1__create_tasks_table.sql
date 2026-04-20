-- =============================================================
-- V1: Task Orchestration Engine schema
-- =============================================================

-- Task definitions — blueprints / templates for executable tasks
CREATE TABLE IF NOT EXISTS task_definitions (
    id                    BIGSERIAL       PRIMARY KEY,
    name                  VARCHAR(255)    NOT NULL,
    description           TEXT,
    task_type             VARCHAR(64)     NOT NULL,
    config                TEXT            NOT NULL DEFAULT '{}',
    cron_expression       VARCHAR(128),
    enabled               BOOLEAN         NOT NULL DEFAULT TRUE,
    max_retries           INT             NOT NULL DEFAULT 0,
    retry_delay_seconds   INT             NOT NULL DEFAULT 60,
    timeout_seconds       INT             NOT NULL DEFAULT 3600,
    user_id               BIGINT          NOT NULL,
    created_at            TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMP       NOT NULL DEFAULT NOW()
);

-- Task executions — each individual run of a task definition
CREATE TABLE IF NOT EXISTS task_executions (
    id                    BIGSERIAL       PRIMARY KEY,
    task_definition_id    BIGINT          NOT NULL REFERENCES task_definitions(id) ON DELETE CASCADE,
    status                VARCHAR(32)     NOT NULL DEFAULT 'PENDING',
    attempt               INT             NOT NULL DEFAULT 1,
    started_at            TIMESTAMP,
    finished_at           TIMESTAMP,
    output                TEXT,
    error_message         TEXT,
    created_at            TIMESTAMP       NOT NULL DEFAULT NOW()
);

-- Indexes
CREATE INDEX idx_taskdef_user_id       ON task_definitions (user_id);
CREATE INDEX idx_taskdef_enabled       ON task_definitions (enabled);
CREATE INDEX idx_taskdef_task_type     ON task_definitions (task_type);

CREATE INDEX idx_taskexec_definition   ON task_executions (task_definition_id);
CREATE INDEX idx_taskexec_status       ON task_executions (status);
CREATE INDEX idx_taskexec_started      ON task_executions (started_at);
