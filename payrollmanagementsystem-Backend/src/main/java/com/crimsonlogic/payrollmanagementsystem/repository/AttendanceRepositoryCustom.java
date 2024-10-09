package com.crimsonlogic.payrollmanagementsystem.repository;

/**
 * Custom repository interface for Attendance-related operations.
 * This interface defines additional methods for managing Attendance data
 * that are not covered by the standard JpaRepository.
 *
 * @author abdulmanan
 */
public interface AttendanceRepositoryCustom {

    /**
     * Inserts attendance records for employees who were absent.
     * This method can be implemented to handle specific business logic
     * for marking employees as absent.
     */
    void insertAbsentAttendance();
}
