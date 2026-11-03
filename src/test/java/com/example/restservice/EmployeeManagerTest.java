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

        assertEquals(4, employees.employees().size());
        assertEquals("1", employees.employees().get(0).employeeId());
        assertEquals("Avery", employees.employees().get(0).firstName());
    }

    @Test
    void createsEmployeeWithProvidedIdAndTrimmedValues() {
        Employee employee = employeeManager.createEmployee(
                new EmployeeRequest("team-42", "  Sam ", " Rivera  ", "sam.rivera@example.com", "Engineer")
        );

        assertEquals("team-42", employee.employeeId());
        assertEquals("Sam", employee.firstName());
        assertEquals("Rivera", employee.lastName());
        assertEquals(employee, employeeManager.getEmployee("team-42"));
        assertEquals(5, employeeManager.getEmployees().employees().size());
    }

    @Test
    void assignsNextAvailableIdWhenRequestOmitsIt() {
        Employee employee = employeeManager.createEmployee(
                new EmployeeRequest(null, "Sam", "Rivera", "sam.rivera@example.com", "Engineer")
        );

        assertEquals("5", employee.employeeId());
    }

    @Test
    void trimsProvidedIdsBeforeCheckingForConflicts() {
        Employee employee = employeeManager.createEmployee(
                new EmployeeRequest(" 5 ", "Sam", "Rivera", "sam.rivera@example.com", "Engineer")
        );
        Employee nextEmployee = employeeManager.createEmployee(
                new EmployeeRequest(null, "Dana", "Kim", "dana.kim@example.com", "Designer")
        );

        assertEquals("5", employee.employeeId());
        assertEquals("6", nextEmployee.employeeId());
    }

    @Test
    void rejectsDuplicateEmployeeIdsAndEmails() {
        assertThrows(DuplicateEmployeeIdException.class, () -> employeeManager.createEmployee(
                new EmployeeRequest("1", "Sam", "Rivera", "sam.rivera@example.com", "Engineer")
        ));
        assertThrows(DuplicateEmployeeEmailException.class, () -> employeeManager.createEmployee(
                new EmployeeRequest("team-42", "Sam", "Rivera", "AVERY.MORGAN@example.com", "Engineer")
        ));
        assertThrows(DuplicateEmployeeEmailException.class, () -> employeeManager.updateEmployee(
                "1",
                new EmployeeRequest("1", "Avery", "Morgan", "JORDAN.LEE@example.com", "Engineer")
        ));
        assertEquals("avery.morgan@example.com", employeeManager.getEmployee("1").email());
    }

    @Test
    void updatesAndDeletesEmployee() {
        Employee updated = employeeManager.updateEmployee(
                "1",
                new EmployeeRequest("1", "Avery", "Morgan", "avery.morgan@example.com", "Principal Engineer")
        );

        assertEquals("Principal Engineer", updated.title());
        assertEquals("Principal Engineer", employeeManager.getEmployee("1").title());

        employeeManager.deleteEmployee("1");

        assertEquals(3, employeeManager.getEmployees().employees().size());
        assertThrows(EmployeeNotFoundException.class, () -> employeeManager.getEmployee("1"));
    }

    @Test
    void returnsAnImmutableEmployeeList() {
        Employees employees = employeeManager.getEmployees();

        assertThrows(UnsupportedOperationException.class, () -> employees.employees().clear());
        assertEquals(4, employeeManager.getEmployees().employees().size());
    }

    @Test
    void reportsMissingEmployee() {
        assertThrows(EmployeeNotFoundException.class, () -> employeeManager.getEmployee("missing"));
        assertThrows(EmployeeNotFoundException.class, () -> employeeManager.deleteEmployee("missing"));
    }
}
