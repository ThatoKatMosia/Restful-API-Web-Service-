package com.example.restservice;

public class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException(String employeeId) {
        super("Employee " + employeeId + " was not found");
    }
}
