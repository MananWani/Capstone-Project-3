package com.crimsonlogic.payrollmanagementsystem.service;

import com.crimsonlogic.payrollmanagementsystem.domain.Employees;
import com.crimsonlogic.payrollmanagementsystem.dto.SalaryDTO;
import jakarta.transaction.Transactional;

import java.util.List;

/**
 * Service interface for managing employee salary details within the payroll management system.
 * Provides methods to retrieve and update cost-to-company (CTC) details as well as to set salary for employees.
 *
 * @author abdulmanan
 */
public interface SalaryService {

    /**
     * Retrieves a list of all cost-to-company (CTC) details for employees.
     *
     * @return a list of SalaryDTO containing CTC details
     */
    List<SalaryDTO> getCtcDetails();

    /**
     * Updates the cost-to-company (CTC) for a specific employee.
     *
     * @param salaryDTO the salary data transfer object containing the updated CTC information
     * @return true if the update was successful; false otherwise
     */
    boolean updateCtc(SalaryDTO salaryDTO);

    /**
     * Sets the salary for a specific employee.
     *
     * @param employee the employee for whom the salary is to be set
     */
    @Transactional
    void setSalary(Employees employee);
}
