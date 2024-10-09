package com.crimsonlogic.payrollmanagementsystem.controller;

import com.crimsonlogic.payrollmanagementsystem.dto.LeaveTypeDTO;
import com.crimsonlogic.payrollmanagementsystem.service.LeaveTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/*
  Controller for managing leave types within the payroll management system.
  This includes retrieving all leave types, adding a new leave type,
  and updating existing leave types.

  @author abdulmanan
 */
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/leavetype")
public class LeaveTypeController {

    private final LeaveTypeService leaveTypeService;

    /**
     * Constructs a LeaveTypeController with the specified LeaveTypeService.
     *
     * @param leaveTypeService the service used to handle leave type operations
     */
    public LeaveTypeController(LeaveTypeService leaveTypeService) {
        super();
        this.leaveTypeService = leaveTypeService;
    }

    /**
     * Retrieves all leave types in the system.
     *
     * @return a ResponseEntity containing a list of LeaveTypeDTOs
     */
    @GetMapping("/getalltypes")
    public ResponseEntity<List<LeaveTypeDTO>> getAllTypes() {
        List<LeaveTypeDTO> leaveTypeDTO = leaveTypeService.getAllTypes();
        return ResponseEntity.ok(leaveTypeDTO);
    }

    /**
     * Adds a new leave type based on the provided LeaveTypeDTO.
     *
     * @param newType the DTO containing the details of the leave type to be added
     * @return a ResponseEntity indicating the result of the addition operation
     */
    @PostMapping("/addleavetype")
    public ResponseEntity<Void> addLeaveType(@RequestBody LeaveTypeDTO newType) {
        Boolean isAdded = leaveTypeService.addLeaveType(newType);
        if (Boolean.FALSE.equals(isAdded)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(201).build();
    }

    /**
     * Updates an existing leave type based on the provided LeaveTypeDTO.
     *
     * @param updateType the DTO containing the updated details of the leave type
     * @return a ResponseEntity indicating the result of the update operation
     */
    @PostMapping("/updateleavetype")
    public ResponseEntity<Void> updateLeaveType(@RequestBody LeaveTypeDTO updateType) {
        boolean isUpdated = leaveTypeService.updateLeaveType(updateType);

        if (!isUpdated) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.status(201).build();
    }
}
