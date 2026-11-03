package com.example.restservice;

import java.util.List;

public record Employees(List<Employee> employees) {
    public Employees {
        employees = List.copyOf(employees);
    }
}
