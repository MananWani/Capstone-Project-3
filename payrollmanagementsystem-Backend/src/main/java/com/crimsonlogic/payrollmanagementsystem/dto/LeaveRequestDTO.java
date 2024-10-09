package com.crimsonlogic.payrollmanagementsystem.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author abdulmanan
 *
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class LeaveRequestDTO {
    private String leaveRequestId;

    private LocalDate startDate;

    private String startHalf;

    private LocalDate endDate;

    private String endHalf;

    private String status;

    private String reason;

    private String description;

    private BigDecimal noOfDays;

    private String requestByEmployee;

    private String  typeOfLeave;

    private String typeId;

    private String employeeId;
}
