package com.crimsonlogic.payrollmanagementsystem.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LeaveRecordTest {

    private LeaveRecord leaveRecord;

    @BeforeEach
    void setUp() {
        leaveRecord = new LeaveRecord();
    }

    @Test
    void testGenerateId() {
        leaveRecord.generateId();
        assertNotNull(leaveRecord.getLeaveRecordId());
        assertTrue(leaveRecord.getLeaveRecordId().startsWith("LEV"));
    }

    @Test
    void testSettersAndGetters() {
        Integer totalLeaves = 30;
        leaveRecord.setTotalLeaves(totalLeaves);

        BigDecimal usedLeaves = new BigDecimal("5");
        leaveRecord.setUsedLeaves(usedLeaves);

        BigDecimal remainingLeaves = new BigDecimal("25");
        leaveRecord.setRemainingLeaves(remainingLeaves);

        Employees employee = new Employees();
        employee.setEmployeeId("EMP123");
        leaveRecord.setLeaveForEmployee(employee);

        LeaveType leaveType = new LeaveType();
        leaveType.setTypeId("TYPE01");
        leaveRecord.setTypeOfLeave(leaveType);

        assertEquals(totalLeaves, leaveRecord.getTotalLeaves());
        assertEquals(usedLeaves, leaveRecord.getUsedLeaves());
        assertEquals(remainingLeaves, leaveRecord.getRemainingLeaves());
        assertEquals(employee, leaveRecord.getLeaveForEmployee());
        assertEquals(leaveType, leaveRecord.getTypeOfLeave());
    }
}
