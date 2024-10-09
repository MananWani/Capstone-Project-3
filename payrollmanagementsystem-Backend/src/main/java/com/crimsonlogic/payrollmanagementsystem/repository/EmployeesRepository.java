package com.crimsonlogic.payrollmanagementsystem.repository;

import com.crimsonlogic.payrollmanagementsystem.domain.Employees;
import com.crimsonlogic.payrollmanagementsystem.domain.Users;
import com.crimsonlogic.payrollmanagementsystem.dto.EmployeesDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing Employees entities.
 * Provides CRUD operations and custom queries for Employee records.
 *
 * @author abdulmanan
 */
@Repository
public interface EmployeesRepository extends JpaRepository<Employees, String> {

    /**
     * Finds employee IDs and full names for employees with designations of 'Manager' or 'General Manager'.
     *
     * @return List of EmployeesDTO containing employeeId and fullName.
     */
    @Query("SELECT new com.crimsonlogic.payrollmanagementsystem.dto.EmployeesDTO(e.employeeId, e.fullName) " +
            "FROM Employees e WHERE e.designation.designationName IN ('Manager', 'General Manager')")
    List<EmployeesDTO> findEmployeeIdAndFullNameByDesignation();

    /**
     * Gets detailed information of all employees.
     *
     * @return List of EmployeesDTO with full details of employees.
     */
    @Query("SELECT new com.crimsonlogic.payrollmanagementsystem.dto.EmployeesDTO(e.employeeId, e.fullName, e.mobileNumber, " +
            "e.user.email, e.isActive, e.designation.designationName, e.manager.fullName) FROM Employees e")
    List<EmployeesDTO> getEmployeeDetails();

    /**
     * Checks if an employee exists by mobile number.
     *
     * @param mobileNumber Mobile number to check.
     * @return true if exists, false otherwise.
     */
    boolean existsByMobileNumber(String mobileNumber);

    /**
     * Finds an employee by associated user.
     *
     * @param user User entity associated with the employee.
     * @return Employee associated with the user.
     */
    Employees findByUser(Users user);

    /**
     * Finds the full name of an employee by user.
     *
     * @param user User entity associated with the employee.
     * @return Full name of the employee.
     */
    @Query("SELECT e.fullName FROM Employees e WHERE e.user = :user")
    String findFullNameByUser(Users user);

    /**
     * Finds employees reporting to a specific manager.
     *
     * @param employee Manager whose reportees are to be found.
     * @return List of EmployeesDTO for employees reporting to the manager.
     */
    @Query("SELECT new com.crimsonlogic.payrollmanagementsystem.dto.EmployeesDTO(e.employeeId, e.fullName, e.rating) " +
            "FROM Employees e WHERE e.manager = :employee")
    List<EmployeesDTO> findEmployeesReportingToManager(Employees employee);
}
