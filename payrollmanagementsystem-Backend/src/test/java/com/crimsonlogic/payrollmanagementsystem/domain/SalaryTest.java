package com.crimsonlogic.payrollmanagementsystem.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SalaryTest {

    private Salary salary;

    @BeforeEach
    void setUp() {
        salary = new Salary();
    }

    @Test
    void testGenerateId() {
        // Simulate the @PrePersist behavior
        salary.generateId();
        assertNotNull(salary.getSalaryId());
        assertTrue(salary.getSalaryId().startsWith("SLY"));
    }

    @Test
    void testSettersAndGetters() {
        BigDecimal costToCompany = new BigDecimal("50000.00");
        salary.setCostToCompany(costToCompany);

        Employees employee = new Employees();
        employee.setEmployeeId("EMP123");
        salary.setSalaryOfEmployee(employee);

        assertEquals(costToCompany, salary.getCostToCompany());
        assertEquals(employee, salary.getSalaryOfEmployee());
    }
}

