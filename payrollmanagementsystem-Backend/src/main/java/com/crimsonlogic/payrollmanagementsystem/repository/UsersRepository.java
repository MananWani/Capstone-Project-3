package com.crimsonlogic.payrollmanagementsystem.repository;

import com.crimsonlogic.payrollmanagementsystem.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Users entities.
 * Provides CRUD operations and custom query methods for user-related data.
 *
 * @author abdulmanan
 */
@Repository
public interface UsersRepository extends JpaRepository<Users, String> {

    /**
     * Check if a user exists by their email.
     *
     * @param email the email to check
     * @return true if a user with the specified email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Find a user by their email.
     *
     * @param email the email of the user
     * @return the user associated with the specified email
     */
    Users findByEmail(String email);
}
