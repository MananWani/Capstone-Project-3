package com.crimsonlogic.payrollmanagementsystem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


import com.crimsonlogic.payrollmanagementsystem.domain.Designation;
import com.crimsonlogic.payrollmanagementsystem.domain.Employees;
import com.crimsonlogic.payrollmanagementsystem.domain.LeaveType;
import com.crimsonlogic.payrollmanagementsystem.domain.Attendance;
import com.crimsonlogic.payrollmanagementsystem.domain.LeaveRequest;
import com.crimsonlogic.payrollmanagementsystem.domain.SalaryRecord;
import com.crimsonlogic.payrollmanagementsystem.dto.SalaryRecordDTO;
import com.crimsonlogic.payrollmanagementsystem.dto.TaxResponseDTO;
import com.crimsonlogic.payrollmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.payrollmanagementsystem.repository.AttendanceRepository;
import com.crimsonlogic.payrollmanagementsystem.repository.SalaryRepository;
import com.crimsonlogic.payrollmanagementsystem.repository.EmployeesRepository;
import com.crimsonlogic.payrollmanagementsystem.repository.LeaveRequestRepository;
import com.crimsonlogic.payrollmanagementsystem.repository.LeaveTypeRepository;
import com.crimsonlogic.payrollmanagementsystem.repository.SalaryRecordRepository;
import com.crimsonlogic.payrollmanagementsystem.serviceimpl.SalaryRecordServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SalaryRecordServiceImplTest {

    @InjectMocks
    private SalaryRecordServiceImpl salaryRecordService;

    @Mock
    private SalaryRecordRepository salaryRecordRepository;

    @Mock
    private EmployeesRepository employeesRepository;

    @Mock
    private LeaveTypeRepository leaveTypeRepository;

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private SalaryRepository salaryRepository;

    private Employees employee;

    private LeaveType leaveType;

    private SalaryRecord salaryRecord;

    private Attendance attendance;

    private SalaryRecordDTO salaryRecordDTO;

    @BeforeEach
    void setUp() {
        YearMonth yearMonth = YearMonth.now();

        // Initialize employee first
        employee = new Employees();
        employee.setEmployeeId("emp1");
        employee.setFullName("John Doe");
        employee.setJoiningDate(LocalDate.of(2020, 1, 1));
        employee.setDateOfBirth(LocalDate.of(1990, 6, 15));

        Designation designation = new Designation();
        designation.setDesignationId("des1");
        employee.setDesignation(designation);

        leaveType = new LeaveType();
        leaveType.setTypeId("type1");
        leaveType.setTypeName("Marriage Leave");

        attendance = new Attendance();
        attendance.setAttendanceId("atd1");
        attendance.setStatus("Absent");
        attendance.setAttendanceByEmployee(employee);

        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setStatus("Approved");
        leaveRequest.setRequestByEmployee(employee);
        leaveRequest.setTypeOfLeave(leaveType);
        leaveRequest.setStartDate(LocalDate.of(yearMonth.getYear(), yearMonth.getMonthValue(), 1));

        salaryRecord = new SalaryRecord();
        salaryRecord.setSalaryRecordOfEmployee(employee);
        salaryRecord.setGrossSalary(new BigDecimal("1000.00"));
        salaryRecord.setNetSalary(new BigDecimal("1000.00"));
        salaryRecord.setBonusAmount(new BigDecimal("1000.00"));
        salaryRecord.setPfAmount(new BigDecimal("1000.00"));
        salaryRecord.setTaxAmount(new BigDecimal("1000.00"));
        salaryRecord.setPenaltyAmount(new BigDecimal("1000.00"));
        salaryRecordDTO = new SalaryRecordDTO();
    }

    @Test
    void testGetSalaryByEmployeeId_Success() {
        when(employeesRepository.findById(employee.getEmployeeId())).thenReturn(Optional.of(employee));
        when(salaryRecordRepository.findBySalaryRecordOfEmployee(employee)).thenReturn(Collections.singletonList(salaryRecord));

        List<SalaryRecordDTO> result = salaryRecordService.getSalaryByEmployeeId(employee.getEmployeeId());

        assertThat(result).isNotNull().hasSize(1);

        verify(salaryRecordRepository).findBySalaryRecordOfEmployee(employee);
    }

    @Test
    void testGetSalaryByEmployeeId_EmployeeNotFound() {
        when(employeesRepository.findById(employee.getEmployeeId())).thenReturn(Optional.empty());

        List<SalaryRecordDTO> result = salaryRecordService.getSalaryByEmployeeId(employee.getEmployeeId());

        assertThat(result).isNotNull().isEmpty();
        verify(salaryRecordRepository, never()).findBySalaryRecordOfEmployee(any(Employees.class));
    }

    @Test
    void testReleaseSalaryForEmployee_Success() throws ResourceNotFoundException {
        salaryRecordDTO.setEmployeeId(employee.getEmployeeId());
        when(employeesRepository.findById(employee.getEmployeeId())).thenReturn(Optional.of(employee));


        salaryRecordService.releaseSalaryForEmployee(salaryRecordDTO);

        verify(salaryRecordRepository).save(argThat(result ->
                result.getSalaryRecordOfEmployee().equals(employee)
        ));
    }

    @Test
    void testReleaseSalaryForEmployee_EmployeeNotFound() {
        when(employeesRepository.findById(salaryRecordDTO.getEmployeeId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> salaryRecordService.releaseSalaryForEmployee(salaryRecordDTO));

        verify(salaryRecordRepository, never()).save(any(SalaryRecord.class));
    }

    @Test
    void testGetAllSalaries() {
        when(salaryRecordRepository.findAll()).thenReturn(Collections.singletonList(salaryRecord));

        List<SalaryRecordDTO> result = salaryRecordService.getAllSalaries();

        assertThat(result).isNotNull().hasSize(1);
        verify(salaryRecordRepository).findAll();
    }

    @Test
    void testGetSalaryByQuarter_Success() throws ResourceNotFoundException {
        String quarter = "quarter 1";

        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 3, 31);

        List<SalaryRecord> salaryRecords = Collections.singletonList(salaryRecord);
        when(salaryRecordRepository.findSalaryRecordsByQuarter(startDate, endDate)).thenReturn(salaryRecords);

        SalaryRecordDTO result = salaryRecordService.getSalaryByQuarter(quarter);

        assertThat(result).isNotNull();
        verify(salaryRecordRepository).findSalaryRecordsByQuarter(startDate, endDate);
    }

    @Test
    void testGetSalaryByQuarter_InvalidQuarter() {
        String invalidQuarter = "quarter 5";

        assertThrows(ResourceNotFoundException.class, () -> salaryRecordService.getSalaryByQuarter(invalidQuarter));
    }

    @Test
    void testCalculateSalaryForEmployee_Success() throws ResourceNotFoundException {
        when(employeesRepository.findById(employee.getEmployeeId())).thenReturn(Optional.of(employee));
        when(salaryRepository.findCostToCompany(employee)).thenReturn(BigDecimal.valueOf(600000));
        when(leaveTypeRepository.findByTypeName(leaveType.getTypeName())).thenReturn(leaveType);
        when(attendanceRepository.findByEmployeeAndDate(employee, YearMonth.now().getMonthValue(), YearMonth.now().getYear()))
                .thenReturn(Collections.singletonList(attendance));
        when(leaveRequestRepository.findByRequestByEmployeeAndStartDateAndTypeOfLeave(employee, 2024, 10, leaveType))
                .thenReturn(null);

        SalaryRecordDTO result = salaryRecordService.calculateSalaryForEmployee(employee.getEmployeeId());

        assertThat(result).isNotNull();
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getBonusAmount());
        assertEquals(BigDecimal.valueOf(50000), result.getGrossSalary());
        assertNotEquals(BigDecimal.ZERO, result.getPenaltyAmount());
    }

    @Test
    void testCalculateSalaryForEmployee_EmployeeNotFound() {
        when(employeesRepository.findById(employee.getEmployeeId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> salaryRecordService.calculateSalaryForEmployee(employee.getEmployeeId()));
    }

    @Test
    void testGetTaxByQuarter_Success() throws ResourceNotFoundException {
        when(employeesRepository.findById(employee.getEmployeeId())).thenReturn(Optional.of(employee));
        when(salaryRecordRepository.findSalaryRecordsByQuarterAndEmployee(any(), any(), eq(employee)))
                .thenReturn(Collections.singletonList(salaryRecord));

        TaxResponseDTO response = salaryRecordService.getTaxByQuarter("quarter 1", employee.getEmployeeId());

        assertThat(response).isNotNull();
        assertThat(response.getGrossSalary()).isEqualTo(salaryRecord.getGrossSalary());
        assertThat(response.getTaxDeducted()).isEqualTo(salaryRecord.getTaxAmount());
        assertThat(response.getNetSalary()).isEqualTo(salaryRecord.getNetSalary());
    }

    @Test
    void testGetTaxByQuarter_EmployeeNotFound() {
        when(employeesRepository.findById(employee.getEmployeeId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> salaryRecordService.getTaxByQuarter("quarter 1", employee.getEmployeeId()));
    }

    @Test
    void testGetTaxByQuarter_InvalidQuarter() {
        assertThrows(ResourceNotFoundException.class, () -> salaryRecordService.getTaxByQuarter("invalid quarter", employee.getEmployeeId()));
    }

}

