package com.crimsonlogic.payrollmanagementsystem.serviceimpl;

import com.crimsonlogic.payrollmanagementsystem.domain.Employees;
import com.crimsonlogic.payrollmanagementsystem.domain.Roles;
import com.crimsonlogic.payrollmanagementsystem.domain.Users;
import com.crimsonlogic.payrollmanagementsystem.dto.LoginResponseDTO;
import com.crimsonlogic.payrollmanagementsystem.dto.UsersDTO;
import com.crimsonlogic.payrollmanagementsystem.exception.IncorrectPasswordException;
import com.crimsonlogic.payrollmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.payrollmanagementsystem.repository.EmployeesRepository;
import com.crimsonlogic.payrollmanagementsystem.repository.RolesRepository;
import com.crimsonlogic.payrollmanagementsystem.repository.UsersRepository;
import com.crimsonlogic.payrollmanagementsystem.service.LoginLogsService;
import com.crimsonlogic.payrollmanagementsystem.service.UsersService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * Service implementation for user management, including authentication,
 * role updates, and password management.
 *
 * @author abdulmanan
 */
@Service
@Slf4j
public class UsersServiceImpl implements UsersService {

    // Repositories for managing users, roles, employees, and login logs
    private final RolesRepository rolesRepository;
    private final UsersRepository usersRepository;
    private final EmployeesRepository employeesRepository;
    private final LoginLogsService loginLogsService;

    // Constructor to initialize the repositories
    public UsersServiceImpl(UsersRepository usersRepository, EmployeesRepository employeesRepository,
                            LoginLogsService loginLogsService,
                            RolesRepository rolesRepository) {
        super();
        this.usersRepository = usersRepository;
        this.employeesRepository = employeesRepository;
        this.loginLogsService = loginLogsService;
        this.rolesRepository = rolesRepository;
    }

    @Override
    @Transactional
    public LoginResponseDTO authenticateUser(String email, String password) throws ResourceNotFoundException {
        log.info("inside authenticateUser method");
        // Find user by email and password hash
        Optional<Users> optionalUser = Optional.ofNullable(usersRepository.findByEmail(email));

        // Check if the password is correct
        if (optionalUser.isEmpty() || !BCrypt.checkpw(password,optionalUser.get().getPasswordHash())) {
            throw new ResourceNotFoundException("Invalid username or password.");
        }

        LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
        Users user = optionalUser.get();

        // Check if the user is an admin
        if (user.getRole().getRoleName().equalsIgnoreCase("Admin")) {
            loginResponseDTO.setFullName(email);
            loginResponseDTO.setRole(user.getRole().getRoleName());
            return loginResponseDTO;
        }

        // Get the employee associated with the user
        Employees employee = employeesRepository.findByUser(user);
        String logId = loginLogsService.setLoginLog(employee);

        if (employee == null || !employee.getIsActive()) {
            throw new ResourceNotFoundException("Account is inactive.");
        }

        // Set user details in the login response
        loginResponseDTO.setEmployeeId(employee.getEmployeeId());
        loginResponseDTO.setFullName(employee.getFullName());
        loginResponseDTO.setRole(user.getRole().getRoleName());
        loginResponseDTO.setLogId(logId);

        return loginResponseDTO; // Return the login response DTO
    }

    /**
     * Hashes the password using BCrypt.
     *
     * @param password the password to hash
     * @return the hashed password
     */
    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    @Override
    public List<UsersDTO> getUserRole() {
        log.info("inside getUserRole method");
        // Prepare a list to hold user roles
        List<UsersDTO> returnList = new ArrayList<>();
        List<Users> usersList = usersRepository.findAll(); // Get all users

        // Populate the return list with user roles and names
        usersList.forEach(user -> {
            String fullName = employeesRepository.findFullNameByUser(user);
            UsersDTO usersDTO = new UsersDTO();
            usersDTO.setUserId(user.getUserId());
            usersDTO.setRole(user.getRole().getRoleName());
            usersDTO.setFullName(fullName);
            returnList.add(usersDTO);
        });

        return returnList; // Return the list of user roles
    }

    @Override
    @Transactional
    public boolean updateRole(UsersDTO usersDTO) {
        log.info("inside updateRole method");
        // Find user and role by their IDs
        Users user = usersRepository.findById(usersDTO.getUserId()).orElse(null);
        Roles role = rolesRepository.findById(usersDTO.getRole()).orElse(null);

        if (user != null && role != null) {
            user.setRole(role); // Update the user's role
            usersRepository.save(user); // Save the updated user
            return true; // Indicate success
        }

        return false; // Return false if user or role not found
    }

    @Override
    @Transactional
    public void updatePassword(String email, String currentPassword, String newPassword)
            throws IncorrectPasswordException, ResourceNotFoundException {
        log.info("inside updatePassword method");
        // Find user by email
        Users user = usersRepository.findByEmail(email);

        if (user == null) {
            throw new ResourceNotFoundException("Unexpected error, please try again.");
        }

        // Check if the current password matches
        if (!BCrypt.checkpw(currentPassword,user.getPasswordHash())){
            throw new IncorrectPasswordException("Current password is incorrect.");
        }

        // Update the password and timestamp
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        user.setPasswordHash(hashPassword(newPassword));
        user.setUpdatedAt(now);
        usersRepository.save(user); // Save the updated user
    }
}

