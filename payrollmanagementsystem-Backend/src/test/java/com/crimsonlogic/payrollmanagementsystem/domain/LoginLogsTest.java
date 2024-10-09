package com.crimsonlogic.payrollmanagementsystem.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LoginLogsTest {

    private LoginLogs loginLog;

    @BeforeEach
    void setUp() {
        loginLog = new LoginLogs();
    }

    @Test
    void testGenerateId() {
        loginLog.generateId();
        assertNotNull(loginLog.getLogId());
        assertTrue(loginLog.getLogId().startsWith("LOG"));
    }

    @Test
    void testSettersAndGetters() {
        Timestamp loginTime = new Timestamp(System.currentTimeMillis());
        loginLog.setLoginTime(loginTime);

        Timestamp logoutTime = new Timestamp(System.currentTimeMillis() + 3600000); // 1 hour later
        loginLog.setLogoutTime(logoutTime);

        Employees employee = new Employees();
        employee.setEmployeeId("EMP123");
        loginLog.setLogForEmployee(employee);

        assertEquals(loginTime, loginLog.getLoginTime());
        assertEquals(logoutTime, loginLog.getLogoutTime());
        assertEquals(employee, loginLog.getLogForEmployee());
    }
}

