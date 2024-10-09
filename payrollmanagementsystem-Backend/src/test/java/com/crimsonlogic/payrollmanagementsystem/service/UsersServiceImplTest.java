package com.crimsonlogic.payrollmanagementsystem.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

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
import com.crimsonlogic.payrollmanagementsystem.serviceimpl.UsersServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UsersServiceImplTest {

    @InjectMocks
    private UsersServiceImpl usersService;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private EmployeesRepository employeesRepository;

    @Mock
    private LoginLogsService loginLogsService;

    @Mock
    private RolesRepository rolesRepository;

    private Users user;

    private Users admin;

    private Employees employee;

    private Roles role;

    @BeforeEach
    void setUp() {
        user = new Users();
        user.setUserId("user1");
        user.setEmail("test@example.com");
        String userPassword = "hashPassword";
        String userHash = BCrypt.hashpw(userPassword, BCrypt.gensalt());
        user.setPasswordHash(userHash);

        admin = new Users();
        admin.setUserId("user2");
        admin.setEmail("test@example.com");
        String rawPassword = "hashPassword";
        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        admin.setPasswordHash(hashedPassword);

        employee = new Employees();
        employee.setEmployeeId("emp1");
        employee.setFullName("John Doe");
        employee.setIsActive(true);

        role = new Roles();
        role.setRoleName("User");
        user.setRole(role);

        Roles adminRole = new Roles();
        adminRole.setRoleName("Admin");
        admin.setRole(adminRole);
    }

    @Test
    void testAuthenticateUser_Success_Admin() throws ResourceNotFoundException {
        when(usersRepository.findByEmail(admin.getEmail())).thenReturn(admin);

        String rawPassword = "hashPassword";
        try (MockedStatic<BCrypt> mockedBCrypt = mockStatic(BCrypt.class)) {
            mockedBCrypt.when(() -> BCrypt.checkpw(rawPassword, admin.getPasswordHash())).thenReturn(true);

            LoginResponseDTO response = usersService.authenticateUser(admin.getEmail(), rawPassword);

            assertThat(response.getFullName()).isEqualTo(admin.getEmail());
            assertThat(response.getRole()).isEqualTo("Admin");
        }
    }


    @Test
    void testAuthenticateUser_Success_User() throws ResourceNotFoundException {
        when(usersRepository.findByEmail(user.getEmail())).thenReturn(user);
        String rawPassword = "hashPassword";
        try (MockedStatic<BCrypt> mockedBCrypt = mockStatic(BCrypt.class)) {
            mockedBCrypt.when(() -> BCrypt.checkpw(rawPassword, user.getPasswordHash())).thenReturn(true);
            when(employeesRepository.findByUser(user)).thenReturn(employee);
            when(loginLogsService.setLoginLog(employee)).thenReturn("logId");

            LoginResponseDTO response = usersService.authenticateUser(user.getEmail(), rawPassword);

            assertThat(response.getEmployeeId()).isEqualTo(employee.getEmployeeId());
            assertThat(response.getFullName()).isEqualTo(employee.getFullName());
            assertThat(response.getRole()).isEqualTo(user.getRole().getRoleName());
            assertThat(response.getLogId()).isEqualTo("logId");
        }
    }


    @Test
    void testAuthenticateUser_InvalidCredentials() {
        when(usersRepository.findByEmail(user.getEmail())).thenReturn(user);
        assertThrows(ResourceNotFoundException.class, () -> usersService.authenticateUser(user.getEmail(), "wrongPassword"));
    }

    @Test
    void testGetUserRole() {
        when(usersRepository.findAll()).thenReturn(Collections.singletonList(user));
        when(employeesRepository.findFullNameByUser(user)).thenReturn(employee.getFullName());

        List<UsersDTO> roles = usersService.getUserRole();

        assertThat(roles)
                .isNotNull()
                .hasSize(1);
        assertThat(roles.get(0).getFullName()).isEqualTo(employee.getFullName());
    }

    @Test
    void testUpdateRole_Success() {
        when(usersRepository.findById(anyString())).thenReturn(Optional.of(user));
        when(rolesRepository.findById(anyString())).thenReturn(Optional.of(role));

        boolean updated = usersService.updateRole(new UsersDTO("user1", "User"));

        assertThat(updated).isTrue();
        verify(usersRepository).save(user);
    }

    @Test
    void testUpdateRole_UserNotFound() {
        when(usersRepository.findById(anyString())).thenReturn(Optional.empty());

        boolean updated = usersService.updateRole(new UsersDTO("user1", "User"));

        assertThat(updated).isFalse();
        verify(usersRepository, never()).save(any(Users.class));
    }

    @Test
    void testUpdatePassword_Success() throws IncorrectPasswordException, ResourceNotFoundException {
        when(usersRepository.findByEmail(user.getEmail())).thenReturn(user);
        when(usersRepository.save(user)).thenReturn(user); // Mock save
        try (MockedStatic<BCrypt> mockedBCrypt = mockStatic(BCrypt.class)) {
            mockedBCrypt.when(() -> BCrypt.checkpw("hashPassword", user.getPasswordHash())).thenReturn(true);
            usersService.updatePassword(user.getEmail(), "hashPassword", "newPassword");
            user.setPasswordHash("newPassword");
            assertThat(user.getPasswordHash()).isNotEqualTo("hashPassword");
            verify(usersRepository).save(user);
        }
    }


    @Test
    void testUpdatePassword_UserNotFound() {
        when(usersRepository.findByEmail(anyString())).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> usersService.updatePassword(user.getEmail(), user.getPasswordHash(), "newPassword"));
    }

    @Test
    void testUpdatePassword_IncorrectCurrentPassword() {
        when(usersRepository.findByEmail(anyString())).thenReturn(user);

        assertThrows(IncorrectPasswordException.class, () -> usersService.updatePassword(user.getEmail(), "wrongPassword", "newPassword"));
    }
}

