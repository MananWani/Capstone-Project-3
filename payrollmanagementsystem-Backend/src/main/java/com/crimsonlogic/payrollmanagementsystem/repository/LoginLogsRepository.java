package com.crimsonlogic.payrollmanagementsystem.repository;

import com.crimsonlogic.payrollmanagementsystem.domain.Employees;
import com.crimsonlogic.payrollmanagementsystem.domain.LoginLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository interface for managing LoginLogs entities.
 * Provides CRUD operations for login logs.
 *
 * @author abdulmanan
 */
@Repository
public interface LoginLogsRepository extends JpaRepository<LoginLogs, String> {

    /**
     * Finds log records for a specific employee.
     *
     * @param employee the employee whose log records are to be retrieved
     * @return a list of LoginLogs records for the specified employee
     */
    List<LoginLogs> findByLogForEmployee(Employees employee);
}
