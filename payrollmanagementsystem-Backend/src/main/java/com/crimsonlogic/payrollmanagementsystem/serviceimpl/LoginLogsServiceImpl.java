package com.crimsonlogic.payrollmanagementsystem.serviceimpl;

import com.crimsonlogic.payrollmanagementsystem.domain.Employees;
import com.crimsonlogic.payrollmanagementsystem.domain.LoginLogs;
import com.crimsonlogic.payrollmanagementsystem.dto.LoginLogsDTO;
import com.crimsonlogic.payrollmanagementsystem.mapper.Mapper;
import com.crimsonlogic.payrollmanagementsystem.repository.EmployeesRepository;
import com.crimsonlogic.payrollmanagementsystem.repository.LoginLogsRepository;
import com.crimsonlogic.payrollmanagementsystem.service.LoginLogsService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * LoginLogsServiceImpl implements LoginLogsService to manage login and logout logs for employees.
 * It provides methods to create and update log entries.
 *
 * @author abdulmanan
 */
@Slf4j
@Service
public class LoginLogsServiceImpl implements LoginLogsService {

    private final LoginLogsRepository loginLogsRepository;
    private final EmployeesRepository employeesRepository;

    // Constructor to initialize the LoginLogsRepository
    LoginLogsServiceImpl(LoginLogsRepository loginLogsRepository,
                         EmployeesRepository employeesRepository) {
        super();
        this.loginLogsRepository = loginLogsRepository;
        this.employeesRepository = employeesRepository;
    }

    @Override
    @Transactional
    public String setLoginLog(Employees employee) {
        log.info("inside setLoginLog method");
        // Create a new login log entry for the given employee
        LoginLogs log = new LoginLogs();

        // Capture the current timestamp for the login time
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        log.setLoginTime(now);
        log.setLogForEmployee(employee);

        // Save the login log to the repository
        loginLogsRepository.save(log);
        return log.getLogId(); // Return the ID of the created log
    }

    @Override
    @Transactional
    public void setLogoutLog(String logId) {
        log.info("inside setLogoutLog method");
        // Find the existing login log entry by its ID
        Optional<LoginLogs> logOpt = loginLogsRepository.findById(logId);

        // Capture the current timestamp for the logout time
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        // If the log entry is found, update the logout time
        if (logOpt.isPresent()) {
            LoginLogs log = logOpt.get();
            log.setLogoutTime(now);
            loginLogsRepository.save(log); // Save the updated log entry
        }
    }

    @Override
    public List<LoginLogsDTO> getLogsForEmployee(String employeeId) {
        log.info("inside getLogsForEmployee method");
        // Find the employee by ID
        return employeesRepository.findById(employeeId)
                // If the employee exists, process their login logs
                .map(employee -> loginLogsRepository.findByLogForEmployee(employee)
                        // Convert the list of logs to DTOs
                        .stream()
                        .map(Mapper.INSTANCE::entityTODtoForLoginLogs)
                        .toList()) // Collect the results into a List
                .orElseGet(ArrayList::new); // Return an empty list if the employee is not found
    }

}
