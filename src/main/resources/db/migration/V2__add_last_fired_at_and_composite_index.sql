-- Track when each cron-scheduled task was last fired (survives restarts)
ALTER TABLE task_definitions ADD COLUMN last_fired_at TIMESTAMP;

-- Composite index for the frequent "find running executions for definition" query
CREATE INDEX idx_taskexec_definition_status ON task_executions (task_definition_id, status);
