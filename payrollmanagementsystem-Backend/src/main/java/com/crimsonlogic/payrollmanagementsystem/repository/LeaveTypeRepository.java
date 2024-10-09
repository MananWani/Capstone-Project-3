package com.crimsonlogic.payrollmanagementsystem.repository;

import com.crimsonlogic.payrollmanagementsystem.domain.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing LeaveType entities.
 * Provides CRUD operations and custom queries for leave types.
 *
 * @author abdulmanan
 */
@Repository
public interface LeaveTypeRepository extends JpaRepository<LeaveType, String> {

    /**
     * Finds a LeaveType by its name.
     *
     * @param typeName The name of the leave type to be queried.
     * @return The LeaveType associated with the given name, or null if not found.
     */
    LeaveType findByTypeName(String typeName);
}
