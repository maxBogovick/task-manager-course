package com.taskmanager;

import com.taskmanager.dto.ErrorResponse;
import com.taskmanager.dto.TaskDefinitionRequest;
import com.taskmanager.dto.TaskDefinitionResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TaskDefinitionStage01Tests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateTaskDefinition() throws Exception {
        TaskDefinitionRequest request = new TaskDefinitionRequest("First task", "Stage 01 example");

        String body = mockMvc.perform(post("/api/tasks")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TaskDefinitionResponse response = objectMapper.readValue(body, TaskDefinitionResponse.class);

        assertThat(response.id()).isNotNull();
        assertThat(response.name()).isEqualTo("First task");
    }

    @Test
    void shouldListTaskDefinitions() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TaskDefinitionRequest("Listed task", null))))
                .andExpect(status().isCreated());

        String body = mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<TaskDefinitionResponse> response =
                objectMapper.readValue(body, new TypeReference<List<TaskDefinitionResponse>>() {
                });

        assertThat(response).isNotEmpty();
    }

    @Test
    void shouldGetTaskDefinitionById() throws Exception {
        String createdBody = mockMvc.perform(post("/api/tasks")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TaskDefinitionRequest("Fetch me", "Stored in DB"))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TaskDefinitionResponse created = objectMapper.readValue(createdBody, TaskDefinitionResponse.class);

        String responseBody = mockMvc.perform(get("/api/tasks/" + created.id()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TaskDefinitionResponse response = objectMapper.readValue(responseBody, TaskDefinitionResponse.class);

        assertThat(response.id()).isEqualTo(created.id());
        assertThat(response.name()).isEqualTo("Fetch me");
    }

    @Test
    void shouldReturn404ForMissingTask() throws Exception {
        String body = mockMvc.perform(get("/api/tasks/99999"))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse response = objectMapper.readValue(body, ErrorResponse.class);

        assertThat(response.message()).contains("99999");
    }

    @Test
    void shouldReturn400ForBlankName() throws Exception {
        String body = mockMvc.perform(post("/api/tasks")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TaskDefinitionRequest("", null))))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse response = objectMapper.readValue(body, ErrorResponse.class);
        assertThat(response.message()).isNotBlank();
    }
}
