package com.crimsonlogic.payrollmanagementsystem.serviceimpl;

import com.crimsonlogic.payrollmanagementsystem.domain.Roles;
import com.crimsonlogic.payrollmanagementsystem.dto.RolesDTO;
import com.crimsonlogic.payrollmanagementsystem.mapper.Mapper;
import com.crimsonlogic.payrollmanagementsystem.repository.RolesRepository;
import com.crimsonlogic.payrollmanagementsystem.service.RolesService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * RolesServiceImpl implements RolesService to manage user roles within the system.
 * It provides methods to retrieve all roles, add a new role, and update an existing role.
 *
 * @author abdulmanan
 */@Slf4j

@Service
public class RolesServiceImpl implements RolesService {

    private final RolesRepository rolesRepository;

    // Constructor to initialize the RolesRepository
    public RolesServiceImpl(RolesRepository rolesRepository) {
        super();
        this.rolesRepository = rolesRepository;
    }

    @Override
    public List<RolesDTO> getAllRoles() {
        log.info("inside getAllRoles method");
        // Retrieve all roles from the repository and convert them to DTOs
        List<Roles> roles = rolesRepository.findAll();
        return Mapper.INSTANCE.entityToDtoForRoles(roles);
    }

    @Override
    @Transactional
    public Boolean addRole(RolesDTO newRole) {
        log.info("inside addRole method");
        // Check if the role name is provided; return false if not
        if (newRole.getRoleName() == null) {
            return false; // Role name is mandatory
        }

        // Create a new Roles entity and set its properties
        Roles role = new Roles();
        role.setRoleName(newRole.getRoleName());

        // Save the new role to the repository
        rolesRepository.save(role);
        return true; // Return true to indicate success
    }

    @Override
    @Transactional
    public boolean updateRole(RolesDTO updateRole) {
        log.info("inside updateRole method");
        // Find the existing role by ID and update its properties if found
        return rolesRepository.findById(updateRole.getRoleId())
                .map(role -> {
                    role.setRoleName(updateRole.getRoleName()); // Update role name
                    rolesRepository.save(role); // Save the updated role
                    return true; // Return true to indicate success
                })
                .orElse(false); // Return false if the role was not found
    }
}

