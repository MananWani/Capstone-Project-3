package com.crimsonlogic.payrollmanagementsystem.service;

import com.crimsonlogic.payrollmanagementsystem.dto.SalaryRecordDTO;
import com.crimsonlogic.payrollmanagementsystem.dto.TaxResponseDTO;
import com.crimsonlogic.payrollmanagementsystem.exception.ResourceNotFoundException;

import java.util.List;

/**
 * Service interface for managing salary records within the payroll management system.
 * Provides methods to calculate salaries, release payments, and retrieve salary details.
 *
 * @author abdulmanan
 */
public interface SalaryRecordService {

    /**
     * Calculates the salary for a specific employee based on their ID.
     *
     * @param employeeId the ID of the employee whose salary is to be calculated
     * @return the calculated salary record for the employee
     * @throws ResourceNotFoundException if the employee is not found
     */
    SalaryRecordDTO calculateSalaryForEmployee(String employeeId) throws ResourceNotFoundException;

    /**
     * Retrieves all salary records for a specific employee.
     *
     * @param employeeId the ID of the employee whose salary records are to be retrieved
     * @return a list of salary records for the specified employee
     */
    List<SalaryRecordDTO> getSalaryByEmployeeId(String employeeId);

    /**
     * Releases salary for a specific employee based on the provided salary record.
     *
     * @param salaryRecordDTO the salary record containing payment details
     * @throws ResourceNotFoundException if the salary record or employee is not found
     */
    void releaseSalaryForEmployee(SalaryRecordDTO salaryRecordDTO) throws ResourceNotFoundException;

    /**
     * Retrieves all salary records in the system.
     *
     * @return a list of all salary records
     */
    List<SalaryRecordDTO> getAllSalaries();

    /**
     * Retrieves the salary record for a specific quarter.
     *
     * @param quarter the quarter for which the salary record is requested
     * @return the salary record for the specified quarter
     * @throws ResourceNotFoundException if the salary record for the quarter is not found
     */
    SalaryRecordDTO getSalaryByQuarter(String quarter) throws ResourceNotFoundException;

    /**
     * Retrieves tax information for an employee for a specific quarter.
     *
     * @param quarter the quarter for which tax information is requested
     * @param employeeId the ID of the employee for whom the tax information is requested
     * @return the tax response data for the specified employee and quarter
     * @throws ResourceNotFoundException if the employee or tax information is not found
     */
    TaxResponseDTO getTaxByQuarter(String quarter, String employeeId) throws ResourceNotFoundException;
}
