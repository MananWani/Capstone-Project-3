package com.crimsonlogic.payrollmanagementsystem.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SalaryRecordTest {

    private SalaryRecord salaryRecord;

    @BeforeEach
    void setUp() {
        salaryRecord = new SalaryRecord();
    }

    @Test
    void testGenerateId() {
        // Simulate the @PrePersist behavior
        salaryRecord.generateId();
        assertNotNull(salaryRecord.getSalaryRecordId());
        assertTrue(salaryRecord.getSalaryRecordId().startsWith("RCD"));
    }

    @Test
    void testSettersAndGetters() {
        LocalDate payPeriodStart = LocalDate.of(2024, 10, 1);
        salaryRecord.setPayPeriodStart(payPeriodStart);

        LocalDate payPeriodEnd = LocalDate.of(2024, 10, 31);
        salaryRecord.setPayPeriodEnd(payPeriodEnd);

        BigDecimal grossSalary = new BigDecimal("5000.00");
        salaryRecord.setGrossSalary(grossSalary);

        BigDecimal bonusAmount = new BigDecimal("500.00");
        salaryRecord.setBonusAmount(bonusAmount);

        BigDecimal taxAmount = new BigDecimal("750.00");
        salaryRecord.setTaxAmount(taxAmount);

        BigDecimal pfAmount = new BigDecimal("300.00");
        salaryRecord.setPfAmount(pfAmount);

        BigDecimal penaltyAmount = new BigDecimal("100.00");
        salaryRecord.setPenaltyAmount(penaltyAmount);

        BigDecimal netSalary = new BigDecimal("4850.00");
        salaryRecord.setNetSalary(netSalary);

        Employees employee = new Employees();
        employee.setEmployeeId("EMP123");
        salaryRecord.setSalaryRecordOfEmployee(employee);

        assertEquals(payPeriodStart, salaryRecord.getPayPeriodStart());
        assertEquals(payPeriodEnd, salaryRecord.getPayPeriodEnd());
        assertEquals(grossSalary, salaryRecord.getGrossSalary());
        assertEquals(bonusAmount, salaryRecord.getBonusAmount());
        assertEquals(taxAmount, salaryRecord.getTaxAmount());
        assertEquals(pfAmount, salaryRecord.getPfAmount());
        assertEquals(penaltyAmount, salaryRecord.getPenaltyAmount());
        assertEquals(netSalary, salaryRecord.getNetSalary());
        assertEquals(employee, salaryRecord.getSalaryRecordOfEmployee());
    }
}

