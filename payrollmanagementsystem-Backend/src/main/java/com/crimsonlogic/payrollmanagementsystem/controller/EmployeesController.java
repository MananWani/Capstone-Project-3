package com.crimsonlogic.payrollmanagementsystem.controller;

import com.crimsonlogic.payrollmanagementsystem.dto.EmployeesDTO;
import com.crimsonlogic.payrollmanagementsystem.exception.ResourceExistsException;
import com.crimsonlogic.payrollmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.payrollmanagementsystem.service.EmployeesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/*
  Controller for managing employee-related operations within the payroll management system.
  This includes retrieving all employees, adding a new employee,
  updating employee details, and retrieving managers and team members.

  @author abdulmanan
 */
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/employees")
public class EmployeesController {

    private final EmployeesService employeesService;

    /**
     * Constructs an EmployeesController with the specified EmployeesService.
     *
     * @param employeesService the service used to handle employee operations
     */
    public EmployeesController(EmployeesService employeesService) {
        super();
        this.employeesService = employeesService;
    }

    /**
     * Retrieves all employees in the system.
     *
     * @return a ResponseEntity containing a list of EmployeesDTOs
     */
    @GetMapping("/getallemployees")
    public ResponseEntity<List<EmployeesDTO>> getAllEmployees() {
        List<EmployeesDTO> employees = employeesService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    /**
     * Adds a new employee based on the provided EmployeesDTO.
     *
     * @param newEmployee the DTO containing the details of the employee to be added
     * @return a ResponseEntity indicating the result of the addition operation
     */
    @PostMapping("/addemployee")
    public ResponseEntity<String> addEmployee(@RequestBody EmployeesDTO newEmployee) {
        try {
            employeesService.addEmployees(newEmployee);
            return ResponseEntity.status(201).build();
        } catch (ResourceExistsException | ResourceNotFoundException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    /**
     * Updates an existing employee based on the provided EmployeesDTO.
     *
     * @param oldEmployee the DTO containing the updated details of the employee
     * @return a ResponseEntity indicating the result of the update operation
     */
    @PostMapping("/updateemployee")
    public ResponseEntity<Void> updateEmployee(@RequestBody EmployeesDTO oldEmployee) {
        if (employeesService.updateEmployee(oldEmployee)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Retrieves a list of managers in the system.
     *
     * @return a ResponseEntity containing a list of EmployeesDTOs for managers
     */
    @GetMapping("/getmanagers")
    public ResponseEntity<List<EmployeesDTO>> getManagers() {
        List<EmployeesDTO> managers = employeesService.getManagers();
        return ResponseEntity.ok(managers);
    }

    /**
     * Retrieves details of a specific employee by their ID.
     *
     * @param employeeId the ID of the employee to retrieve
     * @return a ResponseEntity containing the EmployeesDTO for the specified employee
     */
    @GetMapping("/getemployee")
    public ResponseEntity<EmployeesDTO> getEmployee(@RequestParam String employeeId) {
        try {
            EmployeesDTO employee = employeesService.getEmployeeById(employeeId);
            return ResponseEntity.ok(employee);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Retrieves the team members of a specified employee.
     *
     * @param employeeId the ID of the employee whose team is to be retrieved
     * @return a ResponseEntity containing a list of EmployeesDTOs for team members
     */
    @GetMapping("/getteam")
    public ResponseEntity<List<EmployeesDTO>> getTeam(@RequestParam String employeeId) {
        List<EmployeesDTO> teamMembers = employeesService.getTeam(employeeId);
        return ResponseEntity.ok(teamMembers);
    }

    /**
     * Updates the rating of an employee based on the provided EmployeesDTO.
     *
     * @param employeesDTO the DTO containing the employee details and new rating
     * @return a ResponseEntity indicating the result of the rating update operation
     */
    @PostMapping("/updaterating")
    public ResponseEntity<Void> updateRating(@RequestBody EmployeesDTO employeesDTO) {
        if (employeesService.updateRating(employeesDTO)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
