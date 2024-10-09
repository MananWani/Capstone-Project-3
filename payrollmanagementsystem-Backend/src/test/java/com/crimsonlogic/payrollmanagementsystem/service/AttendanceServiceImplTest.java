package com.crimsonlogic.payrollmanagementsystem.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.anyInt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.crimsonlogic.payrollmanagementsystem.domain.Attendance;
import com.crimsonlogic.payrollmanagementsystem.domain.Employees;
import com.crimsonlogic.payrollmanagementsystem.dto.AttendanceDTO;
import com.crimsonlogic.payrollmanagementsystem.dto.AttendanceResponseDTO;
import com.crimsonlogic.payrollmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.payrollmanagementsystem.repository.AttendanceRepository;
import com.crimsonlogic.payrollmanagementsystem.repository.AttendanceRepositoryCustom;
import com.crimsonlogic.payrollmanagementsystem.repository.EmployeesRepository;
import com.crimsonlogic.payrollmanagementsystem.serviceimpl.AttendanceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceImplTest {

    @InjectMocks
    private AttendanceServiceImpl attendanceService;

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private AttendanceRepositoryCustom attendanceRepositoryCustom;

    @Mock
    private EmployeesRepository employeesRepository;

    private Employees employee;

    private Attendance attendance;

    @BeforeEach
    void setUp() {
        employee = new Employees();
        employee.setEmployeeId("emp1");
        employee.setFullName("John Doe");

        attendance = new Attendance();
        attendance.setAttendanceByEmployee(employee);
        attendance.setAttendanceForDate(LocalDate.now());
        attendance.setStatus("Present");
        attendance.setTotalHours(BigDecimal.valueOf(8));
    }

    @Test
    void testGetAttendanceForEmployee_Success() {
        when(employeesRepository.findById(employee.getEmployeeId())).thenReturn(Optional.of(employee));
        when(attendanceRepository.findByAttendanceByEmployee(employee)).thenReturn(Collections.singletonList(attendance));

        List<AttendanceDTO> attendanceDTOs = attendanceService.getAttendanceForEmployee(employee.getEmployeeId());

        assertThat(attendanceDTOs).isNotEmpty();
        assertThat(attendanceDTOs.get(0).getAttendanceByEmployee()).isEqualTo(employee.getEmployeeId());
        verify(attendanceRepository).findByAttendanceByEmployee(employee);
    }

    @Test
    void testGetAttendanceForEmployee_EmployeeNotFound() {
        when(employeesRepository.findById(employee.getEmployeeId())).thenReturn(Optional.empty());

        List<AttendanceDTO> attendanceDTOs = attendanceService.getAttendanceForEmployee(employee.getEmployeeId());

        assertThat(attendanceDTOs).isNotNull().isEmpty();
        verify(attendanceRepository, never()).findByAttendanceByEmployee(any(Employees.class));
    }

    @Test
    void testMarkAttendance_Success() throws ResourceNotFoundException {
        AttendanceDTO attendanceDTO = new AttendanceDTO();
        attendanceDTO.setAttendanceByEmployee(employee.getEmployeeId());
        attendanceDTO.setAttendanceForDate(LocalDate.now());
        attendanceDTO.setStatus("Present");

        when(employeesRepository.findById(employee.getEmployeeId())).thenReturn(Optional.of(employee));

        attendanceService.markAttendance(attendanceDTO);

        verify(attendanceRepository).save(any(Attendance.class));
    }

    @Test
    void testMarkAttendance_EmployeeNotFound() {
        AttendanceDTO attendanceDTO = new AttendanceDTO();
        attendanceDTO.setAttendanceByEmployee(employee.getEmployeeId());

        when(employeesRepository.findById(employee.getEmployeeId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> attendanceService.markAttendance(attendanceDTO));
    }

    @Test
    void testGetTeamAttendance_Success() throws ResourceNotFoundException {
        when(employeesRepository.findById(employee.getEmployeeId())).thenReturn(Optional.of(employee));
        when(attendanceRepository.findByMonthAndEmployee(anyInt(), any())).thenReturn(Collections.singletonList(attendance));

        AttendanceResponseDTO response = attendanceService.getTeamAttendance(10, employee.getEmployeeId());

        assertThat(response.getFullName()).isEqualTo(employee.getFullName());
        assertThat(response.getPresentCount()).isEqualTo(1);
        verify(attendanceRepository).findByMonthAndEmployee(anyInt(), any(Employees.class));
    }

    @Test
    void testGetTeamAttendance_EmployeeNotFound() {
        when(employeesRepository.findById(employee.getEmployeeId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> attendanceService.getTeamAttendance(10, employee.getEmployeeId()));
    }

    @Test
    void testRegularize() {
        attendanceService.regularize();

        verify(attendanceRepositoryCustom).insertAbsentAttendance();
    }
}

