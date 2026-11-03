package com.example.restservice;

public class DuplicateEmployeeIdException extends RuntimeException {
    public DuplicateEmployeeIdException(String employeeId) {
        super("An employee with ID " + employeeId + " already exists");
    }
}
