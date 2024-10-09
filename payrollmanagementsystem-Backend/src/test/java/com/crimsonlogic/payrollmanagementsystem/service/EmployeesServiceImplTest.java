package com.crimsonlogic.payrollmanagementsystem.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.crimsonlogic.payrollmanagementsystem.domain.Designation;
import com.crimsonlogic.payrollmanagementsystem.domain.Employees;
import com.crimsonlogic.payrollmanagementsystem.domain.Roles;
import com.crimsonlogic.payrollmanagementsystem.domain.Users;
import com.crimsonlogic.payrollmanagementsystem.dto.EmployeesDTO;
import com.crimsonlogic.payrollmanagementsystem.exception.ResourceExistsException;
import com.crimsonlogic.payrollmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.payrollmanagementsystem.repository.DesignationRepository;
import com.crimsonlogic.payrollmanagementsystem.repository.EmployeesRepository;
import com.crimsonlogic.payrollmanagementsystem.repository.RolesRepository;
import com.crimsonlogic.payrollmanagementsystem.repository.UsersRepository;
import com.crimsonlogic.payrollmanagementsystem.serviceimpl.EmployeesServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmployeesServiceImplTest {

    @InjectMocks
    private EmployeesServiceImpl employeesService;

    @Mock
    private EmployeesRepository employeesRepository;

    @Mock
    private DesignationRepository designationRepository;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private RolesRepository rolesRepository;

    @Mock
    private LeaveRecordService leaveRecordService;

    @Mock
    private SalaryService salaryRecordService;

    private Employees employee;

    private Employees manager;

    private Designation designation;

    private Users user;

    private Roles role;

    private EmployeesDTO employeeDTO;

    @BeforeEach
    void setUp() {

        user=new Users();
        user.setEmail("test@example.com");
        user.setRole(role);

        role=new Roles();
        role.setRoleId("rol1");
        role.setRoleName("New role");

        designation=new Designation();
        designation.setDesignationId("des1");
        designation.setDesignationName("Software Engineer");

        manager=new Employees();
        manager.setEmployeeId("emp2");
        manager.setFullName("mgr1");

        employee = new Employees();
        employee.setEmployeeId("emp1");
        employee.setMobileNumber("1234567890");
        employee.setIsActive(true);
        employee.setDesignation(designation);
        employee.setManager(manager);
        employee.setUser(user);

        employeeDTO = new EmployeesDTO();
        employeeDTO.setEmployeeId("emp1");
        employeeDTO.setMobileNumber("1234567890");
        employeeDTO.setIsActive(true);
        employeeDTO.setDesignation("des1");
        employeeDTO.setManager("mgr1");
        employeeDTO.setRole("rol1");
        employeeDTO.setEmail("test@example.com");
        employeeDTO.setPasswordHash("hashedPassword");
    }

    @Test
    void testGetAllEmployees() {
        when(employeesRepository.getEmployeeDetails()).thenReturn(Collections.singletonList(employeeDTO));

        List<EmployeesDTO> employeesList = employeesService.getAllEmployees();

        assertThat(employeesList)
                .isNotNull()
                .hasSize(1);
        verify(employeesRepository).getEmployeeDetails();
    }

    @Test
    void testAddEmployees_Success() throws ResourceExistsException, ResourceNotFoundException {
        when(employeesRepository.existsByMobileNumber(anyString())).thenReturn(false);
        when(usersRepository.existsByEmail(anyString())).thenReturn(false);
        when(designationRepository.findById(anyString())).thenReturn(Optional.of(designation));
        when(rolesRepository.findById(anyString())).thenReturn(Optional.of(role));
        when(employeesRepository.findById(anyString())).thenReturn(Optional.of(manager));

        employeesService.addEmployees(employeeDTO);

        verify(employeesRepository).save(any(Employees.class));
        verify(leaveRecordService).setEmployeeLeaves(any(Employees.class));
        verify(salaryRecordService).setSalary(any(Employees.class));
    }

    @Test
    void testAddEmployees_MobileExists() {
        when(employeesRepository.existsByMobileNumber(anyString())).thenReturn(true);

        assertThrows(ResourceExistsException.class, () -> employeesService.addEmployees(employeeDTO));
    }

    @Test
    void testAddEmployees_EmailExists() {
        when(employeesRepository.existsByMobileNumber(anyString())).thenReturn(false);
        when(usersRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(ResourceExistsException.class, () -> employeesService.addEmployees(employeeDTO));
    }

    @Test
    void testUpdateEmployee_Success() {
        when(employeesRepository.findById(anyString())).thenReturn(Optional.of(employee));
        when(designationRepository.findById(anyString())).thenReturn(Optional.of(new Designation()));
        when(employeesRepository.findById(employeeDTO.getManager())).thenReturn(Optional.of(new Employees()));

        boolean result = employeesService.updateEmployee(employeeDTO);

        assertThat(result).isTrue();
        verify(employeesRepository).save(employee);
    }

    @Test
    void testUpdateEmployee_NotFound() {
        when(employeesRepository.findById(anyString())).thenReturn(Optional.empty());

        boolean result = employeesService.updateEmployee(employeeDTO);

        assertThat(result).isFalse();
        verify(employeesRepository, never()).save(any(Employees.class));
    }

    @Test
    void testGetEmployeeById_Success() throws ResourceNotFoundException {
        when(employeesRepository.findById("emp1")).thenReturn(Optional.of(employee));

        EmployeesDTO result = employeesService.getEmployeeById("emp1");

        assertThat(result).isNotNull();
        assertThat(result.getDesignation()).isEqualTo("Software Engineer");
        verify(employeesRepository).findById("emp1");
    }

    @Test
    void testGetEmployeeById_NotFound() {
        when(employeesRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeesService.getEmployeeById("invalidId"));
    }

    @Test
    void testGetTeam_Success() {
        when(employeesRepository.findById(anyString())).thenReturn(Optional.of(employee));
        when(employeesRepository.findEmployeesReportingToManager(any(Employees.class))).thenReturn(Collections.singletonList(employeeDTO));

        List<EmployeesDTO> team = employeesService.getTeam("emp1");

        assertThat(team).isNotEmpty();
        verify(employeesRepository).findEmployeesReportingToManager(employee);
    }

    @Test
    void testUpdateRating_Success() {
        when(employeesRepository.findById(anyString())).thenReturn(Optional.of(employee));

        boolean result = employeesService.updateRating(employeeDTO);

        assertThat(result).isTrue();
        verify(employeesRepository).save(employee);
    }

    @Test
    void testUpdateRating_NotFound() {
        when(employeesRepository.findById(anyString())).thenReturn(Optional.empty());

        boolean result = employeesService.updateRating(employeeDTO);

        assertThat(result).isFalse();
        verify(employeesRepository, never()).save(any(Employees.class));
    }
}

