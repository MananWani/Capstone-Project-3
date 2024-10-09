package com.crimsonlogic.payrollmanagementsystem.service;

import com.crimsonlogic.payrollmanagementsystem.dto.LeaveTypeDTO;

import java.util.List;

/**
 * Service interface for managing leave types within the payroll management system.
 * Provides methods to retrieve, add, and update leave types.
 *
 * @author abdulmanan
 */
public interface LeaveTypeService {

    /**
     * Retrieve a list of all leave types.
     *
     * @return a list of LeaveTypeDTO representing all available leave types
     */
    List<LeaveTypeDTO> getAllTypes();

    /**
     * Add a new leave type.
     *
     * @param newType the leave type details to be added
     * @return true if the addition was successful, false otherwise
     */
    Boolean addLeaveType(LeaveTypeDTO newType);

    /**
     * Update an existing leave type.
     *
     * @param updateType the leave type details to be updated
     * @return true if the update was successful, false otherwise
     */
    boolean updateLeaveType(LeaveTypeDTO updateType);
}
