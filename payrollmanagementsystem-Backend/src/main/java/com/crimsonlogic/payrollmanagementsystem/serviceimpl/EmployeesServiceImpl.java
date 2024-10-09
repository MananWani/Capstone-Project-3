package com.crimsonlogic.payrollmanagementsystem.serviceimpl;

import com.crimsonlogic.payrollmanagementsystem.domain.Designation;
import com.crimsonlogic.payrollmanagementsystem.domain.Employees;
import com.crimsonlogic.payrollmanagementsystem.domain.Roles;
import com.crimsonlogic.payrollmanagementsystem.domain.Users;
import com.crimsonlogic.payrollmanagementsystem.dto.EmployeesDTO;
import com.crimsonlogic.payrollmanagementsystem.exception.ResourceExistsException;
import com.crimsonlogic.payrollmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.payrollmanagementsystem.mapper.Mapper;
import com.crimsonlogic.payrollmanagementsystem.repository.DesignationRepository;
import com.crimsonlogic.payrollmanagementsystem.repository.EmployeesRepository;
import com.crimsonlogic.payrollmanagementsystem.repository.RolesRepository;
import com.crimsonlogic.payrollmanagementsystem.repository.UsersRepository;
import com.crimsonlogic.payrollmanagementsystem.service.EmployeesService;
import com.crimsonlogic.payrollmanagementsystem.service.LeaveRecordService;
import com.crimsonlogic.payrollmanagementsystem.service.SalaryService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the EmployeesService interface.
 * This class handles the business logic related to employee management,
 * including retrieving, adding, updating employee details, and managing
 * related entities like roles, designations, and user accounts.
 *
 * @author abdulmanan
 */
@Slf4j
@Service
public class EmployeesServiceImpl implements EmployeesService {

    // Repositories for accessing employee, designation, role, and user data
    private final EmployeesRepository employeesRepository;
    private final DesignationRepository designationRepository;
    private final UsersRepository usersRepository;
    private final RolesRepository rolesRepository;

    // Services for handling leave records and salary management
    private final LeaveRecordService leaveRecordService;
    private final SalaryService salaryRecordService;

    // Constructor to initialize the repositories and services
    public EmployeesServiceImpl(EmployeesRepository employeesRepository,
                                DesignationRepository designationRepository,
                                RolesRepository rolesRepository,
                                UsersRepository usersRepository,
                                LeaveRecordService leaveRecordService,
                                SalaryService salaryRecordService) {
        super();
        this.employeesRepository = employeesRepository;
        this.designationRepository = designationRepository;
        this.usersRepository = usersRepository;
        this.rolesRepository = rolesRepository;
        this.leaveRecordService = leaveRecordService;
        this.salaryRecordService = salaryRecordService;
    }

    @Override
    public List<EmployeesDTO> getAllEmployees() {
        log.info("inside getAllEmployees method");
        // Retrieve all employees and return their details as DTOs
        return employeesRepository.getEmployeeDetails();
    }

    @Override
    @Transactional
    public void addEmployees(EmployeesDTO newEmployee)
            throws ResourceExistsException, ResourceNotFoundException {
        log.info("inside addEmployees method");
        // Check if mobile number or email already exists
        if (employeesRepository.existsByMobileNumber(newEmployee.getMobileNumber())) {
            throw new ResourceExistsException("Mobile number already exists.");
        }
        if (usersRepository.existsByEmail(newEmployee.getEmail())) {
            throw new ResourceExistsException("Email already exists.");
        }

        // Retrieve designation, role, and manager from the repository
        Optional<Designation> designation = designationRepository.findById(newEmployee.getDesignation());
        Optional<Roles> role = rolesRepository.findById(newEmployee.getRole());
        Optional<Employees> manager = employeesRepository.findById(newEmployee.getManager());

        // Validate that all required entities are present
        if (manager.isPresent() && designation.isPresent() && role.isPresent()) {
            // Create a new user and employee, then save to the repository
            Users user = setUser(newEmployee.getEmail(), newEmployee.getPasswordHash(), role.get());
            Employees employee = Mapper.INSTANCE.dtoToEntityForEmployees(newEmployee);
            employee.setDesignation(designation.get());
            employee.setUser(user);
            employee.setManager(manager.get());
            employeesRepository.save(employee);
            leaveRecordService.setEmployeeLeaves(employee); // Set leave records for the new employee
            salaryRecordService.setSalary(employee); // Set salary for the new employee
        } else {
            throw new ResourceNotFoundException("Register failed. Please try again.");
        }
    }

    /**
     * Hashes the password using BCrypt.
     *
     * @param password the password to hash
     * @return the hashed password
     */
    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    @Override
    public List<EmployeesDTO> getManagers() {
        log.info("inside getManagers method");
        // Retrieve and return a list of managers with their IDs and full names
        return employeesRepository.findEmployeeIdAndFullNameByDesignation();
    }

    @Override
    @Transactional
    public boolean updateEmployee(EmployeesDTO oldEmployee) {
        log.info("inside updateEmployee method");
        // Find the existing employee and update their details
        Optional<Employees> employeeOpt = employeesRepository.findById(oldEmployee.getEmployeeId());
        if (employeeOpt.isPresent()) {
            Employees employee = employeeOpt.get();
            employee.setMobileNumber(oldEmployee.getMobileNumber());
            employee.setIsActive(oldEmployee.getIsActive());

            // Update manager and designation if present
            Optional<Employees> manager = employeesRepository.findById(oldEmployee.getManager());
            Optional<Designation> designation = designationRepository.findById(oldEmployee.getDesignation());
            if (manager.isPresent() && designation.isPresent()) {
                employee.setManager(manager.get());
                employee.setDesignation(designation.get());
            } else {
                return false; // Return false if manager or designation is not found
            }

            // Save the updated employee record
            employeesRepository.save(employee);
            return true; // Return true to indicate success
        }
        return false; // Return false if the employee was not found
    }

    @Override
    public EmployeesDTO getEmployeeById(String employeeId)
            throws ResourceNotFoundException {
        log.info("inside getEmployeeById method");
        // Retrieve employee details by ID and map to DTO
        return employeesRepository.findById(employeeId)
                .map(employee -> {
                    EmployeesDTO employeesDTO = Mapper.INSTANCE.entityToDtoForEmployees(employee);
                    employeesDTO.setDesignation(employee.getDesignation().getDesignationName());
                    employeesDTO.setManager(employee.getManager().getFullName());
                    employeesDTO.setEmail(employee.getUser().getEmail());
                    return employeesDTO; // Return the populated DTO
                })
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found.")); // Throw exception if not found
    }

    @Override
    public List<EmployeesDTO> getTeam(String employeeId) {
        log.info("inside getTeam method");
        // Retrieve the list of employees reporting to a specific manager
        List<EmployeesDTO> returnList = new ArrayList<>();
        Optional<Employees> employeeOpt = employeesRepository.findById(employeeId);
        if (employeeOpt.isPresent()) {
            Employees employee = employeeOpt.get();
            returnList = employeesRepository.findEmployeesReportingToManager(employee); // Get team members
            return returnList;
        }
        return returnList; // Return empty list if employee not found
    }

    @Override
    @Transactional
    public boolean updateRating(EmployeesDTO employeesDTO) {
        log.info("inside updateRating method");
        // Update the rating of an employee
        return employeesRepository.findById(employeesDTO.getEmployeeId())
                .map(employee -> {
                    employee.setRating(employeesDTO.getRating());
                    employeesRepository.save(employee);
                    return true; // Return true to indicate success
                })
                .orElse(false); // Return false if employee not found
    }

    @Transactional
    private Users setUser(String email, String password, Roles role) {
        log.info("inside setUser method");
        // Create and save a new user entity
        Users user = new Users();
        user.setEmail(email);
        user.setPasswordHash(hashPassword(password));
        user.setRole(role);
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        return usersRepository.save(user); // Save the user to the repository
    }
}
