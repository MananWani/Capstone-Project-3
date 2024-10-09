package com.crimsonlogic.payrollmanagementsystem.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AttendanceTest {

    private Attendance attendance;

    @BeforeEach
    void setUp() {
        attendance = new Attendance();
    }

    @Test
    void testGenerateId() {
        // Simulate the @PrePersist behavior
        attendance.generateId();
        assertNotNull(attendance.getAttendanceId());
        assertTrue(attendance.getAttendanceId().startsWith("ATD"));
    }

    @Test
    void testSettersAndGetters() {
        LocalDate attendanceDate = LocalDate.of(2024, 10, 8);
        attendance.setAttendanceForDate(attendanceDate);

        BigDecimal overtime = new BigDecimal("2.5");
        attendance.setOvertimeHours(overtime);

        BigDecimal total = new BigDecimal("8.0");
        attendance.setTotalHours(total);

        attendance.setStatus("Present");

        Employees employee = new Employees();
        employee.setEmployeeId("EMP123");
        attendance.setAttendanceByEmployee(employee);

        assertEquals(attendanceDate, attendance.getAttendanceForDate());
        assertEquals(overtime, attendance.getOvertimeHours());
        assertEquals(total, attendance.getTotalHours());
        assertEquals("Present", attendance.getStatus());
        assertEquals(employee, attendance.getAttendanceByEmployee());
    }
}
