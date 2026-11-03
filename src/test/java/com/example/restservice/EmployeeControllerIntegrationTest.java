package com.example.restservice;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void listsSeedEmployees() throws Exception {
        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employees[0].employee_id").value("1"))
                .andExpect(jsonPath("$.employees[0].first_name").value("Avery"));
    }

    @Test
    void createsReadsUpdatesAndDeletesAnEmployee() throws Exception {
        String employeeId = "new-" + UUID.randomUUID();
        String email = employeeId + "@example.com";
        String request = """
                {
                  "employee_id": "%s",
                  "first_name": "Taylor",
                  "last_name": "Reed",
                  "email": "%s",
                  "title": "Platform Engineer"
                }
                """.formatted(employeeId, email);

        MvcResult createResult = mockMvc.perform(post("/employees")
                        .contentType(MediaType.parseMediaType("application/json"))
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.employee_id").value(employeeId))
                .andExpect(jsonPath("$.first_name").value("Taylor"))
                .andReturn();

        String createdEmployeeId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("employee_id")
                .asText();
        String employeeUrl = "/employees/" + createdEmployeeId;
        mockMvc.perform(get(employeeUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));
        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employees[?(@.employee_id == '%s')].email".formatted(employeeId))
                        .value(email));

        String updateRequest = """
                {
                  "employee_id": "%s",
                  "first_name": "Taylor",
                  "last_name": "Reed",
                  "email": "%s",
                  "title": "Senior Platform Engineer"
                }
                """.formatted(employeeId, email);
        mockMvc.perform(put(employeeUrl)
                        .contentType(MediaType.parseMediaType("application/json"))
                        .content(updateRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Senior Platform Engineer"));

        mockMvc.perform(delete(employeeUrl))
                .andExpect(status().isNoContent());
        mockMvc.perform(get(employeeUrl))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").exists());
    }

    @Test
    void rejectsInvalidAndDuplicateEmployeeRequests() throws Exception {
        mockMvc.perform(post("/employees")
                        .contentType(MediaType.parseMediaType("application/json"))
                        .content("""
                                {
                                  "first_name": "",
                                  "last_name": "Reed",
                                  "email": "not-an-email",
                                  "title": "Engineer"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    String detail = objectMapper.readTree(result.getResponse().getContentAsString())
                            .path("detail")
                            .asText();
                    assertTrue(detail.contains("firstName"));
                    assertTrue(detail.contains("email"));
                });

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.parseMediaType("application/json"))
                        .content("""
                                {
                                  "first_name": "Avery",
                                  "last_name": "Morgan",
                                  "email": "AVERY.MORGAN@example.com",
                                  "title": "Engineer"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").exists());

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.parseMediaType("application/json"))
                        .content("""
                                {
                                  "employee_id": "1",
                                  "first_name": "Taylor",
                                  "last_name": "Reed",
                                  "email": "taylor.reed@example.com",
                                  "title": "Engineer"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").exists());
    }
}
