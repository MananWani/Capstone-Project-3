package com.crimsonlogic.payrollmanagementsystem.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
class QueriesTest {

    private Queries query;

    @BeforeEach
    void setUp() {
        query = new Queries();
    }

    @Test
    void testGenerateId() {
        // Simulate the @PrePersist behavior
        query.generateId();
        assertNotNull(query.getQueryId());
        assertTrue(query.getQueryId().startsWith("QRY"));
    }

    @Test
    void testSettersAndGetters() {
        String queryDescription = "Query about leave balance";
        query.setQueryDescription(queryDescription);

        String queryStatus = "Open";
        query.setQueryStatus(queryStatus);

        String comment = "Need clarification on remaining leaves";
        query.setComment(comment);

        Employees employee = new Employees();
        employee.setEmployeeId("EMP123");
        query.setQueryByEmployee(employee);

        SalaryRecord salaryRecord = new SalaryRecord();
        salaryRecord.setSalaryRecordId("SAL001");
        query.setQueryForSalaryRecord(salaryRecord);

        assertEquals(queryDescription, query.getQueryDescription());
        assertEquals(queryStatus, query.getQueryStatus());
        assertEquals(comment, query.getComment());
        assertEquals(employee, query.getQueryByEmployee());
        assertEquals(salaryRecord, query.getQueryForSalaryRecord());
    }
}
