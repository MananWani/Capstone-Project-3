package com.crimsonlogic.payrollmanagementsystem.controller;

import com.crimsonlogic.payrollmanagementsystem.domain.Employees;
import com.crimsonlogic.payrollmanagementsystem.dto.LoginLogsDTO;
import com.crimsonlogic.payrollmanagementsystem.service.LoginLogsService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class LoginLogsControllerTest {

    @InjectMocks
    private LoginLogsController loginLogsController;

    @Mock
    private LoginLogsService loginLogsService;

    private LoginLogsDTO loginLogsDTO;

    @BeforeEach
    public void setUp() {

        Employees employee = new Employees();
        employee.setEmployeeId("emp1");

        loginLogsDTO = new LoginLogsDTO();
        loginLogsDTO.setLogId("log-1");
        loginLogsDTO.setLogForEmployee("emp1");
        loginLogsDTO.setLoginTime(Timestamp.valueOf(LocalDateTime.now()));
        loginLogsDTO.setLogoutTime(Timestamp.valueOf(LocalDateTime.now()));
    }

    @Test
    void testGetLogsForEmployee_Success() {
        when(loginLogsService.getLogsForEmployee("emp1")).thenReturn(Collections.singletonList(loginLogsDTO));

        ResponseEntity<List<LoginLogsDTO>> response = loginLogsController.getLogs("emp1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody().get(0).getLogId()).isEqualTo(loginLogsDTO.getLogId());
        verify(loginLogsService).getLogsForEmployee("emp1");
    }

    @Test
    void testGetLogsForEmployee_NotFound() {
        when(loginLogsService.getLogsForEmployee("employee-2")).thenReturn(Collections.emptyList());

        ResponseEntity<List<LoginLogsDTO>> response = loginLogsController.getLogs("employee-2");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
        verify(loginLogsService).getLogsForEmployee("employee-2");
    }


    @Test
    void setLogoutLog_ShouldSetLogoutForLogId() {
        String logId = "log-1";

        loginLogsController.setLogoutLog(logId);

        verify(loginLogsService).setLogoutLog(logId);
    }

}

