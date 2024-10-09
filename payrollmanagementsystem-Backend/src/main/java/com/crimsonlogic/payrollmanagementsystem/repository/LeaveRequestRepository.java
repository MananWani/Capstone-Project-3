package com.crimsonlogic.payrollmanagementsystem.repository;

import com.crimsonlogic.payrollmanagementsystem.domain.Employees;
import com.crimsonlogic.payrollmanagementsystem.domain.LeaveRequest;
import com.crimsonlogic.payrollmanagementsystem.domain.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing LeaveRequest entities.
 * Provides CRUD operations and custom queries for leave requests.
 *
 * @author abdulmanan
 */
@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, String> {

    /**
     * Finds all leave requests made by a specific employee.
     *
     * @param employee The employee for whom the leave requests are being queried.
     * @return List of LeaveRequest made by the specified employee.
     */
    List<LeaveRequest> findByRequestByEmployee(Employees employee);

    /**
     * Finds a leave request for a specific employee, year, month, and type of leave.
     *
     * @param employee The employee for whom the leave request is being queried.
     * @param year The year of the leave request.
     * @param month The month of the leave request.
     * @param type The type of leave being requested.
     * @return LeaveRequest if found, otherwise null.
     */
    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.requestByEmployee = :employee AND " +
            "EXTRACT(YEAR FROM lr.startDate) = :year " +
            "AND EXTRACT(MONTH FROM lr.startDate) = :month AND lr.typeOfLeave = :type")
    LeaveRequest findByRequestByEmployeeAndStartDateAndTypeOfLeave(
            @Param("employee") Employees employee,
            @Param("year") int year,
            @Param("month") int month,
            @Param("type") LeaveType type
    );

    /**
     * Finds all pending leave requests for a specific manager.
     *
     * @param manager The manager for whom the pending leave requests are being queried.
     * @return List of LeaveRequest that are pending for the specified manager.
     */
    @Query("SELECT lr FROM LeaveRequest lr " +
            "WHERE lr.requestByEmployee.manager = :manager " +
            "AND lr.status = 'Pending'")
    List<LeaveRequest> findPendingLeaveRequests(@Param("manager") Employees manager);
}
