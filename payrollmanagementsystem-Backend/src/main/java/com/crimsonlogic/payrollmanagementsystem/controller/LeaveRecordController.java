package com.crimsonlogic.payrollmanagementsystem.controller;

import com.crimsonlogic.payrollmanagementsystem.dto.LeaveRecordDTO;
import com.crimsonlogic.payrollmanagementsystem.service.LeaveRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/*
  Controller for managing leave records within the payroll management system.
  This includes retrieving leave records for employees.

  @author abdulmanan
 */
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/leaverecord")
public class LeaveRecordController {

    private final LeaveRecordService leaveRecordService;

    /**
     * Constructs a LeaveRecordController with the specified LeaveRecordService.
     *
     * @param leaveRecordService the service used to handle leave record operations
     */
    LeaveRecordController(LeaveRecordService leaveRecordService) {
        super();
        this.leaveRecordService = leaveRecordService;
    }

    /**
     * Retrieves the leave record for a specific employee by their ID.
     *
     * @param employeeId the ID of the employee whose leave record is to be retrieved
     * @return a ResponseEntity containing a list of LeaveRecordDTOs for the specified employee
     */
    @GetMapping("/getleaverecord")
    public ResponseEntity<List<LeaveRecordDTO>> getLeaveRecord(@RequestParam String employeeId) {
        List<LeaveRecordDTO> leaveRecordDTO = leaveRecordService.getLeaveRecordById(employeeId);
        return ResponseEntity.ok(leaveRecordDTO);
    }
}
