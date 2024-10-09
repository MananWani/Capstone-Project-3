package com.crimsonlogic.payrollmanagementsystem.controller;

import com.crimsonlogic.payrollmanagementsystem.dto.SalaryRecordDTO;
import com.crimsonlogic.payrollmanagementsystem.dto.TaxResponseDTO;
import com.crimsonlogic.payrollmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.payrollmanagementsystem.service.SalaryRecordService;
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
  Controller for managing salary records within the payroll management system.
  This includes calculating salaries, releasing salaries, and retrieving salary records.

  @author abdulmanan
 */
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/salaryrecord")
public class SalaryRecordController {

    private final SalaryRecordService salaryService;

    /**
     * Constructs a SalaryRecordController with the specified SalaryRecordService.
     *
     * @param salaryService the service used to handle salary record operations
     */
    SalaryRecordController(SalaryRecordService salaryService) {
        super();
        this.salaryService = salaryService;
    }

    /**
     * Calculates the salary for a specific employee based on their employee ID.
     *
     * @param employeeId the ID of the employee whose salary is to be calculated
     * @return a ResponseEntity containing the SalaryRecordDTO with the calculated salary
     */
    @GetMapping("/calculatesalary")
    public ResponseEntity<SalaryRecordDTO> calculateSalaryForEmployee(@RequestParam String employeeId) {
        try {
            SalaryRecordDTO salaryRecordDTO = salaryService.calculateSalaryForEmployee(employeeId);
            return ResponseEntity.ok(salaryRecordDTO);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Retrieves the salary records for a specific employee based on their employee ID.
     *
     * @param employeeId the ID of the employee whose salary records are to be retrieved
     * @return a ResponseEntity containing a list of SalaryRecordDTOs
     */
    @GetMapping("/getsalary")
    public ResponseEntity<List<SalaryRecordDTO>> getSalary(@RequestParam String employeeId) {
        List<SalaryRecordDTO> salaryRecordDTO = salaryService.getSalaryByEmployeeId(employeeId);
        return ResponseEntity.ok(salaryRecordDTO);
    }

    /**
     * Releases the salary for a specific employee based on the provided SalaryRecordDTO.
     *
     * @param salaryRecordDTO the DTO containing the salary details to be released
     * @return a ResponseEntity indicating the result of the release operation
     */
    @PostMapping("/releasesalary")
    public ResponseEntity<Void> releaseSalaryForEmployee(@RequestBody SalaryRecordDTO salaryRecordDTO) {
        try {
            salaryService.releaseSalaryForEmployee(salaryRecordDTO);
            return ResponseEntity.status(201).build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Retrieves all salary records in the system.
     *
     * @return a ResponseEntity containing a list of SalaryRecordDTOs
     */
    @GetMapping("/getallsalaries")
    public ResponseEntity<List<SalaryRecordDTO>> getAllSalaries() {
        List<SalaryRecordDTO> salaryRecordDTO = salaryService.getAllSalaries();
        return ResponseEntity.ok(salaryRecordDTO);
    }

    /**
     * Retrieves the salary for a specific quarter.
     *
     * @param quarter the quarter for which to retrieve the salary
     * @return a ResponseEntity containing the SalaryRecordDTO for the specified quarter
     */
    @GetMapping("/getquartersalary")
    public ResponseEntity<SalaryRecordDTO> getQuarterSalary(@RequestParam String quarter) {
        try {
            SalaryRecordDTO salaryRecordDTO = salaryService.getSalaryByQuarter(quarter);
            return ResponseEntity.ok(salaryRecordDTO);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Retrieves the tax information for a specific quarter and employee.
     *
     * @param quarter the quarter for which to retrieve the tax information
     * @param employeeId the ID of the employee whose tax information is to be retrieved
     * @return a ResponseEntity containing the TaxResponseDTO with tax information
     */
    @GetMapping("/getquartertax")
    public ResponseEntity<TaxResponseDTO> getQuarterTax(@RequestParam String quarter,
                                                        @RequestParam String employeeId) {
        try {
            TaxResponseDTO taxResponseDTO = salaryService.getTaxByQuarter(quarter, employeeId);
            return ResponseEntity.ok(taxResponseDTO);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
