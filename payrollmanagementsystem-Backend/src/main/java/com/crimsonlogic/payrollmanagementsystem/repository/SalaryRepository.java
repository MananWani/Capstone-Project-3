package com.crimsonlogic.payrollmanagementsystem.repository;

import com.crimsonlogic.payrollmanagementsystem.domain.Employees;
import com.crimsonlogic.payrollmanagementsystem.domain.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

/**
 * Repository interface for managing Salary entities.
 * Provides CRUD operations and custom query methods for salary-related data.
 *
 * @author abdulmanan
 */
@Repository
public interface SalaryRepository extends JpaRepository<Salary, String> {

    /**
     * Find the cost to company (CTC) for a given employee.
     *
     * @param employee the employee whose cost to company is to be retrieved
     * @return the cost to company (CTC) as a BigDecimal
     */
    @Query("SELECT s.costToCompany FROM Salary s WHERE s.salaryOfEmployee = :employee")
    BigDecimal findCostToCompany(@Param("employee") Employees employee);
}
