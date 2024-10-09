package com.crimsonlogic.payrollmanagementsystem.service;

import com.crimsonlogic.payrollmanagementsystem.dto.LeaveRequestDTO;
import com.crimsonlogic.payrollmanagementsystem.exception.LeavesExhaustedException;
import com.crimsonlogic.payrollmanagementsystem.exception.ResourceNotFoundException;

import java.util.List;

/**
 * Service interface for managing employee leave requests.
 * Provides methods to handle leave requests including retrieval, submission, and updates.
 *
 * @author abdulmanan
 */
public interface LeaveRequestService {

    /**
     * Retrieve a list of leave requests for a specific employee.
     *
     * @param employeeId the ID of the employee whose leave requests are to be retrieved
     * @return a list of LeaveRequestDTO representing the leave requests of the employee
     */
    List<LeaveRequestDTO> getLeaveRequestById(String employeeId);

    /**
     * Request leave for an employee.
     *
     * @param leaveRequestDTO the leave request details
     * @throws LeavesExhaustedException if the employee has exhausted their leave
     * @throws ResourceNotFoundException if the employee is not found
     */
    void requestLeave(LeaveRequestDTO leaveRequestDTO) throws LeavesExhaustedException, ResourceNotFoundException;

    /**
     * Retrieve a list of pending leave requests for a specific employee.
     *
     * @param employeeId the ID of the employee whose pending leave requests are to be retrieved
     * @return a list of LeaveRequestDTO representing the pending leave requests
     */
    List<LeaveRequestDTO> getPendingRequestsById(String employeeId);

    /**
     * Update an existing leave request.
     *
     * @param leaveRequestDTO the leave request details to update
     * @return true if the update was successful, false otherwise
     */
    boolean updateLeaveRequest(LeaveRequestDTO leaveRequestDTO);

    /**
     * Cancel a leave request.
     *
     * @param leaveRequestId the ID of the leave request to cancel
     * @return true if the cancellation was successful, false otherwise
     */
    boolean cancelLeaveRequest(String leaveRequestId);
}
