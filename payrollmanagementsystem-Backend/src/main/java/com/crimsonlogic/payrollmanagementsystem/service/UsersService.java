package com.crimsonlogic.payrollmanagementsystem.service;

import com.crimsonlogic.payrollmanagementsystem.dto.LoginResponseDTO;
import com.crimsonlogic.payrollmanagementsystem.dto.UsersDTO;
import com.crimsonlogic.payrollmanagementsystem.exception.IncorrectPasswordException;
import com.crimsonlogic.payrollmanagementsystem.exception.ResourceNotFoundException;

import java.util.List;

/**
 * Service interface for managing user accounts and authentication within the payroll management system.
 * Provides methods for user authentication, role management, and password updates.
 *
 * @author abdulmanan
 */
public interface UsersService {

    /**
     * Authenticates a user based on their email and password hash.
     *
     * @param email the email of the user
     * @param password the hashed password of the user
     * @return a LoginResponseDTO containing authentication details
     * @throws ResourceNotFoundException if the user is not found
     */
    LoginResponseDTO authenticateUser(String email, String password)
            throws ResourceNotFoundException;

    /**
     * Retrieves a list of user roles in the system.
     *
     * @return a list of UsersDTO containing user role information
     */
    List<UsersDTO> getUserRole();

    /**
     * Updates the role of a specified user.
     *
     * @param usersDTO the user data transfer object containing the updated role information
     * @return true if the update was successful; false otherwise
     */
    boolean updateRole(UsersDTO usersDTO);

    /**
     * Updates the password for a user after validating the current password.
     *
     * @param email the email of the user
     * @param currentPassword the user's current password
     * @param newPassword the new password to be set
     * @throws IncorrectPasswordException if the current password is incorrect
     * @throws ResourceNotFoundException if the user is not found
     */
    void updatePassword(String email, String currentPassword, String newPassword)
            throws IncorrectPasswordException, ResourceNotFoundException;
}
