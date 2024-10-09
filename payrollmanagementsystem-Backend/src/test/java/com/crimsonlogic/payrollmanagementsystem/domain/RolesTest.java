package com.crimsonlogic.payrollmanagementsystem.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
class RolesTest {

    private Roles role;

    @BeforeEach
    void setUp() {
        role = new Roles();
    }

    @Test
    void testGenerateId() {
        role.generateId();
        assertNotNull(role.getRoleId());
        assertTrue(role.getRoleId().startsWith("ROL"));
    }

    @Test
    void testSettersAndGetters() {
        String roleName = "Admin";
        role.setRoleName(roleName);

        assertEquals(roleName, role.getRoleName());
    }
}
