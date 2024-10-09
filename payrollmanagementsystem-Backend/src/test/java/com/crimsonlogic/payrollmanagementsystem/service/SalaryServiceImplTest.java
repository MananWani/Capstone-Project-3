package com.crimsonlogic.payrollmanagementsystem.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import com.crimsonlogic.payrollmanagementsystem.domain.Employees;
import com.crimsonlogic.payrollmanagementsystem.domain.Salary;
import com.crimsonlogic.payrollmanagementsystem.dto.SalaryDTO;
import com.crimsonlogic.payrollmanagementsystem.repository.SalaryRepository;
import com.crimsonlogic.payrollmanagementsystem.serviceimpl.SalaryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SalaryServiceImplTest {

    @InjectMocks
    private SalaryServiceImpl salaryService;

    @Mock
    private SalaryRepository salaryRepository;

    private Employees employee;

    private Salary salary;

    private SalaryDTO salaryDTO;

    @BeforeEach
    void setUp() {

        employee = new Employees();
        employee.setEmployeeId("emp1");
        employee.setFullName("John Doe");

        salary = new Salary();
        salary.setSalaryId("sal1");
        salary.setSalaryOfEmployee(employee);
        salary.setCostToCompany(BigDecimal.valueOf(50000));

        salaryDTO = new SalaryDTO();
        salaryDTO.setSalaryId("sal1");
        salaryDTO.setCostToCompany(BigDecimal.valueOf(60000));
    }

    @Test
    void testGetCtcDetails() {
        when(salaryRepository.findAll()).thenReturn(Collections.singletonList(salary));

        List<SalaryDTO> ctcDetails = salaryService.getCtcDetails();

        assertThat(ctcDetails)
                .isNotNull()
                .hasSize(1);
        assertThat(ctcDetails.get(0).getFullName()).isEqualTo("John Doe");
        verify(salaryRepository).findAll();
    }

    @Test
    void testUpdateCtc_Success() {
        when(salaryRepository.findById(salary.getSalaryId())).thenReturn(Optional.of(salary));

        boolean result = salaryService.updateCtc(salaryDTO);

        assertThat(result).isTrue();
        assertThat(salary.getCostToCompany()).isEqualTo(salaryDTO.getCostToCompany());
        verify(salaryRepository).save(salary);
    }

    @Test
    void testUpdateCtc_SalaryNotFound() {
        when(salaryRepository.findById(anyString())).thenReturn(Optional.empty());

        boolean result = salaryService.updateCtc(salaryDTO);

        assertThat(result).isFalse();
        verify(salaryRepository, never()).save(any(Salary.class));
    }

    @Test
    void testSetSalary() {
        salaryService.setSalary(employee);

        verify(salaryRepository).save(any(Salary.class));
    }
}

