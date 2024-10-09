package com.crimsonlogic.payrollmanagementsystem.service;

import com.crimsonlogic.payrollmanagementsystem.dto.RolesDTO;

import java.util.List;

/**
 * Service interface for managing roles within the payroll management system.
 * Provides methods to retrieve, add, and update roles.
 *
 * @author abdulmanan
 */
public interface RolesService {

    /**
     * Retrieves all roles available in the system.
     *
     * @return a list of roles represented by RolesDTO
     */
    List<RolesDTO> getAllRoles();

    /**
     * Adds a new role to the system.
     *
     * @param newRole the data transfer object containing role details
     * @return true if the role was successfully added, false otherwise
     */
    Boolean addRole(RolesDTO newRole);

    /**
     * Updates an existing role in the system.
     *
     * @param updateRole the data transfer object containing updated role details
     * @return true if the role was successfully updated, false otherwise
     */
    boolean updateRole(RolesDTO updateRole);
}
