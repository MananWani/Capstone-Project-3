package com.crimsonlogic.payrollmanagementsystem.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author abdulmanan
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class SalaryDTO {
    private String salaryId;

    private BigDecimal costToCompany;

    private String fullName;
}
