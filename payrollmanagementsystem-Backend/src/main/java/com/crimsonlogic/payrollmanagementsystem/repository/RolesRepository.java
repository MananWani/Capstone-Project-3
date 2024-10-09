package com.crimsonlogic.payrollmanagementsystem.repository;

import com.crimsonlogic.payrollmanagementsystem.domain.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Roles entities.
 * Provides CRUD operations for roles within the payroll management system.
 *
 * @author abdulmanan
 */
@Repository
public interface RolesRepository extends JpaRepository<Roles, String> {
    // Additional query methods can be defined here if needed.
}
