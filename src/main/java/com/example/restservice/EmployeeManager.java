package com.example.restservice;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class EmployeeManager {
    private final List<Employee> employees = new ArrayList<>(List.of(
            new Employee(1, "Avery", "Morgan", "avery.morgan@example.com", "Software Engineer"),
            new Employee(2, "Jordan", "Lee", "jordan.lee@example.com", "Product Manager"),
            new Employee(3, "Riley", "Patel", "riley.patel@example.com", "UX Designer"),
            new Employee(4, "Casey", "Nguyen", "casey.nguyen@example.com", "Data Analyst")
    ));
    private int nextEmployeeId = 5;

    public synchronized Employees getEmployees() {
        return new Employees(employees);
    }

    public synchronized Employee getEmployee(String employeeId) {
        return employees.stream()
                .filter(employee -> employee.employeeId().equals(employeeId))
                .findFirst()
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
    }

    public synchronized Employee createEmployee(EmployeeRequest request) {
        String employeeId = normalizeEmployeeId(request.employeeId());
        String email = request.email().trim();
        ensureEmailIsAvailable(email, null);
        if (employeeId == null) {
            employeeId = nextAvailableEmployeeId();
        } else {
            ensureEmployeeIdIsAvailable(employeeId);
        }
        Employee employee = new Employee(
                employeeId,
                request.firstName().trim(),
                request.lastName().trim(),
                email,
                request.title().trim()
        );
        employees.add(employee);
        return employee;
    }

    public synchronized Employee updateEmployee(String employeeId, EmployeeRequest request) {
        Employee existing = getEmployee(employeeId);
        String email = request.email().trim();
        ensureEmailIsAvailable(email, employeeId);
        Employee employee = new Employee(
                employeeId,
                request.firstName().trim(),
                request.lastName().trim(),
                email,
                request.title().trim()
        );
        employees.set(employees.indexOf(existing), employee);
        return employee;
    }

    public synchronized void deleteEmployee(String employeeId) {
        boolean removed = employees.removeIf(employee -> employee.employeeId().equals(employeeId));
        if (!removed) {
            throw new EmployeeNotFoundException(employeeId);
        }
    }

    private static String normalizeEmployeeId(String employeeId) {
        if (employeeId == null || employeeId.isBlank()) {
            return null;
        }
        return employeeId.trim();
    }

    private String nextAvailableEmployeeId() {
        while (employees.stream()
                .anyMatch(employee -> employee.employeeId().equals(Integer.toString(nextEmployeeId)))) {
            nextEmployeeId++;
        }
        return Integer.toString(nextEmployeeId++);
    }

    private void ensureEmployeeIdIsAvailable(String employeeId) {
        boolean alreadyUsed = employees.stream()
                .anyMatch(employee -> employee.employeeId().equals(employeeId));
        if (alreadyUsed) {
            throw new DuplicateEmployeeIdException(employeeId);
        }
    }

    private void ensureEmailIsAvailable(String email, String excludedEmployeeId) {
        boolean alreadyUsed = employees.stream()
                .anyMatch(employee -> !employee.employeeId().equals(excludedEmployeeId)
                        && employee.email().equalsIgnoreCase(email));
        if (alreadyUsed) {
            throw new DuplicateEmployeeEmailException(email);
        }
    }
}
