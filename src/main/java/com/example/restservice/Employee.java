package com.example.restservice;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Employee(
        @JsonProperty("employee_id") String employeeId,
        @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName,
        String email,
        String title
) {
    public Employee(int employeeId, String firstName, String lastName, String email, String title) {
        this(Integer.toString(employeeId), firstName, lastName, email, title);
    }
}
