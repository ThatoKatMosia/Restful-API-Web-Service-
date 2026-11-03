package com.example.restservice;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmployeeRequest(
        @JsonProperty("employee_id")
        @JsonAlias("employeeId")
        String employeeId,
        @JsonProperty("first_name")
        @JsonAlias("firstName")
        @NotBlank(message = "First name is required")
        String firstName,
        @JsonProperty("last_name")
        @JsonAlias("lastName")
        @NotBlank(message = "Last name is required")
        String lastName,
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,
        @NotBlank(message = "Title is required")
        String title
) {
}
