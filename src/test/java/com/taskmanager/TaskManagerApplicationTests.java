package com.taskmanager;

import com.taskmanager.dto.*;
import com.taskmanager.entity.ExecutionStatus;
import com.taskmanager.exception.ErrorResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for the Task Orchestration Engine.
 *
 * <p>Uses a real PostgreSQL instance started automatically via Spring Boot Docker Compose support
 * ({@code compose-test.yaml}). Flyway runs all migrations, guaranteeing the same schema as production.</p>
 *
 * <p>Tests are ordered and share instance state (via {@link TestInstance.Lifecycle#PER_CLASS})
 * to reduce container startup overhead while keeping the flow readable end-to-end.</p>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TaskManagerApplicationTests {

    @Autowired
    private TestRestTemplate rest;

    // Instance fields — safe with @TestInstance(PER_CLASS)
    private Long createdDefinitionId;
    private Long createdExecutionId;

    // ── Task Definition CRUD ─────────────────────────────────────────────

    @Test
    @Order(1)
    void shouldCreateTaskDefinition() {
        var req = new TaskDefinitionRequest(
                "List home directory",
                "Runs echo hello world",
                "SHELL_COMMAND",
                "{\"command\": \"echo hello world\", \"workDir\": \"/tmp\"}",
                null, true, 2, 5, 60, 1L
        );

        ResponseEntity<TaskDefinitionResponse> response =
                rest.postForEntity("/api/tasks", req, TaskDefinitionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("List home directory");
        assertThat(response.getBody().taskType()).isEqualTo("SHELL_COMMAND");
        assertThat(response.getBody().enabled()).isTrue();
        assertThat(response.getBody().maxRetries()).isEqualTo(2);

        createdDefinitionId = response.getBody().id();
    }

    @Test
    @Order(2)
    void shouldGetTaskDefinitionById() {
        ResponseEntity<TaskDefinitionResponse> response =
                rest.getForEntity("/api/tasks/" + createdDefinitionId, TaskDefinitionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().id()).isEqualTo(createdDefinitionId);
        assertThat(response.getBody().taskType()).isEqualTo("SHELL_COMMAND");
    }

    @Test
    @Order(3)
    void shouldReturn404ForMissingDefinition() {
        ResponseEntity<ErrorResponse> response =
                rest.getForEntity("/api/tasks/99999", ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(4)
    void shouldUpdateTaskDefinition() {
        var req = new TaskDefinitionRequest(
                "Updated task name", "Updated description",
                "SHELL_COMMAND", "{\"command\": \"echo updated\"}",
                null, true, 3, 10, 120, 1L
        );

        HttpEntity<TaskDefinitionRequest> entity = new HttpEntity<>(req);
        ResponseEntity<TaskDefinitionResponse> response =
                rest.exchange("/api/tasks/" + createdDefinitionId, HttpMethod.PUT, entity,
                        TaskDefinitionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().name()).isEqualTo("Updated task name");
        assertThat(response.getBody().maxRetries()).isEqualTo(3);
    }

    @Test
    @Order(5)
    void shouldToggleEnabled() {
        ResponseEntity<TaskDefinitionResponse> response = rest.exchange(
                "/api/tasks/" + createdDefinitionId + "/enabled?enabled=false",
                HttpMethod.PATCH, null, TaskDefinitionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().enabled()).isFalse();

        // Re-enable for subsequent tests
        rest.exchange("/api/tasks/" + createdDefinitionId + "/enabled?enabled=true",
                HttpMethod.PATCH, null, TaskDefinitionResponse.class);
    }

    @Test
    @Order(6)
    void shouldRejectRunningDisabledTask() {
        // Disable first
        rest.exchange("/api/tasks/" + createdDefinitionId + "/enabled?enabled=false",
                HttpMethod.PATCH, null, TaskDefinitionResponse.class);

        ResponseEntity<ErrorResponse> response = rest.exchange(
                "/api/tasks/" + createdDefinitionId + "/run",
                HttpMethod.POST, null, ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        // Re-enable
        rest.exchange("/api/tasks/" + createdDefinitionId + "/enabled?enabled=true",
                HttpMethod.PATCH, null, TaskDefinitionResponse.class);
    }

    // ── Task Execution ───────────────────────────────────────────────────

    @Test
    @Order(7)
    void shouldRunTaskAndCreateExecution() throws InterruptedException {
        ResponseEntity<TaskExecutionResponse> response = rest.exchange(
                "/api/tasks/" + createdDefinitionId + "/run",
                HttpMethod.POST, null, TaskExecutionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody().id()).isNotNull();
        assertThat(response.getBody().taskDefinitionId()).isEqualTo(createdDefinitionId);

        createdExecutionId = response.getBody().id();

        Thread.sleep(3000);
    }

    @Test
    @Order(8)
    void shouldGetExecutionResult() {
        ResponseEntity<TaskExecutionResponse> response =
                rest.getForEntity("/api/executions/" + createdExecutionId, TaskExecutionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().status()).isEqualTo(ExecutionStatus.COMPLETED);
        assertThat(response.getBody().output()).contains("updated");
    }

    @Test
    @Order(9)
    void shouldGetExecutionHistory() {
        ResponseEntity<PagedResponse<TaskExecutionResponse>> response = rest.exchange(
                "/api/tasks/" + createdDefinitionId + "/executions",
                HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().content()).isNotEmpty();
        assertThat(response.getBody().content().getFirst().taskDefinitionId()).isEqualTo(createdDefinitionId);
    }

    // ── List & Filter ────────────────────────────────────────────────────

    @Test
    @Order(10)
    void shouldListDefinitionsWithPagination() {
        ResponseEntity<PagedResponse<TaskDefinitionResponse>> response = rest.exchange(
                "/api/tasks?page=0&size=10",
                HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().content()).isNotEmpty();
        assertThat(response.getBody().page()).isEqualTo(0);
    }

    @Test
    @Order(11)
    void shouldFilterDefinitionsByType() {
        ResponseEntity<PagedResponse<TaskDefinitionResponse>> response = rest.exchange(
                "/api/tasks?type=SHELL_COMMAND",
                HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        response.getBody().content()
                .forEach(t -> assertThat(t.taskType()).isEqualTo("SHELL_COMMAND"));
    }

    @Test
    @Order(12)
    void shouldGetDefinitionsByUser() {
        ResponseEntity<PagedResponse<TaskDefinitionResponse>> response = rest.exchange(
                "/api/tasks/user/1",
                HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        response.getBody().content()
                .forEach(t -> assertThat(t.userId()).isEqualTo(1L));
    }

    @Test
    @Order(13)
    void shouldFilterExecutionsByStatus() {
        ResponseEntity<PagedResponse<TaskExecutionResponse>> response = rest.exchange(
                "/api/executions?status=COMPLETED",
                HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        response.getBody().content()
                .forEach(e -> assertThat(e.status()).isEqualTo(ExecutionStatus.COMPLETED));
    }

    // ── Executor Types ───────────────────────────────────────────────────

    @Test
    @Order(14)
    void shouldListAvailableExecutorTypes() {
        ResponseEntity<Set<String>> response = rest.exchange(
                "/api/tasks/types",
                HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactlyInAnyOrder(
                "SHELL_COMMAND", "HTTP_REQUEST", "TELEGRAM_MESSAGE", "FILE_ORGANIZER"
        );
    }

    // ── Orchestrator Status ──────────────────────────────────────────────

    @Test
    @Order(15)
    void shouldReturnOrchestratorStatus() {
        ResponseEntity<OrchestratorStatusResponse> response = rest.exchange(
                "/api/orchestrator/status",
                HttpMethod.GET, null, OrchestratorStatusResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().status()).isEqualTo("ACTIVE");
        assertThat(response.getBody().runningTasks()).isGreaterThanOrEqualTo(0);
    }

    // ── HTTP Request Executor ────────────────────────────────────────────

    @Test
    @Order(16)
    void shouldCreateAndRunHttpTask() throws InterruptedException {
        var req = new TaskDefinitionRequest(
                "Ping httpbin", "Test HTTP executor",
                "HTTP_REQUEST", "{\"url\": \"https://httpbin.org/get\", \"method\": \"GET\"}",
                null, true, 0, 60, 30, 1L
        );

        ResponseEntity<TaskDefinitionResponse> createResp =
                rest.postForEntity("/api/tasks", req, TaskDefinitionResponse.class);
        assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<TaskExecutionResponse> runResp = rest.exchange(
                "/api/tasks/" + createResp.getBody().id() + "/run",
                HttpMethod.POST, null, TaskExecutionResponse.class);

        assertThat(runResp.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

        Thread.sleep(5000);

        ResponseEntity<TaskExecutionResponse> execResp =
                rest.getForEntity("/api/executions/" + runResp.getBody().id(), TaskExecutionResponse.class);

        assertThat(execResp.getBody().status()).isIn(ExecutionStatus.COMPLETED, ExecutionStatus.FAILED);
    }

    // ── Cancel ───────────────────────────────────────────────────────────

    @Test
    @Order(17)
    void shouldCancelExecution() {
        ResponseEntity<CancelExecutionResponse> response = rest.exchange(
                "/api/executions/" + createdExecutionId + "/cancel",
                HttpMethod.POST, null, CancelExecutionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().executionId()).isEqualTo(createdExecutionId);
        // Already completed, so cancelled=false is valid
        assertThat(response.getBody()).isNotNull();
    }

    // ── Validation ───────────────────────────────────────────────────────

    @Test
    @Order(18)
    void shouldRejectBlankName() {
        var req = new TaskDefinitionRequest("", null, "SHELL_COMMAND",
                "{\"command\":\"echo hi\"}", null, true, 0, 60, 60, 1L);

        ResponseEntity<ErrorResponse> response =
                rest.postForEntity("/api/tasks", req, ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @Order(19)
    void shouldRejectInvalidCronExpression() {
        var req = new TaskDefinitionRequest("Bad cron", null, "SHELL_COMMAND",
                "{\"command\":\"echo hi\"}", "not-a-cron", true, 0, 60, 60, 1L);

        ResponseEntity<ErrorResponse> response =
                rest.postForEntity("/api/tasks", req, ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @Order(20)
    void shouldRejectInvalidConfig() {
        var req = new TaskDefinitionRequest("Missing command", null, "SHELL_COMMAND",
                "{}", null, true, 0, 60, 60, 1L);

        ResponseEntity<ErrorResponse> response =
                rest.postForEntity("/api/tasks", req, ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    // ── Delete ───────────────────────────────────────────────────────────

    @Test
    @Order(99)
    void shouldDeleteDefinition() {
        rest.delete("/api/tasks/" + createdDefinitionId);

        ResponseEntity<ErrorResponse> response =
                rest.getForEntity("/api/tasks/" + createdDefinitionId, ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
