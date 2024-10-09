package com.crimsonlogic.payrollmanagementsystem.service;

import com.crimsonlogic.payrollmanagementsystem.domain.Employees;
import com.crimsonlogic.payrollmanagementsystem.dto.LeaveRecordDTO;

import jakarta.transaction.Transactional;
import java.util.List;

/**
 * Service interface for managing employee leave records.
 * Provides methods to set leaves for employees and retrieve leave records.
 *
 * @author abdulmanan
 */
public interface LeaveRecordService {

    /**
     * Set the leave records for a specified employee.
     *
     * @param employee the employee whose leaves are to be set
     */
    @Transactional
    void setEmployeeLeaves(Employees employee);

    /**
     * Retrieve a list of leave records for a specific employee.
     *
     * @param employeeId the ID of the employee whose leave records are to be retrieved
     * @return a list of LeaveRecordDTO representing the leave records of the employee
     */
    List<LeaveRecordDTO> getLeaveRecordById(String employeeId);
}
