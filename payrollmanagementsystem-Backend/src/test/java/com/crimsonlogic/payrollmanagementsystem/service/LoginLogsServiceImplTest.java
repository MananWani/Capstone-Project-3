package com.crimsonlogic.payrollmanagementsystem.service;


import com.crimsonlogic.payrollmanagementsystem.domain.Employees;
import com.crimsonlogic.payrollmanagementsystem.domain.LoginLogs;
import com.crimsonlogic.payrollmanagementsystem.dto.LoginLogsDTO;
import com.crimsonlogic.payrollmanagementsystem.repository.EmployeesRepository;
import com.crimsonlogic.payrollmanagementsystem.repository.LoginLogsRepository;
import com.crimsonlogic.payrollmanagementsystem.serviceimpl.LoginLogsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class LoginLogsServiceImplTest {

    @InjectMocks
    private LoginLogsServiceImpl loginLogsService;

    @Mock
    private LoginLogsRepository loginLogsRepository;

    @Mock
    private EmployeesRepository employeesRepository;

    private Employees employee;

    private LoginLogs loginLog;

    @BeforeEach
    void setUp() {

        employee = new Employees();
        employee.setEmployeeId("emp1");

        loginLog = new LoginLogs();
        loginLog.setLogId("log1");
        loginLog.setLogForEmployee(employee);
        loginLog.setLoginTime(Timestamp.valueOf(LocalDateTime.now()));
    }

    @Test
    void testSetLoginLog() {
        when(loginLogsRepository.save(any(LoginLogs.class))).thenReturn(loginLog);

        loginLogsService.setLoginLog(employee);

        assertThat(loginLog.getLogId()).isEqualTo("log1");
        verify(loginLogsRepository).save(any(LoginLogs.class));
    }

    @Test
    void testSetLogoutLog_Success() {
        when(loginLogsRepository.findById("log1")).thenReturn(Optional.of(loginLog));
        when(loginLogsRepository.save(any(LoginLogs.class))).thenReturn(loginLog);

        loginLogsService.setLogoutLog("log1");

        assertThat(loginLog.getLogoutTime()).isNotNull();
        verify(loginLogsRepository).save(any(LoginLogs.class));
    }

    @Test
    void testSetLogoutLog_Failure() {
        when(loginLogsRepository.findById("log1")).thenReturn(Optional.empty());

        loginLogsService.setLogoutLog("log1");

        verify(loginLogsRepository, never()).save(any(LoginLogs.class));
    }

    @Test
    void testGetLogsForEmployee() {
        when(employeesRepository.findById(employee.getEmployeeId())).thenReturn(Optional.of(employee));
        when(loginLogsRepository.findByLogForEmployee(employee)).thenReturn(Collections.singletonList(loginLog));

        List<LoginLogsDTO> logs = loginLogsService.getLogsForEmployee(employee.getEmployeeId());

        assertThat(logs).isNotNull();
        assertThat(logs.get(0).getLogId()).isEqualTo("log1");
        verify(loginLogsRepository).findByLogForEmployee(employee);
    }

    @Test
    void testGetLogsForEmployee_EmployeeNotFound() {
        when(employeesRepository.findById(employee.getEmployeeId())).thenReturn(Optional.empty());

        List<LoginLogsDTO> logs = loginLogsService.getLogsForEmployee(employee.getEmployeeId());

        assertThat(logs).isNotNull();
        verify(loginLogsRepository, never()).findByLogForEmployee(any(Employees.class));
    }
}
