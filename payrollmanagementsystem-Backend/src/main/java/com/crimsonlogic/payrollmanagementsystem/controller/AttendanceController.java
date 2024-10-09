package com.crimsonlogic.payrollmanagementsystem.controller;

import com.crimsonlogic.payrollmanagementsystem.dto.AttendanceDTO;
import com.crimsonlogic.payrollmanagementsystem.dto.AttendanceResponseDTO;
import com.crimsonlogic.payrollmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.payrollmanagementsystem.service.AttendanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/*
  Controller for managing attendance-related operations.
  This includes marking attendance, retrieving attendance records for an employee,
  and getting team attendance for a specific month.

  @author abdulmanan
 */
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    /**
     * Constructs an AttendanceController with the specified AttendanceService.
     *
     * @param attendanceService the service used to handle attendance operations
     */
    public AttendanceController(AttendanceService attendanceService){
        super();
        this.attendanceService = attendanceService;
    }

    /**
     * Retrieves the attendance records for a specific employee.
     *
     * @param employeeId the ID of the employee whose attendance is to be retrieved
     * @return a ResponseEntity containing a list of AttendanceDTO for the specified employee
     */
    @GetMapping("/getattendanceforemployee")
    public ResponseEntity<List<AttendanceDTO>> getAttendanceForEmployee(@RequestParam String employeeId) {
        List<AttendanceDTO> attendanceList = attendanceService.getAttendanceForEmployee(employeeId);
        return ResponseEntity.ok(attendanceList);
    }

    /**
     * Marks attendance for an employee based on the provided AttendanceDTO.
     *
     * @param attendanceDTO the DTO containing attendance details to be marked
     * @return a ResponseEntity indicating the result of the attendance marking operation
     */
    @PostMapping("/markattendance")
    public ResponseEntity<String> markAttendance(@RequestBody AttendanceDTO attendanceDTO) {
        try {
            attendanceService.markAttendance(attendanceDTO);
            return ResponseEntity.status(201).build();
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    /**
     * Retrieves team attendance records for a specified month.
     *
     * @param month the month for which team attendance is to be retrieved
     * @param employeeId the ID of the employee associated with the team
     * @return a ResponseEntity containing AttendanceResponseDTO with team attendance records
     */
    @GetMapping("/getteamattendance")
    public ResponseEntity<AttendanceResponseDTO> getTeamAttendance(@RequestParam Integer month, @RequestParam String employeeId ) {
        try {
            AttendanceResponseDTO attendanceRecord = attendanceService.getTeamAttendance(month, employeeId);
            return ResponseEntity.ok(attendanceRecord);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Regularizes attendance for employees as per the defined rules.
     *
     * @return a ResponseEntity indicating the result of the regularization operation
     */
    @PostMapping("/regularize")
    public ResponseEntity<Void> regularize() {
        attendanceService.regularize();
        return ResponseEntity.status(201).build();
    }

}
