package com.crimsonlogic.payrollmanagementsystem.repository;

import com.crimsonlogic.payrollmanagementsystem.domain.Employees;
import com.crimsonlogic.payrollmanagementsystem.domain.Queries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing Queries entities.
 * Provides CRUD operations for queries submitted by employees.
 *
 * @author abdulmanan
 */
@Repository
public interface QueriesRepository extends JpaRepository<Queries, String> {
    /**
     * Finds all queries submitted by a specific employee.
     *
     * @param employee the employee whose queries are to be retrieved
     * @return a list of queries submitted by the specified employee
     */
    List<Queries> findByQueryByEmployee(Employees employee);
}
