package com.crimsonlogic.payrollmanagementsystem.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;

/**
 * @author abdulmanan
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class TaxResponseDTO {

    private LocalDate payPeriodStart;

    private LocalDate payPeriodEnd;

    private BigDecimal grossSalary;

    private BigDecimal netSalary;

    private BigDecimal taxDeducted;

    private Year year;

}
