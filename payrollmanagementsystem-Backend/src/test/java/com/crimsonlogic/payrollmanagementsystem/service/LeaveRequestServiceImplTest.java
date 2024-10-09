package com.crimsonlogic.payrollmanagementsystem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.crimsonlogic.payrollmanagementsystem.domain.Employees;
import com.crimsonlogic.payrollmanagementsystem.domain.LeaveRecord;
import com.crimsonlogic.payrollmanagementsystem.domain.LeaveRequest;
import com.crimsonlogic.payrollmanagementsystem.domain.LeaveType;
import com.crimsonlogic.payrollmanagementsystem.dto.LeaveRequestDTO;
import com.crimsonlogic.payrollmanagementsystem.exception.LeavesExhaustedException;
import com.crimsonlogic.payrollmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.payrollmanagementsystem.repository.EmployeesRepository;
import com.crimsonlogic.payrollmanagementsystem.repository.LeaveRecordRepository;
import com.crimsonlogic.payrollmanagementsystem.repository.LeaveRequestRepository;
import com.crimsonlogic.payrollmanagementsystem.repository.LeaveTypeRepository;
import com.crimsonlogic.payrollmanagementsystem.repository.AttendanceRepository;
import com.crimsonlogic.payrollmanagementsystem.serviceimpl.LeaveRequestServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LeaveRequestServiceImplTest {

    @InjectMocks
    private LeaveRequestServiceImpl leaveRequestService;

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @Mock
    private EmployeesRepository employeesRepository;

    @Mock
    private LeaveTypeRepository leaveTypeRepository;

    @Mock
    private LeaveRecordRepository leaveRecordRepository;

    @Mock
    private AttendanceRepository attendanceRepository;

    private Employees employee;

    private LeaveType leaveType;

    private LeaveRequestDTO leaveRequestDTO;


    @BeforeEach
    void setUp() {
        employee = new Employees();
        employee.setEmployeeId("emp1");
        employee.setFullName("Name");

        leaveType = new LeaveType();
        leaveType.setTypeName("Sick Leave");
        leaveType.setNumberOfLeaves(10);

        leaveRequestDTO = new LeaveRequestDTO();
        leaveRequestDTO.setRequestByEmployee(employee.getEmployeeId());
        leaveRequestDTO.setTypeOfLeave(leaveType.getTypeId());
        leaveRequestDTO.setStartDate(LocalDate.now());
        leaveRequestDTO.setEndDate(LocalDate.now());
        leaveRequestDTO.setStartHalf("Morning");
        leaveRequestDTO.setEndHalf("Morning");
    }

    @Test
    void testGetLeaveRequestById_Success() {
        when(employeesRepository.findById(employee.getEmployeeId())).thenReturn(Optional.of(employee));
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setTypeOfLeave(leaveType);
        when(leaveRequestRepository.findByRequestByEmployee(employee)).thenReturn(Collections.singletonList(leaveRequest));

        List<LeaveRequestDTO> leaveRequests = leaveRequestService.getLeaveRequestById(employee.getEmployeeId());

        assertThat(leaveRequests).isNotEmpty();
        assertThat(leaveRequests.get(0).getTypeOfLeave()).isEqualTo("Sick Leave");
        verify(leaveRequestRepository).findByRequestByEmployee(employee);
    }

    @Test
    void testGetLeaveRequestById_EmployeeNotFound() {
        when(employeesRepository.findById(employee.getEmployeeId())).thenReturn(Optional.empty());

        List<LeaveRequestDTO> leaveRequests = leaveRequestService.getLeaveRequestById(employee.getEmployeeId());

        assertThat(leaveRequests).isEmpty();
        verify(leaveRequestRepository, never()).findByRequestByEmployee(any(Employees.class));
    }

    @Test
    void testRequestLeave_Success() throws LeavesExhaustedException, ResourceNotFoundException {
        when(employeesRepository.findById(leaveRequestDTO.getRequestByEmployee())).thenReturn(Optional.of(employee));
        when(leaveTypeRepository.findById(leaveRequestDTO.getTypeOfLeave())).thenReturn(Optional.of(leaveType));
        LeaveRecord leaveRecord = new LeaveRecord();
        leaveRecord.setRemainingLeaves(BigDecimal.valueOf(10));
        when(leaveRecordRepository.findByLeaveForEmployeeAndTypeOfLeave(employee, leaveType)).thenReturn(leaveRecord);

        leaveRequestService.requestLeave(leaveRequestDTO);

        verify(leaveRequestRepository).save(any(LeaveRequest.class));
    }

    @Test
    void testRequestLeave_LeavesExhausted() {
        when(employeesRepository.findById(leaveRequestDTO.getRequestByEmployee())).thenReturn(Optional.of(employee));
        when(leaveTypeRepository.findById(leaveRequestDTO.getTypeOfLeave())).thenReturn(Optional.of(leaveType));
        LeaveRecord leaveRecord = new LeaveRecord();
        leaveRecord.setRemainingLeaves(BigDecimal.ZERO);
        leaveRecord.setTypeOfLeave(leaveType);
        when(leaveRecordRepository.findByLeaveForEmployeeAndTypeOfLeave(employee, leaveType)).thenReturn(leaveRecord);

        assertThatThrownBy(() -> leaveRequestService.requestLeave(leaveRequestDTO))
                .isInstanceOf(LeavesExhaustedException.class)
                .hasMessageContaining("Sick Leave count is exhausted.");
    }

    @Test
    void testRequestLeave_ResourceNotFound() {
        when(employeesRepository.findById(leaveRequestDTO.getRequestByEmployee())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> leaveRequestService.requestLeave(leaveRequestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Unexpected error, Please try again later.");
    }

    @Test
    void testGetPendingRequestsById_Success() {
        when(employeesRepository.findById(employee.getEmployeeId())).thenReturn(Optional.of(employee));
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setTypeOfLeave(leaveType);
        leaveRequest.setRequestByEmployee(employee);
        when(leaveRequestRepository.findPendingLeaveRequests(employee)).thenReturn(Collections.singletonList(leaveRequest));

        List<LeaveRequestDTO> pendingRequests = leaveRequestService.getPendingRequestsById(employee.getEmployeeId());

        assertThat(pendingRequests).isNotEmpty();
        assertThat(pendingRequests.get(0).getTypeOfLeave()).isEqualTo("Sick Leave");
        verify(leaveRequestRepository).findPendingLeaveRequests(employee);
    }

    @Test
    void testGetPendingRequestsById_EmployeeNotFound() {
        when(employeesRepository.findById(employee.getEmployeeId())).thenReturn(Optional.empty());

        List<LeaveRequestDTO> pendingRequests = leaveRequestService.getPendingRequestsById(employee.getEmployeeId());

        assertThat(pendingRequests).isEmpty();
        verify(leaveRequestRepository, never()).findPendingLeaveRequests(any(Employees.class));
    }

    @Test
    void testUpdateLeaveRequest_Success() {
        leaveRequestDTO.setLeaveRequestId("req1");
        when(leaveRequestRepository.findById("req1")).thenReturn(Optional.of(new LeaveRequest()));

        boolean result = leaveRequestService.updateLeaveRequest(leaveRequestDTO);

        assertThat(result).isTrue();
        verify(leaveRequestRepository).save(any(LeaveRequest.class));
    }

    @Test
    void testUpdateLeaveRequest_RequestNotFound() {
        leaveRequestDTO.setLeaveRequestId("req1");
        when(leaveRequestRepository.findById("req1")).thenReturn(Optional.empty());

        boolean result = leaveRequestService.updateLeaveRequest(leaveRequestDTO);

        assertThat(result).isFalse();
        verify(leaveRequestRepository, never()).save(any(LeaveRequest.class));
    }

    @Test
    void testCancelLeaveRequest_Success() {
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setStatus("Approved");
        leaveRequest.setRequestByEmployee(employee);
        leaveRequest.setTypeOfLeave(leaveType);
        leaveRequest.setNoOfDays(BigDecimal.valueOf(2));
        when(leaveRequestRepository.findById("req1")).thenReturn(Optional.of(leaveRequest));

        LeaveRecord leaveRecord = new LeaveRecord();
        leaveRecord.setRemainingLeaves(BigDecimal.valueOf(8));
        leaveRecord.setUsedLeaves(BigDecimal.valueOf(4));
        when(leaveRecordRepository.findByLeaveForEmployeeAndTypeOfLeave(employee, leaveType)).thenReturn(leaveRecord);

        boolean result = leaveRequestService.cancelLeaveRequest("req1");

        assertThat(result).isTrue();
        verify(leaveRequestRepository).save(leaveRequest);
        verify(attendanceRepository).deleteByEmployeeAndDateRange(any(), any(), any());
    }

    @Test
    void testCancelLeaveRequest_RequestNotFound() {
        when(leaveRequestRepository.findById("req1")).thenReturn(Optional.empty());

        boolean result = leaveRequestService.cancelLeaveRequest("req1");

        assertThat(result).isFalse();
        verify(leaveRequestRepository, never()).save(any(LeaveRequest.class));
    }
}
