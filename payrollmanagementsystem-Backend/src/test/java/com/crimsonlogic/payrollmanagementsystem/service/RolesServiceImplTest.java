package com.crimsonlogic.payrollmanagementsystem.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.crimsonlogic.payrollmanagementsystem.domain.Roles;
import com.crimsonlogic.payrollmanagementsystem.dto.RolesDTO;
import com.crimsonlogic.payrollmanagementsystem.repository.RolesRepository;
import com.crimsonlogic.payrollmanagementsystem.serviceimpl.RolesServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RolesServiceImplTest {

    @InjectMocks
    private RolesServiceImpl rolesService;

    @Mock
    private RolesRepository rolesRepository;

    private Roles role;

    private RolesDTO roleDTO;

    @BeforeEach
    void setUp() {
        role = new Roles();
        role.setRoleId("role1");
        role.setRoleName("ROLE_USER");

        roleDTO = new RolesDTO();
        roleDTO.setRoleId("role1");
        roleDTO.setRoleName("ROLE_ADMIN");
    }

    @Test
    void testGetAllRoles() {
        when(rolesRepository.findAll()).thenReturn(Collections.singletonList(role));

        List<RolesDTO> rolesList = rolesService.getAllRoles();

        assertThat(rolesList)
                .isNotNull()
                .hasSize(1);
        assertThat(rolesList.get(0).getRoleName()).isEqualTo("ROLE_USER");
        verify(rolesRepository).findAll();
    }

    @Test
    void testAddRole_Success() {
        roleDTO.setRoleName("ROLE_NEW");

        boolean result = rolesService.addRole(roleDTO);

        assertThat(result).isTrue();
        verify(rolesRepository).save(any(Roles.class));
    }

    @Test
    void testAddRole_RoleNameMissing() {
        roleDTO.setRoleName(null);

        boolean result = rolesService.addRole(roleDTO);

        assertThat(result).isFalse();
        verify(rolesRepository, never()).save(any(Roles.class));
    }

    @Test
    void testUpdateRole_Success() {
        when(rolesRepository.findById(role.getRoleId())).thenReturn(Optional.of(role));

        boolean result = rolesService.updateRole(roleDTO);

        assertThat(result).isTrue();
        assertThat(role.getRoleName()).isEqualTo(roleDTO.getRoleName());
        verify(rolesRepository).save(role);
    }

    @Test
    void testUpdateRole_RoleNotFound() {
        when(rolesRepository.findById(anyString())).thenReturn(Optional.empty());

        boolean result = rolesService.updateRole(roleDTO);

        assertThat(result).isFalse();
        verify(rolesRepository, never()).save(any(Roles.class));
    }
}

