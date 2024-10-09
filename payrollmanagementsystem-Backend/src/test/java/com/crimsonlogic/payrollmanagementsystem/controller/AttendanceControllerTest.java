package com.crimsonlogic.payrollmanagementsystem.controller;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import com.crimsonlogic.payrollmanagementsystem.dto.AttendanceDTO;
import com.crimsonlogic.payrollmanagementsystem.dto.AttendanceResponseDTO;
import com.crimsonlogic.payrollmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.payrollmanagementsystem.service.AttendanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class AttendanceControllerTest {

    @InjectMocks
    private AttendanceController attendanceController;

    @Mock
    private AttendanceService attendanceService;

    private AttendanceDTO attendanceDTO;

    private AttendanceResponseDTO attendanceResponseDTO;

    @BeforeEach
    void setUp() {

        attendanceDTO = new AttendanceDTO();
        attendanceDTO.setAttendanceId("atd-1");
        attendanceDTO.setAttendanceForDate(LocalDate.now());
        // Add other necessary fields

        attendanceResponseDTO = new AttendanceResponseDTO();
        // Initialize attendanceResponseDTO as needed
    }

    @Test
    void testGetAttendanceForEmployee_Success() {
        List<AttendanceDTO> attendanceList = Collections.singletonList(attendanceDTO);
        when(attendanceService.getAttendanceForEmployee("employee-1")).thenReturn(attendanceList);

        ResponseEntity<List<AttendanceDTO>> response = attendanceController.getAttendanceForEmployee("employee-1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody().get(0).getAttendanceId()).isEqualTo(attendanceDTO.getAttendanceId());
    }

    @Test
    void testMarkAttendance_Success() throws ResourceNotFoundException {
        doNothing().when(attendanceService).markAttendance(any(AttendanceDTO.class));

        ResponseEntity<String> response = attendanceController.markAttendance(attendanceDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void testMarkAttendance_ResourceNotFound() throws ResourceNotFoundException {
        doThrow(new ResourceNotFoundException("Resource not found")).when(attendanceService).markAttendance(any(AttendanceDTO.class));

        ResponseEntity<String> response = attendanceController.markAttendance(attendanceDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Resource not found");
    }

    @Test
    void testGetTeamAttendance_Success() throws ResourceNotFoundException {
        when(attendanceService.getTeamAttendance(10, "employee-1")).thenReturn(attendanceResponseDTO);

        ResponseEntity<AttendanceResponseDTO> response = attendanceController.getTeamAttendance(10, "employee-1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(attendanceResponseDTO);
    }

    @Test
    void testGetTeamAttendance_ResourceNotFound() throws ResourceNotFoundException {
        doThrow(new ResourceNotFoundException("Resource not found")).when(attendanceService).getTeamAttendance(anyInt(), anyString());

        ResponseEntity<AttendanceResponseDTO> response = attendanceController.getTeamAttendance(10, "employee-1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testRegularize_Success() {
        doNothing().when(attendanceService).regularize();

        ResponseEntity<Void> response = attendanceController.regularize();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }
}
