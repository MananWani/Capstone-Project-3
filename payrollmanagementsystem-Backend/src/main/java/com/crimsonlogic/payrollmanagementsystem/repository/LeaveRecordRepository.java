package com.crimsonlogic.payrollmanagementsystem.repository;

import com.crimsonlogic.payrollmanagementsystem.domain.Employees;
import com.crimsonlogic.payrollmanagementsystem.domain.LeaveRecord;
import com.crimsonlogic.payrollmanagementsystem.domain.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing LeaveRecord entities.
 * Provides CRUD operations and custom queries for leave records.
 *
 * @author abdulmanan
 */
@Repository
public interface LeaveRecordRepository extends JpaRepository<LeaveRecord, String> {

    /**
     * Finds a leave record for a specific employee and type of leave.
     *
     * @param employee The employee for whom the leave record is being queried.
     * @param leaveType The type of leave to be checked.
     * @return LeaveRecord if found, otherwise null.
     */
    @Query("SELECT lr FROM LeaveRecord lr WHERE lr.leaveForEmployee = :employee " +
            "AND lr.typeOfLeave = :leaveType")
    LeaveRecord findByLeaveForEmployeeAndTypeOfLeave(
            @Param("employee") Employees employee,
            @Param("leaveType") LeaveType leaveType
    );

    /**
     * Finds all leave records for a specific employee.
     *
     * @param employee The employee for whom the leave records are being queried.
     * @return List of LeaveRecord for the specified employee.
     */
    List<LeaveRecord> findByLeaveForEmployee(Employees employee);
}
