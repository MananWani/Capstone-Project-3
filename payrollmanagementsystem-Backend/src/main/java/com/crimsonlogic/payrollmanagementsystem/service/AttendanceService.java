package com.crimsonlogic.payrollmanagementsystem.service;

import com.crimsonlogic.payrollmanagementsystem.dto.AttendanceDTO;
import com.crimsonlogic.payrollmanagementsystem.dto.AttendanceResponseDTO;
import com.crimsonlogic.payrollmanagementsystem.exception.ResourceNotFoundException;

import java.util.List;

/**
 * Service interface for managing employee attendance.
 * Provides methods to retrieve and manipulate attendance data.
 *
 * @author abdulmanan
 */
public interface AttendanceService {

    /**
     * Retrieve the attendance records for a specific employee.
     *
     * @param employeeId the ID of the employee whose attendance is to be retrieved
     * @return a list of AttendanceDTO containing attendance details for the employee
     */
    List<AttendanceDTO> getAttendanceForEmployee(String employeeId);

    /**
     * Mark attendance for an employee.
     *
     * @param attendanceDTO the attendance details to be marked
     * @throws ResourceNotFoundException if the employee is not found
     */
    void markAttendance(AttendanceDTO attendanceDTO) throws ResourceNotFoundException;

    /**
     * Get the attendance summary for a team in a specific month.
     *
     * @param month the month for which the attendance summary is to be retrieved
     * @param employeeId the ID of the employee whose team attendance is requested
     * @return an AttendanceResponseDTO containing the team's attendance summary
     * @throws ResourceNotFoundException if the team or employee is not found
     */
    AttendanceResponseDTO getTeamAttendance(Integer month, String employeeId) throws ResourceNotFoundException;

    /**
     * Regularize attendance for employees as necessary.
     */
    void regularize();
}
