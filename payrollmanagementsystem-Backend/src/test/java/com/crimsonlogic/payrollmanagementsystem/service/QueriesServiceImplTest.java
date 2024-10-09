package com.crimsonlogic.payrollmanagementsystem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.crimsonlogic.payrollmanagementsystem.domain.Employees;
import com.crimsonlogic.payrollmanagementsystem.domain.Queries;
import com.crimsonlogic.payrollmanagementsystem.domain.SalaryRecord;
import com.crimsonlogic.payrollmanagementsystem.dto.QueriesDTO;
import com.crimsonlogic.payrollmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.payrollmanagementsystem.repository.EmployeesRepository;
import com.crimsonlogic.payrollmanagementsystem.repository.QueriesRepository;
import com.crimsonlogic.payrollmanagementsystem.repository.SalaryRecordRepository;
import com.crimsonlogic.payrollmanagementsystem.serviceimpl.QueriesServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class QueriesServiceImplTest {

    @InjectMocks
    private QueriesServiceImpl queriesService;

    @Mock
    private QueriesRepository queriesRepository;

    @Mock
    private EmployeesRepository employeesRepository;

    @Mock
    private SalaryRecordRepository salaryRecordRepository;

    private Employees employee;

    private SalaryRecord salaryRecord;

    private QueriesDTO queriesDTO;

    @BeforeEach
    void setUp() {

        employee = new Employees();
        employee.setEmployeeId("emp1");

        salaryRecord = new SalaryRecord();
        salaryRecord.setSalaryRecordId("sal1");

        queriesDTO = new QueriesDTO();
        queriesDTO.setEmployeeId("emp1");
        queriesDTO.setSalaryRecordId("sal1");
        queriesDTO.setQueryDescription("Query Description");
    }

    @Test
    void testAddQuery_Success() throws ResourceNotFoundException {
        when(employeesRepository.findById(employee.getEmployeeId())).thenReturn(Optional.of(employee));
        when(salaryRecordRepository.findById(salaryRecord.getSalaryRecordId())).thenReturn(Optional.of(salaryRecord));
        when(queriesRepository.save(any(Queries.class))).thenReturn(new Queries());

        queriesService.addQuery(queriesDTO);

        verify(queriesRepository).save(any(Queries.class));
    }

    @Test
    void testAddQuery_EmployeeNotFound() {
        when(employeesRepository.findById(queriesDTO.getEmployeeId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> queriesService.addQuery(queriesDTO));

        verify(queriesRepository, never()).save(any(Queries.class));
    }

    @Test
    void testAddQuery_SalaryRecordNotFound() {
        when(employeesRepository.findById(employee.getEmployeeId())).thenReturn(Optional.of(employee));
        when(salaryRecordRepository.findById(queriesDTO.getSalaryRecordId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> queriesService.addQuery(queriesDTO));

        verify(queriesRepository, never()).save(any(Queries.class));
    }

    @Test
    void testGetQueryByEmployeeId_Success() {
        when(employeesRepository.findById(employee.getEmployeeId())).thenReturn(Optional.of(employee));
        List<Queries> queryList = Collections.singletonList(new Queries());
        when(queriesRepository.findByQueryByEmployee(employee)).thenReturn(queryList);

        List<QueriesDTO> queries = queriesService.getQueryByEmployeeId(employee.getEmployeeId());

        assertThat(queries)
                .isNotNull().
                hasSize(1);
        verify(queriesRepository).findByQueryByEmployee(employee);
    }

    @Test
    void testGetQueryByEmployeeId_EmployeeNotFound() {
        when(employeesRepository.findById(employee.getEmployeeId())).thenReturn(Optional.empty());

        List<QueriesDTO> queries = queriesService.getQueryByEmployeeId(employee.getEmployeeId());

        assertThat(queries)
                .isNotNull()
                .isEmpty();
        verify(queriesRepository, never()).findByQueryByEmployee(any(Employees.class));
    }

    @Test
    void testGetAllQueries() {
        when(queriesRepository.findAll()).thenReturn(Collections.singletonList(new Queries()));

        List<QueriesDTO> queries = queriesService.getAllQueries();

        assertThat(queries)
                .isNotNull()
                .hasSize(1);
        verify(queriesRepository).findAll();
    }

    @Test
    void testResponseToQuery_Success() {
        Queries existingQuery = new Queries();
        existingQuery.setQueryId("query1");
        when(queriesRepository.findById(existingQuery.getQueryId())).thenReturn(Optional.of(existingQuery));

        queriesDTO.setQueryId("query1");
        boolean result = queriesService.responseToQuery(queriesDTO);

        assertThat(result).isTrue();
        assertThat(existingQuery.getQueryStatus()).isEqualTo(queriesDTO.getQueryStatus());
        verify(queriesRepository).save(existingQuery);
    }

    @Test
    void testResponseToQuery_QueryNotFound() {
        queriesDTO.setQueryId("query1");
        when(queriesRepository.findById(queriesDTO.getQueryId())).thenReturn(Optional.empty());

        boolean result = queriesService.responseToQuery(queriesDTO);

        assertThat(result).isFalse();
        verify(queriesRepository, never()).save(any(Queries.class));
    }
}


