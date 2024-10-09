package com.crimsonlogic.payrollmanagementsystem.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LeaveTypeTest {

    private LeaveType leaveType;

    @BeforeEach
    void setUp() {
        leaveType = new LeaveType();
    }

    @Test
    void testGenerateId() {
        leaveType.generateId();
        assertNotNull(leaveType.getTypeId());
        assertTrue(leaveType.getTypeId().startsWith("TYP"));
    }

    @Test
    void testSettersAndGetters() {
        String typeName = "Annual Leave";
        leaveType.setTypeName(typeName);

        Integer numberOfLeaves = 20;
        leaveType.setNumberOfLeaves(numberOfLeaves);

        assertEquals(typeName, leaveType.getTypeName());
        assertEquals(numberOfLeaves, leaveType.getNumberOfLeaves());
    }
}

