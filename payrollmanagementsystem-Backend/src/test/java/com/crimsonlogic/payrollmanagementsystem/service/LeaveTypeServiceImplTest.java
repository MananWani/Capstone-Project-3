package com.crimsonlogic.payrollmanagementsystem.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.crimsonlogic.payrollmanagementsystem.domain.LeaveType;
import com.crimsonlogic.payrollmanagementsystem.dto.LeaveTypeDTO;
import com.crimsonlogic.payrollmanagementsystem.repository.LeaveTypeRepository;
import com.crimsonlogic.payrollmanagementsystem.serviceimpl.LeaveTypeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LeaveTypeServiceImplTest {

    @InjectMocks
    private LeaveTypeServiceImpl leaveTypeService;

    @Mock
    private LeaveTypeRepository leaveTypeRepository;

    private LeaveType leaveType;

    private LeaveTypeDTO leaveTypeDTO;

    @BeforeEach
    void setUp() {

        leaveType = new LeaveType();
        leaveType.setTypeId("leave1");
        leaveType.setTypeName("Annual Leave");
        leaveType.setNumberOfLeaves(30);

        leaveTypeDTO = new LeaveTypeDTO();
        leaveTypeDTO.setTypeId("leave1");
        leaveTypeDTO.setTypeName("Sick Leave");
        leaveTypeDTO.setNumberOfLeaves(10);
    }

    @Test
    void testGetAllTypes() {
        when(leaveTypeRepository.findAll()).thenReturn(Collections.singletonList(leaveType));

        List<LeaveTypeDTO> leaveTypesList = leaveTypeService.getAllTypes();

        assertThat(leaveTypesList).isNotNull().hasSize(1);
        assertThat(leaveTypesList.get(0).getTypeName()).isEqualTo("Annual Leave");
        verify(leaveTypeRepository).findAll();
    }

    @Test
    void testAddLeaveType_Success() {
        leaveTypeDTO.setTypeName("New Leave Type");
        leaveTypeDTO.setNumberOfLeaves(15);

        boolean result = leaveTypeService.addLeaveType(leaveTypeDTO);

        assertThat(result).isTrue();
        verify(leaveTypeRepository).save(any(LeaveType.class));
    }

    @Test
    void testAddLeaveType_TypeNameMissing() {
        leaveTypeDTO.setTypeName(null);

        boolean result = leaveTypeService.addLeaveType(leaveTypeDTO);

        assertThat(result).isFalse();
        verify(leaveTypeRepository, never()).save(any(LeaveType.class));
    }

    @Test
    void testUpdateLeaveType_Success() {
        when(leaveTypeRepository.findById(leaveType.getTypeId())).thenReturn(Optional.of(leaveType));

        boolean result = leaveTypeService.updateLeaveType(leaveTypeDTO);

        assertThat(result).isTrue();
        assertThat(leaveType.getTypeName()).isEqualTo(leaveTypeDTO.getTypeName());
        assertThat(leaveType.getNumberOfLeaves()).isEqualTo(leaveTypeDTO.getNumberOfLeaves());
        verify(leaveTypeRepository).save(leaveType);
    }

    @Test
    void testUpdateLeaveType_NotFound() {
        when(leaveTypeRepository.findById(anyString())).thenReturn(Optional.empty());

        boolean result = leaveTypeService.updateLeaveType(leaveTypeDTO);

        assertThat(result).isFalse();
        verify(leaveTypeRepository, never()).save(any(LeaveType.class));
    }
}

