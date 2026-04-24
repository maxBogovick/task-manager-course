CREATE TABLE task_executions (
    id BIGSERIAL PRIMARY KEY,
    task_definition_id BIGINT NOT NULL REFERENCES task_definitions(id) ON DELETE CASCADE,
    status VARCHAR(32) NOT NULL,
    output TEXT,
    created_at TIMESTAMP NOT NULL
);

