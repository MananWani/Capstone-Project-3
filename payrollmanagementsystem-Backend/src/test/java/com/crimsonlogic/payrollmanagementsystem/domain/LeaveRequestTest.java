package com.crimsonlogic.payrollmanagementsystem.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LeaveRequestTest {

    private LeaveRequest leaveRequest;

    @BeforeEach
    void setUp() {
        leaveRequest = new LeaveRequest();
    }

    @Test
    void testGenerateId() {
        leaveRequest.generateId();
        assertNotNull(leaveRequest.getLeaveRequestId());
        assertTrue(leaveRequest.getLeaveRequestId().startsWith("REQ"));
    }

    @Test
    void testSettersAndGetters() {
        LocalDate startDate = LocalDate.of(2024, 10, 1);
        leaveRequest.setStartDate(startDate);

        String startHalf = "Morning";
        leaveRequest.setStartHalf(startHalf);

        LocalDate endDate = LocalDate.of(2024, 10, 5);
        leaveRequest.setEndDate(endDate);

        String endHalf = "Afternoon";
        leaveRequest.setEndHalf(endHalf);

        String status = "Approved";
        leaveRequest.setStatus(status);

        BigDecimal noOfDays = new BigDecimal("5");
        leaveRequest.setNoOfDays(noOfDays);

        String reason = "Family emergency";
        leaveRequest.setReason(reason);

        String description = "Need to attend a family event.";
        leaveRequest.setDescription(description);

        Employees employee = new Employees();
        employee.setEmployeeId("EMP123");
        leaveRequest.setRequestByEmployee(employee);

        LeaveType leaveType = new LeaveType();
        leaveType.setTypeId("TYPE01");
        leaveRequest.setTypeOfLeave(leaveType);

        assertEquals(startDate, leaveRequest.getStartDate());
        assertEquals(startHalf, leaveRequest.getStartHalf());
        assertEquals(endDate, leaveRequest.getEndDate());
        assertEquals(endHalf, leaveRequest.getEndHalf());
        assertEquals(status, leaveRequest.getStatus());
        assertEquals(noOfDays, leaveRequest.getNoOfDays());
        assertEquals(reason, leaveRequest.getReason());
        assertEquals(description, leaveRequest.getDescription());
        assertEquals(employee, leaveRequest.getRequestByEmployee());
        assertEquals(leaveType, leaveRequest.getTypeOfLeave());
    }
}

