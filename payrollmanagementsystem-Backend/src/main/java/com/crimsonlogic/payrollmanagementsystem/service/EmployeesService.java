package com.crimsonlogic.payrollmanagementsystem.service;

import com.crimsonlogic.payrollmanagementsystem.dto.EmployeesDTO;
import com.crimsonlogic.payrollmanagementsystem.exception.ResourceExistsException;
import com.crimsonlogic.payrollmanagementsystem.exception.ResourceNotFoundException;

import java.util.List;

/**
 * Service interface for managing employee records.
 * Provides methods to retrieve, add, update, and manage employee details.
 *
 * @author abdulmanan
 */
public interface EmployeesService {

    /**
     * Retrieve a list of all employees.
     *
     * @return a list of EmployeesDTO representing all employees
     */
    List<EmployeesDTO> getAllEmployees();

    /**
     * Add a new employee.
     *
     * @param newEmployee the details of the employee to be added
     * @throws ResourceExistsException if the employee already exists
     * @throws ResourceNotFoundException if a related resource is not found
     */
    void addEmployees(EmployeesDTO newEmployee) throws ResourceExistsException, ResourceNotFoundException;

    /**
     * Retrieve a list of all managers.
     *
     * @return a list of EmployeesDTO representing managers
     */
    List<EmployeesDTO> getManagers();

    /**
     * Update an existing employee's details.
     *
     * @param oldEmployee the updated details of the employee
     * @return true if the employee was updated successfully, false otherwise
     */
    boolean updateEmployee(EmployeesDTO oldEmployee);

    /**
     * Retrieve an employee by their ID.
     *
     * @param employeeId the ID of the employee to retrieve
     * @return the EmployeesDTO of the requested employee
     * @throws ResourceNotFoundException if the employee is not found
     */
    EmployeesDTO getEmployeeById(String employeeId) throws ResourceNotFoundException;

    /**
     * Retrieve the team members of a specific employee.
     *
     * @param employeeId the ID of the employee whose team is to be retrieved
     * @return a list of EmployeesDTO representing the team members
     */
    List<EmployeesDTO> getTeam(String employeeId);

    /**
     * Update the rating of an employee.
     *
     * @param employeesDTO the employee details with updated rating
     * @return true if the rating was updated successfully, false otherwise
     */
    boolean updateRating(EmployeesDTO employeesDTO);
}
