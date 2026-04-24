package com.taskmanager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.dto.TaskDefinitionRequest;
import com.taskmanager.dto.TaskDefinitionResponse;
import com.taskmanager.dto.TaskExecutionRequest;
import com.taskmanager.dto.TaskExecutionResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TaskExecutionStage02Tests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateExecutionForTask() throws Exception {
        String createdTaskBody = mockMvc.perform(post("/api/tasks")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TaskDefinitionRequest("Stage 02 task", null))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TaskDefinitionResponse task = objectMapper.readValue(createdTaskBody, TaskDefinitionResponse.class);

        String createdExecutionBody = mockMvc.perform(post("/api/tasks/" + task.id() + "/executions")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TaskExecutionRequest("First run output"))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TaskExecutionResponse execution =
                objectMapper.readValue(createdExecutionBody, TaskExecutionResponse.class);

        assertThat(execution.id()).isNotNull();
        assertThat(execution.taskDefinitionId()).isEqualTo(task.id());
    }

    @Test
    void shouldListExecutionsByTaskId() throws Exception {
        String createdTaskBody = mockMvc.perform(post("/api/tasks")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TaskDefinitionRequest("Task with executions", null))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TaskDefinitionResponse task = objectMapper.readValue(createdTaskBody, TaskDefinitionResponse.class);

        mockMvc.perform(post("/api/tasks/" + task.id() + "/executions")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TaskExecutionRequest("Execution output"))))
                .andExpect(status().isCreated());

        String responseBody = mockMvc.perform(get("/api/tasks/" + task.id() + "/executions"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<TaskExecutionResponse> executions =
                objectMapper.readValue(responseBody, new TypeReference<List<TaskExecutionResponse>>() {
                });

        assertThat(executions).isNotEmpty();
        assertThat(executions.getFirst().taskDefinitionId()).isEqualTo(task.id());
    }
}
