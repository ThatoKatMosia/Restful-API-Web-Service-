package com.example.restservice;

import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

@Component
public class EmployeeManager {
    private final Employees employees;
    private int nextEmployeeId = 5;

    public EmployeeManager() {
        this.employees = new Employees(List.of(
                new Employee(1, "Avery", "Morgan", "avery.morgan@example.com", "Software Engineer"),
                new Employee(2, "Jordan", "Lee", "jordan.lee@example.com", "Product Manager"),
                new Employee(3, "Riley", "Patel", "riley.patel@example.com", "UX Designer"),
                new Employee(4, "Casey", "Nguyen", "casey.nguyen@example.com", "Data Analyst")
        ));
    }

    public synchronized Employees getEmployees() {
        return new Employees(employees.getEmployees());
    }

    public synchronized Employee getEmployee(String employeeId) {
        Employee employee = employees.getEmployees().stream()
                .filter(candidate -> candidate.getEmployee_id().equals(employeeId))
                .findFirst()
                .orElse(null);
        if (employee == null) {
            throw new EmployeeNotFoundException(employeeId);
        }
        return employee;
    }

    public synchronized Employee createEmployee(EmployeeRequest request) {
        ensureEmailIsAvailable(request.email(), null);
        String employeeId = request.employeeId();
        if (employeeId == null || employeeId.isBlank()) {
            employeeId = nextAvailableEmployeeId();
        } else {
            ensureEmployeeIdIsAvailable(employeeId);
        }
        Employee employee = new Employee(
                employeeId,
                request.firstName().trim(),
                request.lastName().trim(),
                request.email().trim(),
                request.title().trim()
        );
        employees.getEmployees().add(employee);
        return employee;
    }

    public synchronized Employee updateEmployee(String employeeId, EmployeeRequest request) {
        Employee existing = getEmployee(employeeId);
        ensureEmailIsAvailable(request.email(), employeeId);
        Employee employee = new Employee(
                employeeId,
                request.firstName().trim(),
                request.lastName().trim(),
                request.email().trim(),
                request.title().trim()
        );
        List<Employee> employeeList = employees.getEmployees();
        employeeList.set(employeeList.indexOf(existing), employee);
        return employee;
    }

    public synchronized void deleteEmployee(String employeeId) {
        boolean removed = employees.getEmployees().removeIf(employee -> employee.getEmployee_id().equals(employeeId));
        if (!removed) {
            throw new EmployeeNotFoundException(employeeId);
        }
    }

    private String nextAvailableEmployeeId() {
        while (employees.getEmployees().stream()
                .anyMatch(employee -> employee.getEmployee_id().equals(Integer.toString(nextEmployeeId)))) {
            nextEmployeeId++;
        }
        return Integer.toString(nextEmployeeId++);
    }

    private void ensureEmployeeIdIsAvailable(String employeeId) {
        boolean alreadyUsed = employees.getEmployees().stream()
                .anyMatch(employee -> employee.getEmployee_id().equals(employeeId));
        if (alreadyUsed) {
            throw new DuplicateEmployeeIdException(employeeId);
        }
    }

    private void ensureEmailIsAvailable(String email, String excludedEmployeeId) {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        boolean alreadyUsed = employees.getEmployees().stream()
                .anyMatch(employee -> !employee.getEmployee_id().equals(excludedEmployeeId)
                        && employee.getEmail().toLowerCase(Locale.ROOT).equals(normalizedEmail));
        if (alreadyUsed) {
            throw new DuplicateEmployeeEmailException(email);
        }
    }
}
