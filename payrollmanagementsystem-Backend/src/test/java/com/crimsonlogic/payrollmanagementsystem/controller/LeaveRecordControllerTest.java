package com.crimsonlogic.payrollmanagementsystem.controller;

import java.util.Collections;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.crimsonlogic.payrollmanagementsystem.dto.LeaveRecordDTO;
import com.crimsonlogic.payrollmanagementsystem.service.LeaveRecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class LeaveRecordControllerTest {

    @InjectMocks
    private LeaveRecordController leaveRecordController;

    @Mock
    private LeaveRecordService leaveRecordService;

    private LeaveRecordDTO leaveRecordDTO;

    @BeforeEach
    void setUp() {

        leaveRecordDTO = new LeaveRecordDTO();
        leaveRecordDTO.setLeaveForEmployee("employee-1");
        leaveRecordDTO.setTypeOfLeave("Sick Leave");
        // Initialize other necessary fields
    }

    @Test
    void testGetLeaveRecord_Success() {
        List<LeaveRecordDTO> leaveRecords = Collections.singletonList(leaveRecordDTO);
        when(leaveRecordService.getLeaveRecordById("employee-1")).thenReturn(leaveRecords);

        ResponseEntity<List<LeaveRecordDTO>> response = leaveRecordController.getLeaveRecord("employee-1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody().get(0).getLeaveForEmployee()).isEqualTo(leaveRecordDTO.getLeaveForEmployee());
    }

    @Test
    void testGetLeaveRecord_NotFound() {
        when(leaveRecordService.getLeaveRecordById("employee-1")).thenReturn(List.of());

        ResponseEntity<List<LeaveRecordDTO>> response = leaveRecordController.getLeaveRecord("employee-1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }
}