package com.crimsonlogic.payrollmanagementsystem.service;

import com.crimsonlogic.payrollmanagementsystem.dto.QueriesDTO;
import com.crimsonlogic.payrollmanagementsystem.exception.ResourceNotFoundException;

import java.util.List;

/**
 * Service interface for managing queries within the payroll management system.
 * Provides methods to add, retrieve, and respond to employee queries.
 *
 * @author abdulmanan
 */
public interface QueriesService {

    /**
     * Adds a new query submitted by an employee.
     *
     * @param queriesDTO the data transfer object containing query details
     * @throws ResourceNotFoundException if the employee associated with the query is not found
     */
    void addQuery(QueriesDTO queriesDTO) throws ResourceNotFoundException;

    /**
     * Retrieves all queries submitted by a specific employee.
     *
     * @param employeeId the ID of the employee whose queries are to be retrieved
     * @return a list of queries submitted by the employee
     */
    List<QueriesDTO> getQueryByEmployeeId(String employeeId);

    /**
     * Retrieves all queries in the system.
     *
     * @return a list of all queries
     */
    List<QueriesDTO> getAllQueries();

    /**
     * Provides a response to a specific query.
     *
     * @param queriesDTO the data transfer object containing query and response details
     * @return true if the response was successfully recorded, false otherwise
     */
    boolean responseToQuery(QueriesDTO queriesDTO);
}
