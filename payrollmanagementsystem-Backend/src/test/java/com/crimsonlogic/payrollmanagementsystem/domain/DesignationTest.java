package com.crimsonlogic.payrollmanagementsystem.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DesignationTest {

    private Designation designation;

    @BeforeEach
    void setUp() {
        designation = new Designation();
    }

    @Test
    void testGenerateId() {
        designation.generateId();
        assertNotNull(designation.getDesignationId());
        assertTrue(designation.getDesignationId().startsWith("DSG"));
    }

    @Test
    void testSettersAndGetters() {
        String designationName = "Software Engineer";
        designation.setDesignationName(designationName);

        assertEquals(designationName, designation.getDesignationName());
    }
}

