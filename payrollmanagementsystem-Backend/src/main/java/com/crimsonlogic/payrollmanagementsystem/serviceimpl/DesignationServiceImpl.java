package com.crimsonlogic.payrollmanagementsystem.serviceimpl;

import com.crimsonlogic.payrollmanagementsystem.domain.Designation;
import com.crimsonlogic.payrollmanagementsystem.dto.DesignationDTO;
import com.crimsonlogic.payrollmanagementsystem.mapper.Mapper;
import com.crimsonlogic.payrollmanagementsystem.repository.DesignationRepository;
import com.crimsonlogic.payrollmanagementsystem.service.DesignationService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of the DesignationService interface.
 * This class handles the business logic related to designations,
 * including retrieving, adding, and updating designations in the system.
 *
 * @author abdulmanan
 */
@Slf4j
@Service
public class DesignationServiceImpl implements DesignationService {

    // Repository for accessing designation data
    private final DesignationRepository designationRepository;

    // Constructor to initialize the designation repository
    public DesignationServiceImpl(DesignationRepository designationRepository) {
        super();
        this.designationRepository = designationRepository;
    }

    @Override
    public List<DesignationDTO> getAllDesignations() {
        log.info("inside getAllDesignations method");
        // Retrieve all designations from the repository
        List<Designation> designations = designationRepository.findAll();
        // Map the list of Designation entities to DesignationDTOs
        return Mapper.INSTANCE.entityToDtoForDesignations(designations);
    }

    @Override
    @Transactional
    public boolean addDesignation(DesignationDTO newDesignation) {
        log.info("inside addDesignation method");
        // Validate that the designation name is not null
        if (newDesignation.getDesignationName() == null) {
            return false; // Return false if the name is null
        }
        // Create a new Designation entity and set its name
        Designation designation = new Designation();
        designation.setDesignationName(newDesignation.getDesignationName());
        // Save the new designation to the repository
        designationRepository.save(designation);
        return true; // Return true to indicate success
    }

    @Override
    @Transactional
    public boolean updateDesignation(DesignationDTO updateDesignation) {
        log.info("inside updateDesignation method");
        // Find the designation by ID and update its name if it exists
        return designationRepository.findById(updateDesignation.getDesignationId())
                .map(designation -> {
                    designation.setDesignationName(updateDesignation.getDesignationName());
                    // Save the updated designation to the repository
                    designationRepository.save(designation);
                    return true; // Return true to indicate success
                })
                .orElse(false); // Return false if the designation was not found
    }
}
