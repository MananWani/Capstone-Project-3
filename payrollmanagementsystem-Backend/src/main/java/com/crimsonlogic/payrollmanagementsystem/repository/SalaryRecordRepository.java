package com.crimsonlogic.payrollmanagementsystem.repository;

import com.crimsonlogic.payrollmanagementsystem.domain.Employees;
import com.crimsonlogic.payrollmanagementsystem.domain.SalaryRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for managing SalaryRecord entities.
 * Provides CRUD operations and custom query methods for salary records within the payroll management system.
 *
 * @author abdulmanan
 */
@Repository
public interface SalaryRecordRepository extends JpaRepository<SalaryRecord, String> {

    /**
     * Find all salary records for a given employee.
     *
     * @param employee the employee whose salary records are to be retrieved
     * @return a list of SalaryRecord for the specified employee
     */
    List<SalaryRecord> findBySalaryRecordOfEmployee(Employees employee);

    /**
     * Find salary records within a specific date range (quarter).
     *
     * @param startDate the start date of the pay period
     * @param endDate   the end date of the pay period
     * @return a list of SalaryRecord within the specified date range
     */
    @Query("SELECT sr FROM SalaryRecord sr WHERE sr.payPeriodStart BETWEEN :startDate AND :endDate")
    List<SalaryRecord> findSalaryRecordsByQuarter(@Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);

    /**
     * Find salary records for a specific employee within a specific date range (quarter).
     *
     * @param startDate the start date of the pay period
     * @param endDate   the end date of the pay period
     * @param employee   the employee whose salary records are to be retrieved
     * @return a list of SalaryRecord for the specified employee within the date range
     */
    @Query("SELECT sr FROM SalaryRecord sr WHERE sr.salaryRecordOfEmployee = :employee AND sr.payPeriodStart BETWEEN :startDate AND :endDate")
    List<SalaryRecord> findSalaryRecordsByQuarterAndEmployee(@Param("startDate") LocalDate startDate,
                                                             @Param("endDate") LocalDate endDate,
                                                             @Param("employee") Employees employee);
}
