package com.crimsonlogic.payrollmanagementsystem.controller;

import com.crimsonlogic.payrollmanagementsystem.dto.LoginLogsDTO;
import com.crimsonlogic.payrollmanagementsystem.service.LoginLogsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/*
  Controller for managing login logs within the payroll management system.
  This includes logging user logout events.

  @author abdulmanan
 */
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/loginlogs")
public class LoginLogsController {

    private final LoginLogsService loginLogsService;

    /**
     * Constructs a LoginLogsController with the specified LoginLogsService.
     *
     * @param loginLogsService the service used to handle login log operations
     */
    LoginLogsController(LoginLogsService loginLogsService) {
        super();
        this.loginLogsService = loginLogsService;
    }

    /**
     * Retrieves the login/logout records for a specific employee.
     *
     * @param employeeId the ID of the employee whose record is to be retrieved
     * @return a ResponseEntity containing a list of LoginLogsDTO for the specified employee
     */
    @GetMapping("/getlogs")
    public ResponseEntity<List<LoginLogsDTO>> getLogs(@RequestParam String employeeId) {
        List<LoginLogsDTO> loginLogsDTO=loginLogsService.getLogsForEmployee(employeeId);
        return ResponseEntity.ok(loginLogsDTO);
    }

    /**
     * Sets a logout log for a specified log ID.
     *
     * @param logId the ID of the log entry to be updated with a logout event
     */
    @PostMapping("/setlogoutlog")
    public void setLogoutLog(@RequestParam String logId) {
        loginLogsService.setLogoutLog(logId);
    }
}
