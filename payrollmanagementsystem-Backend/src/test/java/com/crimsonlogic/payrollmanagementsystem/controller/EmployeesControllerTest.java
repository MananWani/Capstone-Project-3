package com.crimsonlogic.payrollmanagementsystem.controller;

import java.util.Collections;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import com.crimsonlogic.payrollmanagementsystem.dto.EmployeesDTO;
import com.crimsonlogic.payrollmanagementsystem.exception.ResourceExistsException;
import com.crimsonlogic.payrollmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.payrollmanagementsystem.service.EmployeesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class EmployeesControllerTest {

    @InjectMocks
    private EmployeesController employeesController;

    @Mock
    private EmployeesService employeesService;

    private EmployeesDTO employeesDTO;

    @BeforeEach
    void setUp() {

        employeesDTO = new EmployeesDTO();
        employeesDTO.setEmployeeId("employee-1");
        employeesDTO.setFullName("John Doe");
        // Initialize other necessary fields
    }

    @Test
    void testGetAllEmployees_Success() {
        List<EmployeesDTO> employeesList = Collections.singletonList(employeesDTO);
        when(employeesService.getAllEmployees()).thenReturn(employeesList);

        ResponseEntity<List<EmployeesDTO>> response = employeesController.getAllEmployees();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody().get(0).getEmployeeId()).isEqualTo(employeesDTO.getEmployeeId());
    }

    @Test
    void testAddEmployee_Success() throws ResourceExistsException, ResourceNotFoundException {
        doNothing().when(employeesService).addEmployees(any(EmployeesDTO.class));

        ResponseEntity<String> response = employeesController.addEmployee(employeesDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void testAddEmployee_ResourceExists() throws ResourceExistsException, ResourceNotFoundException {
        doThrow(new ResourceExistsException("Employee already exists")).when(employeesService).addEmployees(any(EmployeesDTO.class));

        ResponseEntity<String> response = employeesController.addEmployee(employeesDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Employee already exists");
    }

    @Test
    void testAddEmployee_ResourceNotFound() throws ResourceExistsException, ResourceNotFoundException {
        doThrow(new ResourceNotFoundException("Resource not found")).when(employeesService).addEmployees(any(EmployeesDTO.class));

        ResponseEntity<String> response = employeesController.addEmployee(employeesDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Resource not found");
    }

    @Test
    void testUpdateEmployee_Success() {
        when(employeesService.updateEmployee(any(EmployeesDTO.class))).thenReturn(true);

        ResponseEntity<Void> response = employeesController.updateEmployee(employeesDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void testUpdateEmployee_NotFound() {
        when(employeesService.updateEmployee(any(EmployeesDTO.class))).thenReturn(false);

        ResponseEntity<Void> response = employeesController.updateEmployee(employeesDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testGetManagers_Success() {
        List<EmployeesDTO> managersList = Collections.singletonList(employeesDTO);
        when(employeesService.getManagers()).thenReturn(managersList);

        ResponseEntity<List<EmployeesDTO>> response = employeesController.getManagers();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    void testGetEmployee_Success() throws ResourceNotFoundException {
        when(employeesService.getEmployeeById("employee-1")).thenReturn(employeesDTO);

        ResponseEntity<EmployeesDTO> response = employeesController.getEmployee("employee-1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(employeesDTO);
    }

    @Test
    void testGetEmployee_NotFound() throws ResourceNotFoundException {
        when(employeesService.getEmployeeById("employee-1")).thenThrow(new ResourceNotFoundException("Employee not found"));

        ResponseEntity<EmployeesDTO> response = employeesController.getEmployee("employee-1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testGetTeam_Success() {
        List<EmployeesDTO> teamList = Collections.singletonList(employeesDTO);
        when(employeesService.getTeam("employee-1")).thenReturn(teamList);

        ResponseEntity<List<EmployeesDTO>> response = employeesController.getTeam("employee-1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    void testUpdateRating_Success() {
        when(employeesService.updateRating(any(EmployeesDTO.class))).thenReturn(true);

        ResponseEntity<Void> response = employeesController.updateRating(employeesDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void testUpdateRating_NotFound() {
        when(employeesService.updateRating(any(EmployeesDTO.class))).thenReturn(false);

        ResponseEntity<Void> response = employeesController.updateRating(employeesDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
