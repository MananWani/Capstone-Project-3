package com.crimsonlogic.payrollmanagementsystem.controller;

import java.util.Collections;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import com.crimsonlogic.payrollmanagementsystem.dto.LeaveRequestDTO;
import com.crimsonlogic.payrollmanagementsystem.exception.LeavesExhaustedException;
import com.crimsonlogic.payrollmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.payrollmanagementsystem.service.LeaveRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class LeaveRequestControllerTest {

    @InjectMocks
    private LeaveRequestController leaveRequestController;

    @Mock
    private LeaveRequestService leaveRequestService;

    private LeaveRequestDTO leaveRequestDTO;

    @BeforeEach
    void setUp() {

        leaveRequestDTO = new LeaveRequestDTO();
        leaveRequestDTO.setLeaveRequestId("request-1");
        leaveRequestDTO.setEmployeeId("employee-1");
    }

    @Test
    void testGetLeaveRequests_Success() {
        List<LeaveRequestDTO> leaveRequests = Collections.singletonList(leaveRequestDTO);
        when(leaveRequestService.getLeaveRequestById("employee-1")).thenReturn(leaveRequests);

        ResponseEntity<List<LeaveRequestDTO>> response = leaveRequestController.getLeaveRequests("employee-1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody().get(0).getLeaveRequestId()).isEqualTo(leaveRequestDTO.getLeaveRequestId());
    }

    @Test
    void testRequestLeave_Success() throws LeavesExhaustedException, ResourceNotFoundException {
        doNothing().when(leaveRequestService).requestLeave(any(LeaveRequestDTO.class));

        ResponseEntity<String> response = leaveRequestController.requestLeave(leaveRequestDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void testRequestLeave_LeavesExhausted() throws LeavesExhaustedException, ResourceNotFoundException {
        doThrow(new LeavesExhaustedException("Leaves exhausted")).when(leaveRequestService).requestLeave(any(LeaveRequestDTO.class));

        ResponseEntity<String> response = leaveRequestController.requestLeave(leaveRequestDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Leaves exhausted");
    }

    @Test
    void testRequestLeave_ResourceNotFound() throws LeavesExhaustedException, ResourceNotFoundException {
        doThrow(new ResourceNotFoundException("Resource not found")).when(leaveRequestService).requestLeave(any(LeaveRequestDTO.class));

        ResponseEntity<String> response = leaveRequestController.requestLeave(leaveRequestDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Resource not found");
    }

    @Test
    void testGetPendingRequests_Success() {
        List<LeaveRequestDTO> pendingRequests = Collections.singletonList(leaveRequestDTO);
        when(leaveRequestService.getPendingRequestsById("employee-1")).thenReturn(pendingRequests);

        ResponseEntity<List<LeaveRequestDTO>> response = leaveRequestController.getPendingRequests("employee-1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody().get(0).getLeaveRequestId()).isEqualTo(leaveRequestDTO.getLeaveRequestId());
    }

    @Test
    void testUpdateLeaveRequest_Success() {
        when(leaveRequestService.updateLeaveRequest(any(LeaveRequestDTO.class))).thenReturn(true);

        ResponseEntity<Void> response = leaveRequestController.updateLeaveRequest(leaveRequestDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void testUpdateLeaveRequest_NotFound() {
        when(leaveRequestService.updateLeaveRequest(any(LeaveRequestDTO.class))).thenReturn(false);

        ResponseEntity<Void> response = leaveRequestController.updateLeaveRequest(leaveRequestDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testCancelLeaveRequest_Success() {
        when(leaveRequestService.cancelLeaveRequest("request-1")).thenReturn(true);

        ResponseEntity<Void> response = leaveRequestController.cancelLeaveRequest(leaveRequestDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void testCancelLeaveRequest_NotFound() {
        when(leaveRequestService.cancelLeaveRequest("request-1")).thenReturn(false);

        ResponseEntity<Void> response = leaveRequestController.cancelLeaveRequest(leaveRequestDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
