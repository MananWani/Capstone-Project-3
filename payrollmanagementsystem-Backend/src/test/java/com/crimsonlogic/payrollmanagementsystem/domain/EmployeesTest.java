package com.crimsonlogic.payrollmanagementsystem.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EmployeesTest {

    private Employees employee;

    @BeforeEach
    void setUp() {
        employee = new Employees();
    }

    @Test
    void testGenerateId() {
        employee.generateId();
        assertNotNull(employee.getEmployeeId());
        assertTrue(employee.getEmployeeId().startsWith("EMP"));
    }

    @Test
    void testSettersAndGetters() {
        employee.setFullName("John Doe");
        employee.setMobileNumber("1234567890");

        Designation designation = new Designation();
        designation.setDesignationId("des1");
        employee.setDesignation(designation);

        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        LocalDate joiningDate = LocalDate.of(2020, 1, 1);
        employee.setDateOfBirth(birthDate);
        employee.setJoiningDate(joiningDate);

        Employees manager = new Employees();
        manager.setEmployeeId("EMP123");
        employee.setManager(manager);

        employee.setRating(5);
        employee.setIsActive(true);

        Users user = new Users();
        user.setUserId("USR123");
        employee.setUser(user);

        assertEquals("John Doe", employee.getFullName());
        assertEquals("1234567890", employee.getMobileNumber());
        assertEquals(designation, employee.getDesignation());
        assertEquals(birthDate, employee.getDateOfBirth());
        assertEquals(joiningDate, employee.getJoiningDate());
        assertEquals(manager, employee.getManager());
        assertEquals(5, employee.getRating());
        assertTrue(employee.getIsActive());
        assertEquals(user, employee.getUser());
    }
}
