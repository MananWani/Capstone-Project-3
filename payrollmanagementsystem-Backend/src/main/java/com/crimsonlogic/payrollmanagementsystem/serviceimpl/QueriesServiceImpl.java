package com.crimsonlogic.payrollmanagementsystem.serviceimpl;

import com.crimsonlogic.payrollmanagementsystem.domain.Employees;
import com.crimsonlogic.payrollmanagementsystem.domain.Queries;
import com.crimsonlogic.payrollmanagementsystem.domain.SalaryRecord;
import com.crimsonlogic.payrollmanagementsystem.dto.QueriesDTO;
import com.crimsonlogic.payrollmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.payrollmanagementsystem.mapper.Mapper;
import com.crimsonlogic.payrollmanagementsystem.repository.EmployeesRepository;
import com.crimsonlogic.payrollmanagementsystem.repository.QueriesRepository;
import com.crimsonlogic.payrollmanagementsystem.repository.SalaryRecordRepository;
import com.crimsonlogic.payrollmanagementsystem.service.QueriesService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * QueriesServiceImpl implements QueriesService to manage employee queries related to salary records.
 * It provides methods to add queries, retrieve them, and respond to queries.
 *
 * @author abdulmanan
 */
@Slf4j
@Service
public class QueriesServiceImpl implements QueriesService {

    private final QueriesRepository queriesRepository;
    private final EmployeesRepository employeesRepository;
    private final SalaryRecordRepository salaryRecordRepository;

    // Constructor to initialize repositories
    QueriesServiceImpl(QueriesRepository queriesRepository,
                       EmployeesRepository employeesRepository,
                       SalaryRecordRepository salaryRecordRepository) {
        super();
        this.queriesRepository = queriesRepository;
        this.employeesRepository = employeesRepository;
        this.salaryRecordRepository = salaryRecordRepository;
    }

    @Override
    public void addQuery(QueriesDTO queriesDTO) throws ResourceNotFoundException {
        log.info("inside addQuery method");
        // Find the employee by ID; throw an exception if not found
        Employees employee = employeesRepository.findById(queriesDTO.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        // Find the salary record by ID; throw an exception if not found
        SalaryRecord salaryRecord = salaryRecordRepository.findById(queriesDTO.getSalaryRecordId())
                .orElseThrow(() -> new ResourceNotFoundException("Salary record not found"));

        // Create a new Queries entity and set its properties
        Queries queries = new Queries();
        queries.setQueryDescription(queriesDTO.getQueryDescription());
        queries.setQueryStatus("In Progress"); // Set initial status
        queries.setQueryByEmployee(employee);
        queries.setQueryForSalaryRecord(salaryRecord);
        queries.setComment("No comment."); // Default comment

        // Save the new query to the repository
        queriesRepository.save(queries);
    }

    @Override
    public List<QueriesDTO> getQueryByEmployeeId(String employeeId) {
        log.info("inside getQueryByEmployeeId method");
        // Find the employee and retrieve their queries, converting to DTOs
        return employeesRepository.findById(employeeId)
                .map(queriesRepository::findByQueryByEmployee)
                .orElseGet(ArrayList::new) // Return an empty list if not found
                .stream()
                .map(Mapper.INSTANCE::entityToDtoForQueries) // Convert to DTOs
                .toList();
    }

    @Override
    public List<QueriesDTO> getAllQueries() {
        log.info("inside getAllQueries method");
        // Retrieve all queries and convert them to DTOs
        return queriesRepository.findAll().stream()
                .map(Mapper.INSTANCE::entityToDtoForQueries) // Convert to DTOs
                .toList();
    }

    @Override
    @Transactional
    public boolean responseToQuery(QueriesDTO queriesDTO) {
        log.info("inside responseToQuery method");
        // Find the existing query by ID and update its status and comment
        return queriesRepository.findById(queriesDTO.getQueryId())
                .map(existingQuery -> {
                    existingQuery.setQueryStatus(queriesDTO.getQueryStatus());
                    existingQuery.setComment(queriesDTO.getComment());
                    queriesRepository.save(existingQuery); // Save the updated query
                    return true; // Return true to indicate success
                })
                .orElse(false); // Return false if the query was not found
    }
}
