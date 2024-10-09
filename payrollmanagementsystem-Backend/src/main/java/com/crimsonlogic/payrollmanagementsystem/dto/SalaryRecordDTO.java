package com.crimsonlogic.payrollmanagementsystem.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author abdulmanan
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class SalaryRecordDTO {
    private String salaryRecordId;

    private LocalDate payPeriodStart;

    private LocalDate payPeriodEnd;

    private BigDecimal grossSalary;

    private BigDecimal bonusAmount;

    private BigDecimal penaltyAmount;

    private BigDecimal netSalary;

    private BigDecimal pfAmount;

    private BigDecimal taxAmount;

    private String employeeId;

    private String fullName;

    private LocalDate joiningDate;

    private String designation;
}
