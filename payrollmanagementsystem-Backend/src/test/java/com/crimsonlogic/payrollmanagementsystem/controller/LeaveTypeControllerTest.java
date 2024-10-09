package com.crimsonlogic.payrollmanagementsystem.controller;

import java.util.Collections;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.crimsonlogic.payrollmanagementsystem.dto.LeaveTypeDTO;
import com.crimsonlogic.payrollmanagementsystem.service.LeaveTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class LeaveTypeControllerTest {

    @InjectMocks
    private LeaveTypeController leaveTypeController;

    @Mock
    private LeaveTypeService leaveTypeService;

    private LeaveTypeDTO leaveTypeDTO;

    @BeforeEach
    void setUp() {

        leaveTypeDTO = new LeaveTypeDTO();
        leaveTypeDTO.setTypeId("leave-type-1");
        leaveTypeDTO.setTypeName("Annual Leave");
    }

    @Test
    void testGetAllTypes_Success() {
        List<LeaveTypeDTO> leaveTypeList = Collections.singletonList(leaveTypeDTO);
        when(leaveTypeService.getAllTypes()).thenReturn(leaveTypeList);

        ResponseEntity<List<LeaveTypeDTO>> response = leaveTypeController.getAllTypes();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody().get(0).getTypeId()).isEqualTo(leaveTypeDTO.getTypeId());
    }

    @Test
    void testAddLeaveType_Success() {
        when(leaveTypeService.addLeaveType(any(LeaveTypeDTO.class))).thenReturn(true);

        ResponseEntity<Void> response = leaveTypeController.addLeaveType(leaveTypeDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void testAddLeaveType_Failure() {
        when(leaveTypeService.addLeaveType(any(LeaveTypeDTO.class))).thenReturn(false);

        ResponseEntity<Void> response = leaveTypeController.addLeaveType(leaveTypeDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testUpdateLeaveType_Success() {
        when(leaveTypeService.updateLeaveType(any(LeaveTypeDTO.class))).thenReturn(true);

        ResponseEntity<Void> response = leaveTypeController.updateLeaveType(leaveTypeDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void testUpdateLeaveType_NotFound() {
        when(leaveTypeService.updateLeaveType(any(LeaveTypeDTO.class))).thenReturn(false);

        ResponseEntity<Void> response = leaveTypeController.updateLeaveType(leaveTypeDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}