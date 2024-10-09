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
public class LeaveRecordDTO {
    private String leaveRecordId;

    private Integer totalLeaves;

    private BigDecimal usedLeaves;

    private BigDecimal remainingLeaves;

    private String leaveForEmployee;

    private String typeOfLeave;
}
