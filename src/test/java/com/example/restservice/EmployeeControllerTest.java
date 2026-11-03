package com.example.restservice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EmployeeManager employeeManager;

    @Test
    void getsAllEmployees() throws Exception {
        when(employeeManager.getEmployees()).thenReturn(new Employees(List.of(
                new Employee("team-42", "Sam", "Rivera", "sam.rivera@example.com", "Engineer")
        )));

        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employees[0].employee_id").value("team-42"))
                .andExpect(jsonPath("$.employees[0].first_name").value("Sam"))
                .andExpect(jsonPath("$.employees[0].employeeId").doesNotExist());

        verify(employeeManager).getEmployees();
    }

    @Test
    void postsEmployeeJson() throws Exception {
        Employee employee = new Employee(
                "team-42", "Sam", "Rivera", "sam.rivera@example.com", "Engineer"
        );
        when(employeeManager.createEmployee(any(EmployeeRequest.class))).thenReturn(employee);
        String request = """
                {
                  "employee_id": "team-42",
                  "first_name": "Sam",
                  "last_name": "Rivera",
                  "email": "sam.rivera@example.com",
                  "title": "Engineer"
                }
                """;

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/employees/team-42"))
                .andExpect(jsonPath("$.employee_id").value("team-42"))
                .andExpect(jsonPath("$.first_name").value("Sam"))
                .andExpect(jsonPath("$.last_name").value("Rivera"))
                .andExpect(jsonPath("$.email").value("sam.rivera@example.com"));

        verify(employeeManager).createEmployee(objectMapper.readValue(request, EmployeeRequest.class));
    }
}
