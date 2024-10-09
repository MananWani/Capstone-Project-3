package com.crimsonlogic.payrollmanagementsystem.controller;

import com.crimsonlogic.payrollmanagementsystem.dto.DesignationDTO;
import com.crimsonlogic.payrollmanagementsystem.service.DesignationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/*
  Controller for managing designations within the payroll management system.
  This includes retrieving all designations, adding a new designation,
  and updating an existing designation.

  @author abdulmanan
 */
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/designation")
public class DesignationController {

    private final DesignationService designationService;

    /**
     * Constructs a DesignationController with the specified DesignationService.
     *
     * @param designationService the service used to handle designation operations
     */
    public DesignationController(DesignationService designationService) {
        super();
        this.designationService = designationService;
    }

    /**
     * Retrieves all designations in the system.
     *
     * @return a ResponseEntity containing a list of DesignationDTOs
     */
    @GetMapping("/getalldesignations")
    public ResponseEntity<List<DesignationDTO>> getAllDesignations() {
        List<DesignationDTO> designations = designationService.getAllDesignations();
        return ResponseEntity.ok(designations);
    }

    /**
     * Adds a new designation based on the provided DesignationDTO.
     *
     * @param newDesignation the DTO containing the details of the designation to be added
     * @return a ResponseEntity indicating the result of the addition operation
     */
    @PostMapping("/adddesignation")
    public ResponseEntity<Void> addDesignation(@RequestBody DesignationDTO newDesignation) {
        boolean isAdded = designationService.addDesignation(newDesignation);
        if (!isAdded) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(201).build();
    }

    /**
     * Updates an existing designation based on the provided DesignationDTO.
     *
     * @param updateDesignation the DTO containing the updated details of the designation
     * @return a ResponseEntity indicating the result of the update operation
     */
    @PostMapping("/updatedesignation")
    public ResponseEntity<Void> updateDesignation(@RequestBody DesignationDTO updateDesignation) {
        boolean isUpdated = designationService.updateDesignation(updateDesignation);

        if (!isUpdated) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.status(201).build();
    }
}
