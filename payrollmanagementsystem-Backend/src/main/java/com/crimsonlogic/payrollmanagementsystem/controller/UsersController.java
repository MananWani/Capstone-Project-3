package com.crimsonlogic.payrollmanagementsystem.controller;

import com.crimsonlogic.payrollmanagementsystem.dto.LoginResponseDTO;
import com.crimsonlogic.payrollmanagementsystem.dto.UsersDTO;
import com.crimsonlogic.payrollmanagementsystem.exception.IncorrectPasswordException;
import com.crimsonlogic.payrollmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.payrollmanagementsystem.service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/*
  Controller for managing user operations within the payroll management system.
  This includes user authentication, role management, and password updates.

  @author abdulmanan
 */
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/users")
public class UsersController {
    private final UsersService usersService;

    /**
     * Constructs a UsersController with the specified UsersService.
     *
     * @param usersService the service used to handle user operations
     */
    public UsersController(UsersService usersService) {
        super();
        this.usersService = usersService;
    }

    /**
     * Authenticates a user based on the provided email and password.
     *
     * @param usersDTO the DTO containing user credentials for login
     * @return a ResponseEntity containing the login response information
     */
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody UsersDTO usersDTO) {
        try {
            LoginResponseDTO loginResponse = usersService.authenticateUser(usersDTO.getEmail(), usersDTO.getPasswordHash());
            return ResponseEntity.ok(loginResponse);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Retrieves the roles of all users in the system.
     *
     * @return a ResponseEntity containing a list of UsersDTO with user roles
     */
    @GetMapping("/getuserrole")
    public ResponseEntity<List<UsersDTO>> getUserRole() {
        List<UsersDTO> usersDTO = usersService.getUserRole();
        return ResponseEntity.ok(usersDTO);
    }

    /**
     * Updates the role of a specified user.
     *
     * @param usersDTO the DTO containing user information for role update
     * @return a ResponseEntity indicating the result of the update operation
     */
    @PostMapping("/updaterole")
    public ResponseEntity<Void> updateRole(@RequestBody UsersDTO usersDTO) {
        boolean isUpdated = usersService.updateRole(usersDTO);

        if (!isUpdated) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.status(201).build();
    }

    /**
     * Updates the password for a specified user.
     *
     * @param email          the email of the user
     * @param currentPassword the current password of the user
     * @param newPassword     the new password to be set
     * @return a ResponseEntity indicating the result of the password update operation
     */
    @PostMapping("/updatepassword")
    public ResponseEntity<String> updatePassword(@RequestParam String email,
                                                 @RequestParam String currentPassword,
                                                 @RequestParam String newPassword) {
        try {
            usersService.updatePassword(email, currentPassword, newPassword);
            return ResponseEntity.status(201).build();
        } catch (IncorrectPasswordException | ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
