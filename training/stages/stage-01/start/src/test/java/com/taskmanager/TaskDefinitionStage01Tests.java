package com.taskmanager;

import com.taskmanager.dto.ErrorResponse;
import com.taskmanager.dto.TaskDefinitionRequest;
import com.taskmanager.dto.TaskDefinitionResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TaskDefinitionStage01Tests {

    @Autowired
    private TestRestTemplate rest;

    @Test
    void shouldCreateTaskDefinition() {
        TaskDefinitionRequest request = new TaskDefinitionRequest("First task", "Stage 01 example");

        ResponseEntity<TaskDefinitionResponse> response =
                rest.postForEntity("/api/tasks", request, TaskDefinitionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("First task");
    }

    @Test
    void shouldListTaskDefinitions() {
        rest.postForEntity("/api/tasks", new TaskDefinitionRequest("Listed task", null), TaskDefinitionResponse.class);

        ResponseEntity<List<TaskDefinitionResponse>> response = rest.exchange(
                "/api/tasks",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    void shouldReturn400ForBlankName() {
        ResponseEntity<ErrorResponse> response =
                rest.postForEntity("/api/tasks", new TaskDefinitionRequest("", null), ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}

