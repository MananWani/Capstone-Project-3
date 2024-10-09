package com.crimsonlogic.payrollmanagementsystem.controller;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import com.crimsonlogic.payrollmanagementsystem.dto.SalaryRecordDTO;
import com.crimsonlogic.payrollmanagementsystem.dto.TaxResponseDTO;
import com.crimsonlogic.payrollmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.payrollmanagementsystem.service.SalaryRecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class SalaryRecordControllerTest {

    @InjectMocks
    private SalaryRecordController salaryRecordController;

    @Mock
    private SalaryRecordService salaryService;

    private SalaryRecordDTO salaryRecordDTO;

    private TaxResponseDTO taxResponseDTO;

    @BeforeEach
    void setUp() {

        salaryRecordDTO = new SalaryRecordDTO();
        salaryRecordDTO.setEmployeeId("employee-1");
        salaryRecordDTO.setGrossSalary(BigDecimal.valueOf(50000));
        // Initialize other necessary fields

        taxResponseDTO = new TaxResponseDTO();
        taxResponseDTO.setGrossSalary(BigDecimal.valueOf(50000));
        // Initialize other necessary fields
    }

    @Test
    void testCalculateSalaryForEmployee_Success() throws ResourceNotFoundException {
        when(salaryService.calculateSalaryForEmployee("employee-1")).thenReturn(salaryRecordDTO);

        ResponseEntity<SalaryRecordDTO> response = salaryRecordController.calculateSalaryForEmployee("employee-1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(salaryRecordDTO);
    }

    @Test
    void testCalculateSalaryForEmployee_NotFound() throws ResourceNotFoundException {
        when(salaryService.calculateSalaryForEmployee("employee-1")).thenThrow(new ResourceNotFoundException("Resource not found"));

        ResponseEntity<SalaryRecordDTO> response = salaryRecordController.calculateSalaryForEmployee("employee-1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testGetSalary_Success() {
        List<SalaryRecordDTO> salaryRecords = Collections.singletonList(salaryRecordDTO);
        when(salaryService.getSalaryByEmployeeId("employee-1")).thenReturn(salaryRecords);

        ResponseEntity<List<SalaryRecordDTO>> response = salaryRecordController.getSalary("employee-1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    void testReleaseSalaryForEmployee_Success() throws ResourceNotFoundException {
        doNothing().when(salaryService).releaseSalaryForEmployee(salaryRecordDTO);

        ResponseEntity<Void> response = salaryRecordController.releaseSalaryForEmployee(salaryRecordDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void testReleaseSalaryForEmployee_NotFound() throws ResourceNotFoundException {
        doThrow(new ResourceNotFoundException("Resource not found")).when(salaryService).releaseSalaryForEmployee(salaryRecordDTO);

        ResponseEntity<Void> response = salaryRecordController.releaseSalaryForEmployee(salaryRecordDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testGetAllSalaries() {
        List<SalaryRecordDTO> salaryRecords = Collections.singletonList(salaryRecordDTO);
        when(salaryService.getAllSalaries()).thenReturn(salaryRecords);

        ResponseEntity<List<SalaryRecordDTO>> response = salaryRecordController.getAllSalaries();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    void testGetQuarterSalary_Success() throws ResourceNotFoundException {
        when(salaryService.getSalaryByQuarter("quarter 1")).thenReturn(salaryRecordDTO);

        ResponseEntity<SalaryRecordDTO> response = salaryRecordController.getQuarterSalary("quarter 1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(salaryRecordDTO);
    }

    @Test
    void testGetQuarterTax_Success() throws ResourceNotFoundException {
        when(salaryService.getTaxByQuarter("quarter 1", "employee-1")).thenReturn(taxResponseDTO);

        ResponseEntity<TaxResponseDTO> response = salaryRecordController.getQuarterTax("quarter 1", "employee-1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(taxResponseDTO);
    }

    @Test
    void testGetQuarterTax_NotFound() throws ResourceNotFoundException {
        when(salaryService.getTaxByQuarter("quarter 1", "employee-1")).thenThrow(new ResourceNotFoundException("Resource not found"));

        ResponseEntity<TaxResponseDTO> response = salaryRecordController.getQuarterTax("quarter 1", "employee-1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
