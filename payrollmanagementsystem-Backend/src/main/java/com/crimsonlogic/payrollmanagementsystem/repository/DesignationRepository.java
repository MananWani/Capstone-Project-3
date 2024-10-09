package com.crimsonlogic.payrollmanagementsystem.repository;

import com.crimsonlogic.payrollmanagementsystem.domain.Designation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Designation entities.
 * Provides CRUD operations for Designation records.
 *
 * @author abdulmanan
 */
@Repository
public interface DesignationRepository extends JpaRepository<Designation, String> {
}
