package com.crimsonlogic.payrollmanagementsystem.domain;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

class UsersTest {

    private Users user;

    @BeforeEach
    void setUp() {
        user = new Users();
    }

    @Test
    void testGenerateId() {
        user.generateId();
        assertNotNull(user.getUserId());
        assertTrue(user.getUserId().startsWith("USR"));
    }

    @Test
    void testSettersAndGetters() {
        user.setEmail("user@example.com");
        user.setPasswordHash("hashed_password");

        Roles role = new Roles();
        role.setRoleId("rol1");
        user.setRole(role);

        Timestamp now = new Timestamp(System.currentTimeMillis());
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        assertEquals("user@example.com", user.getEmail());
        assertEquals("hashed_password", user.getPasswordHash());
        assertEquals(role, user.getRole());
        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
    }
}
