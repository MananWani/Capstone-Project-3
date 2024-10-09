package com.crimsonlogic.payrollmanagementsystem.controller;

import com.crimsonlogic.payrollmanagementsystem.dto.RolesDTO;
import com.crimsonlogic.payrollmanagementsystem.service.RolesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/*
  Controller for managing roles within the payroll management system.
  This includes retrieving all roles, adding new roles, and updating existing roles.

  @author abdulmanan
 */
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/roles")
public class RolesController {

    private final RolesService rolesService;

    /**
     * Constructs a RolesController with the specified RolesService.
     *
     * @param rolesService the service used to handle role operations
     */
    public RolesController(RolesService rolesService) {
        super();
        this.rolesService = rolesService;
    }

    /**
     * Retrieves all roles in the system.
     *
     * @return a ResponseEntity containing a list of RolesDTOs
     */
    @GetMapping("/getallroles")
    public ResponseEntity<List<RolesDTO>> getAllRoles() {
        List<RolesDTO> roles = rolesService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    /**
     * Adds a new role based on the provided RolesDTO.
     *
     * @param newRole the DTO containing the details of the role to be added
     * @return a ResponseEntity indicating the result of the addition operation
     */
    @PostMapping("/addrole")
    public ResponseEntity<Void> addRole(@RequestBody RolesDTO newRole) {
        Boolean isAdded = rolesService.addRole(newRole);
        if (Boolean.FALSE.equals(isAdded)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(201).build();
    }

    /**
     * Updates an existing role based on the provided RolesDTO.
     *
     * @param updateRole the DTO containing the updated details of the role
     * @return a ResponseEntity indicating the result of the update operation
     */
    @PostMapping("/updaterole")
    public ResponseEntity<Void> updateRole(@RequestBody RolesDTO updateRole) {
        boolean isUpdated = rolesService.updateRole(updateRole);

        if (!isUpdated) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.status(201).build();
    }
}
