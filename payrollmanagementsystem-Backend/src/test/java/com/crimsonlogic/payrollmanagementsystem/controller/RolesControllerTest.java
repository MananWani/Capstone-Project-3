package com.crimsonlogic.payrollmanagementsystem.controller;

import java.util.Arrays;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.crimsonlogic.payrollmanagementsystem.dto.RolesDTO;
import com.crimsonlogic.payrollmanagementsystem.service.RolesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class RolesControllerTest {

    @InjectMocks
    private RolesController rolesController;

    @Mock
    private RolesService rolesService;

    private RolesDTO rolesDTO;

    @BeforeEach
    void setUp() {

        rolesDTO = new RolesDTO();
        rolesDTO.setRoleId("role-1");
        rolesDTO.setRoleName("ROLE_USER");
    }

    @Test
    void testGetAllRoles_Success() {
        List<RolesDTO> rolesList = Arrays.asList(rolesDTO);
        when(rolesService.getAllRoles()).thenReturn(rolesList);

        ResponseEntity<List<RolesDTO>> response = rolesController.getAllRoles();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody().get(0).getRoleId()).isEqualTo(rolesDTO.getRoleId());
    }

    @Test
    void testAddRole_Success() {
        when(rolesService.addRole(any(RolesDTO.class))).thenReturn(true);

        ResponseEntity<Void> response = rolesController.addRole(rolesDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void testAddRole_Failure() {
        when(rolesService.addRole(any(RolesDTO.class))).thenReturn(false);

        ResponseEntity<Void> response = rolesController.addRole(rolesDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testUpdateRole_Success() {
        when(rolesService.updateRole(any(RolesDTO.class))).thenReturn(true);

        ResponseEntity<Void> response = rolesController.updateRole(rolesDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void testUpdateRole_NotFound() {
        when(rolesService.updateRole(any(RolesDTO.class))).thenReturn(false);

        ResponseEntity<Void> response = rolesController.updateRole(rolesDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}