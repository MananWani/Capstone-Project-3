package com.crimsonlogic.payrollmanagementsystem.controller;

import com.crimsonlogic.payrollmanagementsystem.dto.SalaryDTO;
import com.crimsonlogic.payrollmanagementsystem.service.SalaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/*
  Controller for managing salary details within the payroll management system.
  This includes retrieving CTC details and updating CTC information.

  @author abdulmanan
 */
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/salary")
public class SalaryController {

    private final SalaryService salaryService;

    /**
     * Constructs a SalaryController with the specified SalaryService.
     *
     * @param salaryService the service used to handle salary operations
     */
    SalaryController(SalaryService salaryService) {
        super();
        this.salaryService = salaryService;
    }

    /**
     * Retrieves CTC details for all employees.
     *
     * @return a ResponseEntity containing a list of SalaryDTOs with CTC details
     */
    @GetMapping("/getctcdetails")
    public ResponseEntity<List<SalaryDTO>> getCtcDetails() {
        List<SalaryDTO> roles = salaryService.getCtcDetails();
        return ResponseEntity.ok(roles);
    }

    /**
     * Updates the CTC details based on the provided SalaryDTO.
     *
     * @param salaryDTO the DTO containing the updated CTC details
     * @return a ResponseEntity indicating the result of the update operation
     */
    @PostMapping("/updatectcdetails")
    public ResponseEntity<String> updateCtcDetails(@RequestBody SalaryDTO salaryDTO) {
        boolean isUpdated = salaryService.updateCtc(salaryDTO);

        if (!isUpdated) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.status(201).build();
    }
}
