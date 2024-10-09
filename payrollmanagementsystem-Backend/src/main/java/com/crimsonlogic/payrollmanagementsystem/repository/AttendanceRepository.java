package com.crimsonlogic.payrollmanagementsystem.repository;

import com.crimsonlogic.payrollmanagementsystem.domain.Attendance;
import com.crimsonlogic.payrollmanagementsystem.domain.Employees;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for managing Attendance entities.
 * This interface extends JpaRepository, providing CRUD operations
 * and custom query methods for Attendance data.
 *
 * @author abdulmanan
 */
@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, String> {

    /**
     * Finds attendance records for a specific employee.
     *
     * @param employees the employee whose attendance records are to be retrieved
     * @return a list of Attendance records for the specified employee
     */
    List<Attendance> findByAttendanceByEmployee(Employees employees);

    /**
     * Finds attendance records for a specific employee for a given month and year.
     *
     * @param employee the employee whose attendance records are to be retrieved
     * @param currentMonth the month to filter by
     * @param currentYear the year to filter by
     * @return a list of Attendance records matching the specified employee, month, and year
     */
    @Query("SELECT a FROM Attendance a WHERE a.attendanceByEmployee = :employee " +
            "AND EXTRACT(MONTH FROM a.attendanceForDate) = :currentMonth " +
            "AND EXTRACT(YEAR FROM a.attendanceForDate) = :currentYear")
    List<Attendance> findByEmployeeAndDate(@Param("employee") Employees employee,
                                           @Param("currentMonth") int currentMonth,
                                           @Param("currentYear") int currentYear);

    /**
     * Finds attendance records for a specific employee for a given month.
     *
     * @param month the month to filter by
     * @param employee the employee whose attendance records are to be retrieved
     * @return a list of Attendance records matching the specified employee and month
     */
    @Query("SELECT a FROM Attendance a WHERE a.attendanceByEmployee = :employee " +
            "AND EXTRACT(MONTH FROM a.attendanceForDate) = :month ")
    List<Attendance> findByMonthAndEmployee(@Param("month") Integer month,
                                            @Param("employee") Employees employee);

    /**
     * Deletes attendance records for a specific employee within a specified date range.
     *
     * @param employee the employee whose attendance records are to be deleted
     * @param startDate the start date of the range
     * @param endDate the end date of the range
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Attendance a WHERE a.attendanceByEmployee = :employee " +
            "AND a.attendanceForDate BETWEEN :startDate AND :endDate")
    void deleteByEmployeeAndDateRange(
            @Param("employee") Employees employee,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
