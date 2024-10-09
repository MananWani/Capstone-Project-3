package com.crimsonlogic.payrollmanagementsystem.service;

import com.crimsonlogic.payrollmanagementsystem.dto.DesignationDTO;

import java.util.List;

/**
 * Service interface for managing employee designations.
 * Provides methods to retrieve, add, and update designations.
 *
 * @author abdulmanan
 */
public interface DesignationService {

    /**
     * Retrieve a list of all designations.
     *
     * @return a list of DesignationDTO representing all designations
     */
    List<DesignationDTO> getAllDesignations();

    /**
     * Add a new designation.
     *
     * @param newDesignation the designation details to be added
     * @return true if the designation was added successfully, false otherwise
     */
    boolean addDesignation(DesignationDTO newDesignation);

    /**
     * Update an existing designation.
     *
     * @param updateDesignation the updated designation details
     * @return true if the designation was updated successfully, false otherwise
     */
    boolean updateDesignation(DesignationDTO updateDesignation);
}
