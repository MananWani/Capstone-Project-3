package com.crimsonlogic.payrollmanagementsystem.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.crimsonlogic.payrollmanagementsystem.domain.Employees;
import com.crimsonlogic.payrollmanagementsystem.domain.LeaveRecord;
import com.crimsonlogic.payrollmanagementsystem.domain.LeaveType;
import com.crimsonlogic.payrollmanagementsystem.dto.LeaveRecordDTO;
import com.crimsonlogic.payrollmanagementsystem.repository.EmployeesRepository;
import com.crimsonlogic.payrollmanagementsystem.repository.LeaveRecordRepository;
import com.crimsonlogic.payrollmanagementsystem.repository.LeaveTypeRepository;
import com.crimsonlogic.payrollmanagementsystem.serviceimpl.LeaveRecordServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
class LeaveRecordServiceImplTest {

    @InjectMocks
    private LeaveRecordServiceImpl leaveRecordService;

    @Mock
    private LeaveRecordRepository leaveRecordRepository;

    @Mock
    private LeaveTypeRepository leaveTypeRepository;

    @Mock
    private EmployeesRepository employeesRepository;

    private Employees employee;

    private LeaveType leaveType;

    private LeaveRecord leaveRecord;

    @BeforeEach
    void setUp() {
        employee = new Employees();
        employee.setEmployeeId("emp1");

        leaveType = new LeaveType();
        leaveType.setTypeName("Sick Leave");
        leaveType.setNumberOfLeaves(10);

        leaveRecord = new LeaveRecord();
        leaveRecord.setLeaveForEmployee(employee);
        leaveRecord.setTypeOfLeave(leaveType);
        leaveRecord.setTotalLeaves(10);
        leaveRecord.setUsedLeaves(BigDecimal.ZERO);
        leaveRecord.setRemainingLeaves(BigDecimal.valueOf(10));
    }

    @Test
    @Transactional
    void testSetEmployeeLeaves() {
        when(leaveTypeRepository.findAll()).thenReturn(Collections.singletonList(leaveType));

        leaveRecordService.setEmployeeLeaves(employee);

        verify(leaveRecordRepository).save(any(LeaveRecord.class));
    }

    @Test
    void testGetLeaveRecordById_Success() {
        when(employeesRepository.findById(employee.getEmployeeId())).thenReturn(Optional.of(employee));
        when(leaveRecordRepository.findByLeaveForEmployee(employee)).thenReturn(Collections.singletonList(leaveRecord));

        List<LeaveRecordDTO> leaveRecords = leaveRecordService.getLeaveRecordById(employee.getEmployeeId());

        assertThat(leaveRecords)
                .isNotNull()
                .hasSize(1);
        assertThat(leaveRecords.get(0).getTypeOfLeave()).isEqualTo("Sick Leave");
        verify(leaveRecordRepository).findByLeaveForEmployee(employee);
    }

    @Test
    void testGetLeaveRecordById_EmployeeNotFound() {
        when(employeesRepository.findById(employee.getEmployeeId())).thenReturn(Optional.empty());

        List<LeaveRecordDTO> leaveRecords = leaveRecordService.getLeaveRecordById(employee.getEmployeeId());

        assertThat(leaveRecords)
                .isNotNull()
                .isEmpty();
        verify(leaveRecordRepository, never()).findByLeaveForEmployee(any(Employees.class));
    }
}

