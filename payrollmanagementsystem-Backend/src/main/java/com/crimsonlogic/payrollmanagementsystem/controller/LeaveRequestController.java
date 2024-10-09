package com.crimsonlogic.payrollmanagementsystem.controller;

import com.crimsonlogic.payrollmanagementsystem.dto.LeaveRequestDTO;
import com.crimsonlogic.payrollmanagementsystem.exception.LeavesExhaustedException;
import com.crimsonlogic.payrollmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.payrollmanagementsystem.service.LeaveRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/*
  Controller for managing leave requests within the payroll management system.
  This includes retrieving leave requests, submitting new leave requests,
  updating, and canceling existing leave requests.

  @author abdulmanan
 */
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/leaverequest")
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    /**
     * Constructs a LeaveRequestController with the specified LeaveRequestService.
     *
     * @param leaveRequestService the service used to handle leave request operations
     */
    LeaveRequestController(LeaveRequestService leaveRequestService) {
        super();
        this.leaveRequestService = leaveRequestService;
    }

    /**
     * Retrieves all leave requests for a specific employee by their ID.
     *
     * @param employeeId the ID of the employee whose leave requests are to be retrieved
     * @return a ResponseEntity containing a list of LeaveRequestDTOs for the specified employee
     */
    @GetMapping("/getleaverequests")
    public ResponseEntity<List<LeaveRequestDTO>> getLeaveRequests(@RequestParam String employeeId) {
        List<LeaveRequestDTO> leaveRequestDTO = leaveRequestService.getLeaveRequestById(employeeId);
        return ResponseEntity.ok(leaveRequestDTO);
    }

    /**
     * Submits a new leave request based on the provided LeaveRequestDTO.
     *
     * @param leaveRequestDTO the DTO containing the details of the leave request
     * @return a ResponseEntity indicating the result of the leave request operation
     */
    @PostMapping("/requestleave")
    public ResponseEntity<String> requestLeave(@RequestBody LeaveRequestDTO leaveRequestDTO) {
        try {
            leaveRequestService.requestLeave(leaveRequestDTO);
            return ResponseEntity.status(201).build();
        } catch (LeavesExhaustedException | ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Retrieves all pending leave requests for a specific employee by their ID.
     *
     * @param employeeId the ID of the employee whose pending requests are to be retrieved
     * @return a ResponseEntity containing a list of LeaveRequestDTOs for pending requests
     */
    @GetMapping("/getpendingrequests")
    public ResponseEntity<List<LeaveRequestDTO>> getPendingRequests(@RequestParam String employeeId) {
        List<LeaveRequestDTO> leaveRequestDTO = leaveRequestService.getPendingRequestsById(employeeId);
        return ResponseEntity.ok(leaveRequestDTO);
    }

    /**
     * Updates an existing leave request based on the provided LeaveRequestDTO.
     *
     * @param leaveRequestDTO the DTO containing the updated details of the leave request
     * @return a ResponseEntity indicating the result of the update operation
     */
    @PostMapping("/updateleaverequest")
    public ResponseEntity<Void> updateLeaveRequest(@RequestBody LeaveRequestDTO leaveRequestDTO) {
        boolean isUpdated = leaveRequestService.updateLeaveRequest(leaveRequestDTO);

        if (!isUpdated) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.status(201).build();
    }

    /**
     * Cancels an existing leave request based on the provided LeaveRequestDTO.
     *
     * @param leaveRequestDTO the DTO containing the leave request ID to be canceled
     * @return a ResponseEntity indicating the result of the cancellation operation
     */
    @PostMapping("/cancelleaverequest")
    public ResponseEntity<Void> cancelLeaveRequest(@RequestBody LeaveRequestDTO leaveRequestDTO) {
        boolean isUpdated = leaveRequestService.cancelLeaveRequest(leaveRequestDTO.getLeaveRequestId());

        if (!isUpdated) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.status(201).build();
    }
}
