package com.example.restservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EmployeeManagerTest {
    private EmployeeManager employeeManager;

    @BeforeEach
    void setUp() {
        employeeManager = new EmployeeManager();
    }

    @Test
    void returnsTheFourSeedEmployees() {
        Employees employees = employeeManager.getEmployees();

        assertEquals(4, employees.getEmployees().size());
        assertEquals("1", employees.getEmployees().get(0).getEmployee_id());
        assertEquals("Avery", employees.getEmployees().get(0).getFirst_name());
    }

    @Test
    void createsEmployeeWithProvidedIdAndTrimmedValues() {
        Employee employee = employeeManager.createEmployee(
                new EmployeeRequest("team-42", "  Sam ", " Rivera  ", "sam.rivera@example.com", "Engineer")
        );

        assertEquals("team-42", employee.getEmployee_id());
        assertEquals("Sam", employee.getFirst_name());
        assertEquals("Rivera", employee.getLast_name());
        assertEquals(employee, employeeManager.getEmployee("team-42"));
        assertEquals(5, employeeManager.getEmployees().getEmployees().size());
    }

    @Test
    void assignsNextAvailableIdWhenRequestOmitsIt() {
        Employee employee = employeeManager.createEmployee(
                new EmployeeRequest(null, "Sam", "Rivera", "sam.rivera@example.com", "Engineer")
        );

        assertEquals("5", employee.getEmployee_id());
    }

    @Test
    void rejectsDuplicateEmployeeIdsAndEmails() {
        assertThrows(DuplicateEmployeeIdException.class, () -> employeeManager.createEmployee(
                new EmployeeRequest("1", "Sam", "Rivera", "sam.rivera@example.com", "Engineer")
        ));
        assertThrows(DuplicateEmployeeEmailException.class, () -> employeeManager.createEmployee(
                new EmployeeRequest("team-42", "Sam", "Rivera", "AVERY.MORGAN@example.com", "Engineer")
        ));
    }

    @Test
    void updatesAndDeletesEmployee() {
        Employee updated = employeeManager.updateEmployee(
                "1",
                new EmployeeRequest("1", "Avery", "Morgan", "avery.morgan@example.com", "Principal Engineer")
        );

        assertEquals("Principal Engineer", updated.getTitle());
        assertEquals("Principal Engineer", employeeManager.getEmployee("1").getTitle());

        employeeManager.deleteEmployee("1");

        assertEquals(3, employeeManager.getEmployees().getEmployees().size());
        assertThrows(EmployeeNotFoundException.class, () -> employeeManager.getEmployee("1"));
    }

    @Test
    void reportsMissingEmployee() {
        assertThrows(EmployeeNotFoundException.class, () -> employeeManager.getEmployee("missing"));
        assertThrows(EmployeeNotFoundException.class, () -> employeeManager.deleteEmployee("missing"));
    }
}
