package com.crimsonlogic.payrollmanagementsystem.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

import java.util.Collections;
import java.util.List;

import com.crimsonlogic.payrollmanagementsystem.domain.Employees;
import com.crimsonlogic.payrollmanagementsystem.dto.LoginResponseDTO;
import com.crimsonlogic.payrollmanagementsystem.dto.UsersDTO;
import com.crimsonlogic.payrollmanagementsystem.exception.IncorrectPasswordException;
import com.crimsonlogic.payrollmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.payrollmanagementsystem.service.UsersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class UsersControllerTest {

    @InjectMocks
    private UsersController usersController;

    @Mock
    private UsersService usersService;

    private UsersDTO usersDTO;

    private LoginResponseDTO loginResponse;

    @BeforeEach
    void setUp() {

        Employees employee = new Employees();
        employee.setEmployeeId("emp1");

        usersDTO = new UsersDTO();
        usersDTO.setEmail("test@example.com");
        usersDTO.setPasswordHash("hashedPassword");
        usersDTO.setRole("USER");

        loginResponse = new LoginResponseDTO();
        loginResponse.setEmployeeId(employee.getEmployeeId());
        loginResponse.setRole("USER");
    }

    @Test
    void testLogin_Success() throws ResourceNotFoundException {
        when(usersService.authenticateUser(any(String.class), any(String.class))).thenReturn(loginResponse);

        ResponseEntity<Object> response = usersController.login(usersDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(loginResponse);
    }

    @Test
    void testLogin_UserNotFound() throws ResourceNotFoundException {
        when(usersService.authenticateUser(any(String.class), any(String.class)))
                .thenThrow(new ResourceNotFoundException("User not found"));

        ResponseEntity<Object> response = usersController.login(usersDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("User not found");
    }

    @Test
    void testGetUserRole_Success() {
        when(usersService.getUserRole()).thenReturn(Collections.singletonList(usersDTO));

        ResponseEntity<List<UsersDTO>> response = usersController.getUserRole();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    void testUpdateRole_Success() {
        when(usersService.updateRole(usersDTO)).thenReturn(true);

        ResponseEntity<Void> response = usersController.updateRole(usersDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void testUpdateRole_UserNotFound() {
        when(usersService.updateRole(usersDTO)).thenReturn(false);

        ResponseEntity<Void> response = usersController.updateRole(usersDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testUpdatePassword_Success() throws IncorrectPasswordException, ResourceNotFoundException {
        doNothing().when(usersService).updatePassword(any(String.class), any(String.class), any(String.class));

        ResponseEntity<String> response = usersController.updatePassword("test@example.com", "currentPassword", "newPassword");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void testUpdatePassword_IncorrectPassword() throws IncorrectPasswordException, ResourceNotFoundException {
        doThrow(new IncorrectPasswordException("Incorrect password")).when(usersService)
                .updatePassword(any(String.class), any(String.class), any(String.class));

        ResponseEntity<String> response = usersController.updatePassword("test@example.com", "wrongPassword", "newPassword");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Incorrect password");
    }

    @Test
    void testUpdatePassword_UserNotFound() throws IncorrectPasswordException, ResourceNotFoundException {
        doThrow(new ResourceNotFoundException("User not found")).when(usersService)
                .updatePassword(any(String.class), any(String.class), any(String.class));

        ResponseEntity<String> response = usersController.updatePassword("test@example.com", "currentPassword", "newPassword");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("User not found");
    }
}

