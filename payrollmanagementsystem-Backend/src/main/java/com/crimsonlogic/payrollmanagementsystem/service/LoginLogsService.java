package com.crimsonlogic.payrollmanagementsystem.service;

import com.crimsonlogic.payrollmanagementsystem.domain.Employees;
import com.crimsonlogic.payrollmanagementsystem.dto.LoginLogsDTO;
import jakarta.transaction.Transactional;

import java.util.List;

/**
 * Service interface for managing login and logout logs within the payroll management system.
 * Provides methods to record employee login and logout activities.
 *
 * @author abdulmanan
 */
public interface LoginLogsService {

    /**
     * Records the login activity of an employee.
     *
     * @param employee the employee whose login is being recorded
     * @return the log ID of the created login record
     */
    @Transactional
    String setLoginLog(Employees employee);

    /**
     * Records the logout activity of an employee.
     *
     * @param logId the ID of the login record to be updated with logout information
     */
    void setLogoutLog(String logId);

    /**
     * Retrieve the log records for a specific employee.
     *
     * @param employeeId the ID of the employee whose record is to be retrieved
     * @return a list of LoginLogsDTO containing log details for the employee
     */
    List<LoginLogsDTO> getLogsForEmployee(String employeeId);
}
