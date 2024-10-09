package com.crimsonlogic.payrollmanagementsystem.controller;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.crimsonlogic.payrollmanagementsystem.dto.SalaryDTO;
import com.crimsonlogic.payrollmanagementsystem.service.SalaryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class SalaryControllerTest {

    @InjectMocks
    private SalaryController salaryController;

    @Mock
    private SalaryService salaryService;

    private SalaryDTO salaryDTO;

    @BeforeEach
    void setUp() {

        salaryDTO = new SalaryDTO();
        salaryDTO.setSalaryId("salary-1");
        salaryDTO.setCostToCompany(BigDecimal.valueOf(500000));
    }

    @Test
    void testGetCtcDetails_Success() {
        List<SalaryDTO> salaryList = Collections.singletonList(salaryDTO);
        when(salaryService.getCtcDetails()).thenReturn(salaryList);

        ResponseEntity<List<SalaryDTO>> response = salaryController.getCtcDetails();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody().get(0).getSalaryId()).isEqualTo(salaryDTO.getSalaryId());
    }

    @Test
    void testUpdateCtcDetails_Success() {
        when(salaryService.updateCtc(any(SalaryDTO.class))).thenReturn(true);

        ResponseEntity<String> response = salaryController.updateCtcDetails(salaryDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void testUpdateCtcDetails_NotFound() {
        when(salaryService.updateCtc(any(SalaryDTO.class))).thenReturn(false);

        ResponseEntity<String> response = salaryController.updateCtcDetails(salaryDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}